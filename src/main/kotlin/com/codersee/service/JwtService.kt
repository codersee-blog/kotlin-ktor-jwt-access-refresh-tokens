package com.codersee.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.codersee.model.User
import com.codersee.repository.UserRepository
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import java.util.*

class JwtService(
  private val application: Application,
  private val userRepository: UserRepository,
) {

  private val secret = getConfigProperty("jwt.secret")
  private val issuer = getConfigProperty("jwt.issuer")
  private val audience = getConfigProperty("jwt.audience")

  val realm = getConfigProperty("jwt.realm")

  val jwtVerifier: JWTVerifier =
    JWT
      .require(Algorithm.HMAC256(secret))
      .withAudience(audience)
      .withIssuer(issuer)
      .build()

  fun createAccessToken(username: String): String =
    createJwtToken(username, 3_600_000)

  fun createRefreshToken(username: String): String =
    createJwtToken(username, 86_400_000)

  private fun createJwtToken(username: String, expireIn: Int): String =
    JWT.create()
      .withAudience(audience)
      .withIssuer(issuer)
      .withClaim("username", username)
      .withExpiresAt(Date(System.currentTimeMillis() + expireIn))
      .sign(Algorithm.HMAC256(secret))


  fun customValidator(
    credential: JWTCredential,
  ): JWTPrincipal? {
    val username: String? = extractUsername(credential)
    val foundUser: User? = username?.let(userRepository::findByUsername)

    return foundUser?.let {
      if (audienceMatches(credential))
        JWTPrincipal(credential.payload)
      else
        null
    }
  }

  private fun audienceMatches(
    credential: JWTCredential,
  ): Boolean =
    credential.payload.audience.contains(audience)

  fun audienceMatches(
    audience: String
  ): Boolean =
    this.audience == audience

  private fun getConfigProperty(path: String) =
    application.environment.config.property(path).getString()

  private fun extractUsername(credential: JWTCredential): String? =
    credential.payload.getClaim("username").asString()
}


