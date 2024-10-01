package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

// tests that the values in the timeout exception are populated correctly
@EnabledIf(LinuxCondition::class)
class TestTimeoutExceptionTest : FunSpec() {
   init {

      timeout = 250

      test("timeout exception should use the value that caused the test to fail")
         .config(timeout = 23.milliseconds) {
            delay(100.milliseconds)
         }

      aroundTest { (test, execute) ->
         val result = execute(test)
         result.errorOrNull?.message shouldBe "Test 'timeout exception should use the value that caused the test to fail' did not complete within 23ms"
         TestResult.Success(0.milliseconds)
      }
   }
}
