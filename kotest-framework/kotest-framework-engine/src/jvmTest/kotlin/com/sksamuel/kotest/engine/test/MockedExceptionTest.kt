package com.sksamuel.kotest.engine.test

import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.common.stacktrace.stacktraces
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk

@EnabledIf(LinuxOnlyGithubCondition::class)
class MockedExceptionTest : FunSpec() {
   init {
      test("stacktraces should not error on mocked exception") {
         AssertionErrorBuilder.create().withMessage("foo").withCause(mockk<ArithmeticException>()).build().message shouldBe "foo"
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
