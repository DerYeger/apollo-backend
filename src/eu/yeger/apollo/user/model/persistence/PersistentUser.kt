package eu.yeger.apollo.user.model.persistence

import eu.yeger.apollo.shared.model.persistence.Entity
import eu.yeger.apollo.user.model.api.ApiUser
import eu.yeger.apollo.user.model.domain.User
import eu.yeger.apollo.user.model.domain.toApiUser

public data class PersistentUser(
  override val id: String,
  val name: String,
  val password: String
) : Entity

public fun PersistentUser.toDomainUser(): User {
  return User(
    id = id,
    name = name,
    password = password
  )
}

public fun PersistentUser.toApiUser(): ApiUser {
  return toDomainUser().toApiUser()
}
