package com.codersee.repository

import com.codersee.model.User
import java.util.*

class UserRepository {

  private val users = mutableListOf<User>()

  fun findAll(): List<User> =
    users

  fun findById(id: UUID): User? =
    users.firstOrNull { it.id == id }

  fun findByUsername(username: String): User? =
    users.firstOrNull { it.username == username }

  fun save(user: User): Boolean =
    users.add(user)
}