package eu.yeger.apollo.user.repository

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.mapBoth
import eu.yeger.apollo.shared.model.api.IntermediateResult
import eu.yeger.apollo.shared.model.api.TranslationDTO
import eu.yeger.apollo.shared.model.api.conflict
import eu.yeger.apollo.shared.model.api.notFound
import eu.yeger.apollo.shared.repository.Repository
import eu.yeger.apollo.user.model.domain.User
import eu.yeger.apollo.user.model.persistence.PersistentUser

public interface UserRepository : Repository<PersistentUser> {

  public suspend fun getByName(name: String): PersistentUser?

  public suspend fun validateUserWithIdExists(userId: String): IntermediateResult<PersistentUser> {
    return when (val user = getById(userId)) {
      null -> Err(notFound(TranslationDTO("api.error.user.not-found")))
      else -> Ok(user)
    }
  }

  public suspend fun validateUserWithNameExists(userName: String): IntermediateResult<PersistentUser> {
    return when (val user = getByName(userName)) {
      null -> Err(notFound(TranslationDTO("api.error.user.not-found")))
      else -> Ok(user)
    }
  }

  public suspend fun validateUserIdIsAvailable(userId: String): IntermediateResult<Unit> {
    return validateUserWithIdExists(userId)
      .mapBoth(
        success = { Err(conflict(TranslationDTO("api.error.user.id-taken"))) },
        failure = { Ok(Unit) }
      )
  }

  public suspend fun validateUserNameIsAvailable(userName: String): IntermediateResult<Unit> {
    return validateUserWithNameExists(userName)
      .mapBoth(
        success = { Err(conflict(TranslationDTO("api.error.user.name-taken"))) },
        failure = { Ok(Unit) }
      )
  }

  public suspend fun validateUpdateIsPossible(user: User): IntermediateResult<User> {
    return validateUserWithIdExists(user.id)
      .andThen {
        val userWithSameName = validateUserWithNameExists(user.name)
        when {
          userWithSameName is Ok<PersistentUser> && userWithSameName.value.id != user.id -> Err(conflict(TranslationDTO("api.error.user.name-taken")))
          else -> Ok(user)
        }
      }
  }
}
