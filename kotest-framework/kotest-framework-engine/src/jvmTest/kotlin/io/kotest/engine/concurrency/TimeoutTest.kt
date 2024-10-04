package io.kotest.engine.concurrency

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

@EnabledIf(LinuxCondition::class)
class TimeoutTest : FunSpec({
   test("detection with blocking job") {
      shouldThrow<TimeoutCancellationException> {
         withTimeout(50) {
            launch {
               Thread.sleep(2000)
            }
         }
         println("no timeout detected")
      }
   }

   test("detection with non-blocking job") {
      shouldThrow<TimeoutCancellationException> {
         withTimeout(50) {
            launch {
               delay(400)
            }
         }
         println("no timeout detected")
      }
   }
})
