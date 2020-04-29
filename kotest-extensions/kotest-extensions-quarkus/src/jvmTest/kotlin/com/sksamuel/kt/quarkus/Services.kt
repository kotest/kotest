package com.sksamuel.kt.quarkus

import io.quarkus.test.Mock
import javax.enterprise.context.ApplicationScoped

data class User(val name: String)

interface UserRepository {
   fun findUser(): User
}

@ApplicationScoped
class DefaultRepository : UserRepository {
   override fun findUser(): User = User("system_user")
}

@ApplicationScoped
class UserService(val repository: DefaultRepository)

@ApplicationScoped
class MockableService {
   fun greet() = "Hello"
}

@Mock
@ApplicationScoped
class MockedService : MockableService() {
   override fun greet() = "Welcome"
}
