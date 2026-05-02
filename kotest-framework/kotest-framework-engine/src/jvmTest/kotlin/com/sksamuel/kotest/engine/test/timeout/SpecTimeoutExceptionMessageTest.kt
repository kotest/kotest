package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.test.TestResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

// tests that the values in the timeout exception are populated correctly
@EnabledIf(LinuxOnlyGithubCondition::class)
class SpecTimeoutExceptionMessageTest : FunSpec() {
   init {

      timeout = 21

      test("timeout exception should use the value that caused the test to fail") {
         delay(100.milliseconds)
      }

      aroundTest { (test, execute) ->
         val result = execute(test)
         val message = result.errorOrNull?.message
         message shouldNotBe null
         // Depending on which interceptor catches the cancellation first, the surfaced
         // message may be either Kotest's wrapper ("Test '...' did not complete within 21ms")
         // or the underlying kotlinx-coroutines form ("Timed out waiting for 21 ms"). Both
         // include the configured 21ms timeout, which is what this test is asserting.
         message!! shouldMatch Regex(".*21\\s?ms.*")
         TestResult.Success(0.milliseconds)
      }
   }
}
