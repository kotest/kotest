@file:Suppress("RETURN_VALUE_NOT_USED_COERCION")

package io.kotest.permutations.errors

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.permutations.permutations

/**
 * https://github.com/kotest/kotest/issues/6234
 */
@OptIn(ExperimentalKotest::class)
class FailureCauseTest : FunSpec() {
   init {

      test("the error thrown by a failing check should be kept as the cause") {
         val error = IllegalStateException("boom")
         val failure = shouldThrow<AssertionError> {
            permutations {
               iterations = 1
               check {
                  throw error
               }
            }
         }
         failure.message.shouldNotBeNull() shouldContain "Caused by IllegalStateException: boom"
         failure.cause shouldBeSameInstanceAs error
      }

      test("the cause should keep the stack trace of the failing check") {
         val failure = shouldThrow<AssertionError> {
            permutations {
               iterations = 1
               check {
                  failingHelper()
               }
            }
         }
         val cause = failure.cause.shouldNotBeNull()
         cause.stackTrace.any { it.methodName == "failingHelper" } shouldBe true
      }
   }
}

private fun failingHelper(): Nothing = throw UnsupportedOperationException("not here")
