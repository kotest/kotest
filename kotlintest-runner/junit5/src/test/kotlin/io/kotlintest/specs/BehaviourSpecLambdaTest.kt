package io.kotlintest.specs

import io.kotlintest.shouldBe
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith()
class BehaviourSpecLambdaTest : AbstractBehaviorSpec() {

  data class User(var email: String? = "")

  var user: User? = null

  init {

    given("user") {
      user = User(null)

      `when`("set valid email") {
        user!!.email = "sam@kotlintest.io"

        then("should get correct email") {
          user!!.email shouldBe "sam@kotlintest.io"
        }
      }

      `when`("set null email") {
        user!!.email = null

        then("should get null email") {
          user!!.email shouldBe null
        }
      }
    }

    given("null") {
      user = null

      `when`("setting user to not null") {
        user = User("sam@kotlintest.io")

        then("should get correct email") {
          user!!.email shouldBe "sam@kotlintest.io"
        }
      }
    }
  }
}