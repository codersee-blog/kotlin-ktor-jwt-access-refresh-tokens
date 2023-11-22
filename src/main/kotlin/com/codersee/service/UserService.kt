package com.codersee.service

import com.auth0.jwt.interfaces.DecodedJWT
import com.codersee.model.User
import com.codersee.repository.RefreshTokenRepository
import com.codersee.repository.UserRepository
import com.codersee.routing.request.LoginRequest
import com.codersee.routing.response.AuthResponse
import java.util.*

class UserService(
  private val userRepository: UserRepository,
  private val jwtService: JwtService,
  private val refreshTokenRepository: RefreshTokenRepository
) {

  fun findAll(): List<User> =
    userRepository.findAll()

  fun findById(id: String): User? =
    userRepository.findById(
      id = UUID.fromString(id)
    )

  fun findByUsername(username: String): User? =
    userRepository.findByUsername(username)

  fun save(user: User): User? {
    val foundUser = userRepository.findByUsername(user.username)

    return if (foundUser == null) {
      userRepository.save(user)
      user
    } else null
  }

  fun authenticate(loginRequest: LoginRequest): AuthResponse? {
    val username = loginRequest.username
    val foundUser: User? = userRepository.findByUsername(username)

    return if (foundUser != null && loginRequest.password == foundUser.password) {
      val accessToken = jwtService.createAccessToken(username)
      val refreshToken = jwtService.createRefreshToken(username)

      refreshTokenRepository.save(refreshToken, username)

      AuthResponse(
        accessToken = accessToken,
        refreshToken = refreshToken,
      )
    } else
      null
  }

  fun refreshToken(token: String): String? {
    val decodedRefreshToken = verifyRefreshToken(token)
    val persistedUsername = refreshTokenRepository.findUsernameByToken(token)

    return if (decodedRefreshToken != null && persistedUsername != null) {
      val foundUser: User? = userRepository.findByUsername(persistedUsername)
      val usernameFromRefreshToken: String? = decodedRefreshToken.getClaim("username").asString()

      if (foundUser != null && usernameFromRefreshToken == foundUser.username)
        jwtService.createAccessToken(persistedUsername)
      else
        null
    } else
      null
  }

  private fun verifyRefreshToken(token: String): DecodedJWT? {
    val decodedJwt: DecodedJWT? = getDecodedJwt(token)

    return decodedJwt?.let {
      val audienceMatches = jwtService.audienceMatches(it.audience.first())

      if (audienceMatches)
        decodedJwt
      else
        null
    }
  }

  private fun getDecodedJwt(token: String): DecodedJWT? =
    try {
      jwtService.jwtVerifier.verify(token)
    } catch (ex: Exception) {
      null
    }
}