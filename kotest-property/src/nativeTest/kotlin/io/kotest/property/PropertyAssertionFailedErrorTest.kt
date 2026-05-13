package io.kotest.property

import io.kotest.assertions.KotestAssertionFailedError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.int
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class PropertyAssertionFailedErrorTest {

   @Test
   fun propagatesExpectedAndActualFromCause() {
      runBlocking {
         val error = shouldThrow<KotestAssertionFailedError> {
            checkAll(Arb.int(0..0)) { input ->
               input.shouldBeEqual(input + 1)
            }
         }
         // expected/actual should be propagated from the underlying matcher failure
         // so tooling/TeamCity service messages can render a diff for the outer error
         error.expected shouldBe "1"
         error.actual shouldBe "0"
      }
   }

   @Test
   fun doesNotSetExpectedAndActualWhenCauseHasNone() {
      runBlocking {
         val error = shouldThrow<AssertionError> {
            checkAll(Arb.int(0..0)) {
               error("boom")
            }
         }
         // when the underlying failure isn't a KotestAssertionFailedError we fall back to a plain AssertionError
         (error is KotestAssertionFailedError) shouldBe false
      }
   }
}
