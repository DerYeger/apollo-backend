package eu.yeger.apollo.user.model.api

import kotlinx.serialization.Serializable

@Serializable
public data class Credentials(
  val username: String,
  val password: String
)
