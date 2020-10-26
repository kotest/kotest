package io.kotest.spring

import org.springframework.stereotype.Component

data class User(val name: String)

interface UserRepository {
  fun findUser(): User
}

class DefaultRepository : UserRepository {
  override fun findUser(): User = User("system_user")
}

@Component
class UserService(val repository: DefaultRepository)
