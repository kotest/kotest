package com.sksamuel.kotest.matchers

import com.sksamuel.kotest.throwablehandling.catchThrowable
import io.kotest.assertions.shouldFail
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

@EnabledIf(LinuxOnlyGithubCondition::class)
class ShouldFailTest : FreeSpec({

   "shouldFail" - {
      "Should throw an exception when code succeeds" {
         val t = catchThrowable { shouldFail { /* Code succeeds */ } }
         t.shouldBeInstanceOf<AssertionError>()
         t.message shouldBe "Expected exception java.lang.AssertionError but no exception was thrown."
      }

      "Should throw an exception when code throws something other than an assertion error" {
         val t = catchThrowable { shouldFail { throw Exception() } }
         t.shouldBeInstanceOf<AssertionError>()
         t.message shouldBe "Expected exception java.lang.AssertionError but a Exception was thrown instead."
      }

      "Should not thrown an exception when code fails with an assertion error" {
         val t = catchThrowable { shouldFail { throw AssertionError() } }
         t shouldBe null
      }
   }
})

private class FooTest : FunSpec() {
   init {
      test("foo") {
         "a" shouldBe "b"
      }
//      error("foo")
//      throw JvmAssertionFailedError("foo", null, "a", "b")
   }
}
