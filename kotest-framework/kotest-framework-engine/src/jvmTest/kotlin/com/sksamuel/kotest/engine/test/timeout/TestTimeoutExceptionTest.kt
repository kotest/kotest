package com.sksamuel.kotest.engine.test.timeout

import io.kotest.assertions.withClue
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.test.TestResult
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

// tests that the values in the timeout exception are populated correctly
@EnabledIf(LinuxOnlyGithubCondition::class)
class TestTimeoutExceptionTest : FunSpec() {
   init {

      timeout = 250

      test("timeout exception should use the value that caused the test to fail")
         .config(timeout = 23.milliseconds) {
            delay(100.milliseconds)
         }

      aroundTest { (test, execute) ->
         val result = execute(test)
         // The reported timeout must be the test-level 23ms, not the spec-level 250ms. The exact
         // wording is non-deterministic: depending on a timing race the engine surfaces either its
         // own TestTimeoutException ("... did not complete within 23ms") or the underlying coroutine
         // TimeoutCancellationException ("Timed out waiting for 23 ms"). Assert on the timeout value
         // rather than the exact message so the test is not flaky.
         val message = result.errorOrNull?.message
         withClue("actual timeout message: $message") {
            message.shouldNotBeNull()
            message shouldContain Regex("23\\s?ms")
            message shouldNotContain "250"
         }
         TestResult.Success(0.milliseconds)
      }
   }
}
