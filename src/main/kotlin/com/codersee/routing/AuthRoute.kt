package com.codersee.routing

import com.codersee.routing.request.LoginRequest
import com.codersee.routing.request.RefreshTokenRequest
import com.codersee.routing.response.AuthResponse
import com.codersee.routing.response.RefreshTokenResponse
import com.codersee.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoute(userService: UserService) {

  post {
    val loginRequest = call.receive<LoginRequest>()

    val authResponse: AuthResponse? = userService.authenticate(loginRequest)

    authResponse?.let {
      call.respond(authResponse)
    } ?: call.respond(
      message = HttpStatusCode.Unauthorized
    )
  }

  post("/refresh") {
    val request = call.receive<RefreshTokenRequest>()

    val newAccessToken = userService.refreshToken(token = request.token)

    newAccessToken?.let {
      call.respond(
        RefreshTokenResponse(it)
      )
    } ?: call.respond(
      message = HttpStatusCode.Unauthorized
    )
  }

}