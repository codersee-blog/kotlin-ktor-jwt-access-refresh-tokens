package com.codersee

import com.codersee.plugins.configureSecurity
import com.codersee.plugins.configureSerialization
import com.codersee.repository.RefreshTokenRepository
import com.codersee.repository.UserRepository
import com.codersee.routing.configureRouting
import com.codersee.service.JwtService
import com.codersee.service.UserService
import io.ktor.server.application.*

fun main(args: Array<String>) {
  io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
  val userRepository = UserRepository()
  val refreshTokenRepository = RefreshTokenRepository()
  val jwtService = JwtService(this, userRepository)
  val userService = UserService(userRepository, jwtService, refreshTokenRepository)

  configureSerialization()
  configureSecurity(jwtService)
  configureRouting(userService)
}
