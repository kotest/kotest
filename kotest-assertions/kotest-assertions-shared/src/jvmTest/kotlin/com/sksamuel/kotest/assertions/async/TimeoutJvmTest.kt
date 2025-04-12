package com.sksamuel.kotest.assertions.async

import io.kotest.assertions.async.shouldTimeout
import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.common.testTimeSource
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime
import kotlin.time.measureTimedValue
import kotlin.time.toJavaDuration

@EnabledIf(LinuxOnlyGithubCondition::class)
class TimeoutJvmTest : FunSpec({

   coroutineTestScope = true

   setOf(
      InvokeShouldTimeout.WithMillis,
      InvokeShouldTimeout.WithJavaDuration,
   ).forEach { shouldTimeoutWrapper ->

      test("${shouldTimeoutWrapper.description} - should not throw any if operation did not complete in given time") {
         val testDuration = testTimeSource().measureTime {
            shouldNotThrowAny {
               shouldTimeoutWrapper(1.seconds) {
                  delay(1.1.seconds)
               }
            }
         }
         testDuration shouldBe 1.seconds
      }

      test("${shouldTimeoutWrapper.description} - should fail if operation completes within given time") {
         val (failure, testDuration) = testTimeSource().measureTimedValue {
            shouldFail {
               shouldTimeoutWrapper(1.seconds) {
                  delay(0.1.seconds)
               }
            }
         }
         failure.message shouldContain "Operation completed too quickly. Expected that operation completed faster than 1s, but it took 100ms."
         testDuration shouldBe 0.1.seconds
      }
   }
})

/**
 * Utility class to wrap [shouldTimeout] so that it can be called with a Kotlin [Duration],
 * but it will convert the duration to a Java-specific overload.
 *
 * The Kotlin duration is tested in commonTest [com.sksamuel.kotest.assertions.async.TimeoutTest]
 */
private sealed class InvokeShouldTimeout {
   abstract val description: String
   abstract suspend operator fun invoke(timeout: Duration, operation: suspend () -> Unit)

   data object WithJavaDuration : InvokeShouldTimeout() {
      override val description: String = "shouldTimeout (with Java duration)"
      override suspend fun invoke(timeout: Duration, operation: suspend () -> Unit) {
         @Suppress("DEPRECATION")
         shouldTimeout(duration = timeout.toJavaDuration(), thunk = operation)
      }
   }

   data object WithMillis : InvokeShouldTimeout() {
      override val description: String = "shouldTimeout (with TimeUnit)"
      override suspend fun invoke(timeout: Duration, operation: suspend () -> Unit) {
         @Suppress("DEPRECATION")
         shouldTimeout(timeout = timeout.inWholeMilliseconds, unit = TimeUnit.MILLISECONDS, thunk = operation)
      }
   }
}
