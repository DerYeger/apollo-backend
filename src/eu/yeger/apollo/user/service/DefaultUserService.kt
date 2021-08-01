package eu.yeger.apollo.user.service

import com.github.michaelbull.result.*
import eu.yeger.apollo.Arguments
import eu.yeger.apollo.JWTConfiguration
import eu.yeger.apollo.matches
import eu.yeger.apollo.shared.model.api.*
import eu.yeger.apollo.user.model.api.ApiUser
import eu.yeger.apollo.user.model.api.Credentials
import eu.yeger.apollo.user.model.domain.User
import eu.yeger.apollo.user.model.domain.toApiUser
import eu.yeger.apollo.user.model.domain.toPersistentUser
import eu.yeger.apollo.user.model.persistence.PersistentUser
import eu.yeger.apollo.user.model.persistence.toApiUser
import eu.yeger.apollo.user.model.persistence.toDomainUser
import eu.yeger.apollo.user.repository.UserRepository
import eu.yeger.apollo.utils.toResult
import eu.yeger.apollo.withHashedPassword
import mu.KotlinLogging
import java.util.*

private val logger = KotlinLogging.logger {}

private val loginFailed: ResponseEntity<TranslationDTO> = unauthorized(TranslationDTO("login.error.credentials"))

public class DefaultUserService(
  private val userRepository: UserRepository
) : UserService {
  public override suspend fun getAll(): ApiResult<List<ApiUser>> {
    return userRepository
      .getAll()
      .map(PersistentUser::toApiUser)
      .toResult(::ok)
  }

  public override suspend fun getById(id: String): ApiResult<ApiUser> {
    return userRepository
      .validateUserWithIdExists(id)
      .map(PersistentUser::toApiUser)
      .map(::ok)
  }

  public override suspend fun create(user: User): ApiResult<ApiUser> {
    return userRepository
      .validateUserIdIsAvailable(user.id)
      .andThen { userRepository.validateUserNameIsAvailable(user.name) }
      .map { user.withHashedPassword() }
      .onSuccess { hashedUser -> userRepository.save(hashedUser.toPersistentUser()) }
      .map { hashedUser -> hashedUser.toApiUser() }
      .map(::created)
  }

  public override suspend fun update(user: User): ApiResult<ApiUser> {
    return userRepository
      .validateUpdateIsPossible(user)
      .andThen(User::validatePassword)
      .map { user.withHashedPassword() }
      .onSuccess { hashedUser -> userRepository.save(hashedUser.toPersistentUser()) }
      .map { hashedUser -> hashedUser.toApiUser() }
      .map(::ok)
  }

  public override suspend fun deleteById(id: String): ApiResult<Unit> {
    return userRepository
      .validateUserWithIdExists(id)
      .onSuccess { user ->
        userRepository.deleteById(user.id)
        if (userRepository.isEmpty()) {
          createDefaultUserIfRequired()
        }
      }
      .map { ok(Unit) }
  }

  public override suspend fun createDefaultUserIfRequired() {
    val username = Arguments.defaultUsername
    val password = Arguments.defaultPassword
    if (userRepository.isEmpty()) {
      logger.debug { "No existing users" }
      val user = User(id = UUID.randomUUID().toString(), name = username, password = password).withHashedPassword().toPersistentUser()
      userRepository.save(user)
      logger.info { "Created default user ${user.name}" }
    } else {
      logger.debug { "Found existing users" }
    }
  }

  public override suspend fun login(credentials: Credentials): ApiResult<ApiToken> {
    return userRepository
      .validateUserWithNameExists(credentials.username)
      .map(PersistentUser::toDomainUser)
      .andThen { user -> credentials.validateForUser(user) }
      .map(JWTConfiguration::makeToken)
      .mapError { loginFailed }
      .map(::ok)
  }
}

private fun User.validatePassword(): IntermediateResult<User> {
  return when {
    password.isNotBlank() && password.length >= 10 -> Ok(this)
    else -> Err(unprocessableEntity(TranslationDTO("api.error.user.password-too-short")))
  }
}

private fun Credentials.validateForUser(user: User): IntermediateResult<User> {
  return when (this matches user) {
    true -> Ok(user)
    false -> Err(loginFailed)
  }
}
