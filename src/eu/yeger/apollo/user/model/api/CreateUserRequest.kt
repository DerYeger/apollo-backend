package eu.yeger.apollo.user.model.api

import eu.yeger.apollo.user.model.domain.User
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
public data class CreateUserRequest(
  val name: String,
  val password: String
)

public fun CreateUserRequest.toUser(): User {
  return User(
    id = UUID.randomUUID().toString(),
    name = name,
    password = password
  )
}
