package com.codersee.routing.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
  val accessToken: String,
  val refreshToken: String,
)
