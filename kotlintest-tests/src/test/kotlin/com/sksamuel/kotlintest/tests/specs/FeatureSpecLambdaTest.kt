package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.shouldBe
import io.kotlintest.specs.FeatureSpec

class FeatureSpecLambdaTest : FeatureSpec() {

  data class User(var email: String? = "")

  var user: User? = null

  init {

    feature("user") {
      user = User(null)

      and("set valid email") {
        user!!.email = "sam@kotlintest.io"

        scenario("should get correct email") {
          user!!.email shouldBe "sam@kotlintest.io"
        }
      }

      and("set null email") {
        user!!.email = null

        scenario("should get null email") {
          user!!.email shouldBe null
        }
      }
    }

    feature("null") {
      user = null

      scenario("user should be null") {
        user shouldBe null
      }

      and("setting user to not null") {
        user = User("sam@kotlintest.io")

        scenario("should get correct email") {
          user!!.email shouldBe "sam@kotlintest.io"
        }
      }
    }
  }
}