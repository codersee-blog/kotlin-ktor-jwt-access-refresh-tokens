package com.codersee.routing.request

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequest(
  val token: String,
)