package com.sksamuel.kotest.timeout

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlin.time.Duration


// tests that the values in the timeout exception are populated correctly
class TestTimeoutExceptionTest : FunSpec() {
   init {

      timeout = 250

      test("timeout exception should use the value that caused the test to fail").config(
         timeout = Duration.milliseconds(
            23
         )
      ) {
         delay(Duration.milliseconds(100))
      }

      aroundTest { (test, execute) ->
         val result = execute(test)
         result.errorOrNull?.message shouldBe "Test 'timeout exception should use the value that caused the test to fail' did not complete within 23ms"
         TestResult.success(0)
      }
   }
}
