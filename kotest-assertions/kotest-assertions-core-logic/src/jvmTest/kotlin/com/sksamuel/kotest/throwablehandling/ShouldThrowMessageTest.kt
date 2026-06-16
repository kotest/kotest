package com.sksamuel.kotest.throwablehandling

import io.kotest.assertions.throwables.shouldNotThrowMessage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowMessage
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.lang.AssertionError

class ShouldThrowMessageTest : StringSpec() {
   init {
      "it should pass when a lambda throw an exception having given message" {
         shouldThrowMessage("Mandatory parameter not present") {
            throw Exception("Mandatory parameter not present")
         }
      }
      "it should fail when a lambda throw an exception having message other than given message" {
         val exception = shouldThrow<AssertionError> {
            shouldThrowMessage("Something else") {
               throw Exception("Mandatory parameter not present")
            }
         }

         exception.localizedMessage shouldBe """Expected a throwable with message "Something else" but got a throwable with message "Mandatory parameter not present"""".trimMargin()
      }
      "it should fail when a lambda does not throw any exception" {
         val exception = shouldThrow<AssertionError> {
            shouldThrowMessage("Something else") {}
         }

         exception.localizedMessage shouldBe """Expected a throwable with message "Something else" but nothing was thrown""".trimMargin()
      }

      "it should fail when a lambda throw an exception having given message" {
         val exception = shouldThrow<AssertionError> {
            shouldNotThrowMessage("Mandatory parameter not present") {
               throw Exception("Mandatory parameter not present")
            }
         }

         exception.localizedMessage shouldBe """Expected no exception with message: "Mandatory parameter not present"
                                                |but a Exception was thrown with given message""".trimMargin()
      }

      "it should pass when a lambda throw an exception having message other than given message" {
         shouldNotThrowMessage("Something else") {
            throw Exception("Mandatory parameter not present")
         }
      }

      "it should pass when a lambda does not throw any exception" {
         shouldNotThrowMessage("Something else") {}
      }
   }
}
