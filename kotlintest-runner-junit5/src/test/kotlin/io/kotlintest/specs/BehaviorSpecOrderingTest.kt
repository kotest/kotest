package io.kotlintest.specs

import io.kotlintest.shouldBe

class BehaviorSpecOrderingTest : BehaviorSpec() {

  override val oneInstancePerTest: Boolean = false

  data class User(var email: String? = "")

  val VALID_EMAIL = "sam@kotlintest.io"

  init {
    given("user") {
      val user = User(null)

      `when`("set valid email") {
        user.email = VALID_EMAIL

        then("should get correct email") {
          user.email shouldBe VALID_EMAIL
        }
      }

      `when`("set null email") {
        user.email = null

        then("should get null email") {
          user.email shouldBe null
        }
      }
    }
  }
}