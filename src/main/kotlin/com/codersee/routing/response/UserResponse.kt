package com.codersee.routing.response

import com.codersee.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UserResponse(
  @Serializable(with = UUIDSerializer::class)
  val id: UUID,
  val username: String,
)