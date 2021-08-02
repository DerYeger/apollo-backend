package eu.yeger.apollo.user.model.api

import eu.yeger.apollo.user.model.domain.User
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
public data class UpdateUserRequest(
  val id: String,
  val name: String,
  val password: String
)

public fun UpdateUserRequest.toUser(): User {
  return User(
    id = id,
    name = name,
    password = password
  )
}
