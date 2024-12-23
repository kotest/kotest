package io.kotest.assertions.nondeterministic

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlin.time.Duration.Companion.seconds

class DoNotInterceptErrorsTest: StringSpec() {
   init {
      "does not retry after Error" {
         mapOf(
            "OutOfMemoryError" to OutOfMemoryError(),
            "StackOverflowError" to StackOverflowError(),
         ).entries.forEach {  (name, error) ->
            var count = 0
            val thrown = shouldThrow<Error> {
               eventually(1.seconds) {
                  count++
                  throw error
               }
            }
            assertSoftly {
               thrown::class shouldBe AssertionError::class
               thrown.message shouldContain name
               count shouldBe 1
            }
         }
      }
   }
}
