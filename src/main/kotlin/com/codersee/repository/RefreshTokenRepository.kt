package com.codersee.repository

class RefreshTokenRepository {

  private val tokens = mutableMapOf<String, String>()

  fun findUsernameByToken(token: String): String? =
    tokens[token]

  fun save(token: String, username: String) {
    tokens[token] = username
  }

}