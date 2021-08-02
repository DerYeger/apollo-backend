package eu.yeger.apollo.user.model.api

import kotlinx.serialization.Serializable

@Serializable
public data class ApiUser(
  val id: String,
  val name: String
)
