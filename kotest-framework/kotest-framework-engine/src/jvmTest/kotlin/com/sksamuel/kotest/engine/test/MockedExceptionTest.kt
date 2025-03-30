package com.sksamuel.kotest.engine.test

import io.kotest.assertions.Exceptions
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.mpp.stacktraces
import io.mockk.mockk

@EnabledIf(NotMacOnGithubCondition::class)
class MockedExceptionTest : FunSpec() {
   init {
      test("stacktraces should not error on mocked exception") {
         Exceptions.createAssertionError("foo", mockk<ArithmeticException>()).message shouldBe "foo"
      }

      test("cleanStackTrace should not error on mocked exception") {
         stacktraces.cleanStackTrace(mockk<ArithmeticException>())
      }

      test("root should not error on mocked exception") {
         stacktraces.root(mockk<ArithmeticException>())
      }

      test("throwableLocation should not error on mocked exception") {
         stacktraces.throwableLocation(mockk<ArithmeticException>()) shouldBe null
      }
   }
}
