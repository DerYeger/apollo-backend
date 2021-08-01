package eu.yeger.apollo.shared.model.api

import kotlinx.serialization.Serializable

@Serializable
public data class ApiToken(val token: String, val expiryDate: Long)
