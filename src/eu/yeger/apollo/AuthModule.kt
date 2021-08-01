package eu.yeger.apollo

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import eu.yeger.apollo.shared.model.api.ApiToken
import eu.yeger.apollo.user.model.api.Credentials
import eu.yeger.apollo.user.model.domain.User
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import mu.KotlinLogging
import org.mindrot.jbcrypt.BCrypt
import java.util.*
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger { }

public fun Application.authModule() {
  authentication {
    jwt {
      verifier(JWTConfiguration.verifier)
      validate { credential ->
        if (credential.payload.audience.contains(JWTConfiguration.audience)) JWTPrincipal(credential.payload) else null
      }
    }
  }

  logger.info { "Installation complete" }
}

public object JWTConfiguration {
  private val issuer: String = Arguments.url
  public const val audience: String = "apollo"

  private val duration: Long = TimeUnit.DAYS.toMillis(30)

  private val algorithm = Algorithm.HMAC256(Arguments.jwtSecret ?: UUID.randomUUID().toString())
  public val verifier: JWTVerifier = JWT
    .require(algorithm)
    .withAudience(audience)
    .withIssuer(issuer)
    .build()

  public fun makeToken(user: User): ApiToken {
    val expiration = Date(System.currentTimeMillis() + duration)
    val token = JWT.create()
      .withSubject(user.id)
      .withAudience(audience)
      .withIssuer(issuer)
      .withExpiresAt(expiration)
      .sign(algorithm)
    return ApiToken(token, expiration.time)
  }
}

public fun User.withHashedPassword(): User {
  return this.copy(password = BCrypt.hashpw(password, BCrypt.gensalt()))
}

public infix fun Credentials.matches(user: User): Boolean {
  return this.username == user.name &&
    BCrypt.checkpw(this.password, user.password)
}
