package eu.yeger.apollo.user.model.domain

import eu.yeger.apollo.user.model.api.ApiUser
import eu.yeger.apollo.user.model.persistence.PersistentUser
import java.util.*

public data class User(
  val id: String,
  val name: String,
  val password: String
)

public fun User.toPersistentUser(): PersistentUser =
  PersistentUser(
    id = id,
    name = name,
    password = password
  )

public fun User.toApiUser(): ApiUser =
  ApiUser(
    id = id,
    name = name
  )
