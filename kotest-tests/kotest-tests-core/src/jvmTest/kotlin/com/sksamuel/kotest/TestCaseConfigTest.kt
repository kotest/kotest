@file:Suppress("OverridingDeprecatedMember")

package com.sksamuel.kotest

import io.kotest.assertions.fail
import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import java.lang.RuntimeException
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@UseExperimental(ExperimentalTime::class)
class TestCaseConfigTest : WordSpec() {

   object TagZ : Tag()

   override fun defaultTestCaseConfig(): TestCaseConfig? =
      TestCaseConfig(tags = setOf(TagZ), timeout = 6000.milliseconds)

   private val invocationCounter = AtomicInteger(0)
   private val threadCounter = AtomicInteger(0)

   init {

      extensions(object : TestCaseExtension {
         override suspend fun intercept(
            testCase: TestCase,
            execute: suspend (TestCase, suspend (TestResult) -> Unit) -> Unit,
            complete: suspend (TestResult) -> Unit
         ) {
            execute(testCase) {
               when (testCase.name) {
                  "support overriding timeout" -> {
                     when (it.status) {
                        TestStatus.Failure, TestStatus.Error -> when (it.error) {
                           is java.util.concurrent.TimeoutException -> complete(TestResult.success(Duration.ZERO))
                           else -> complete(
                              TestResult.error(
                                 RuntimeException("Expected test to fail on timeout but was ${it.error}"),
                                 Duration.ZERO
                              )
                           )
                        }
                        else -> complete(
                           TestResult.error(
                              RuntimeException("Expected test to fail on timeout"),
                              Duration.ZERO
                           )
                        )
                     }
                  }
                  else -> complete(it)
               }
            }
         }
      })

      "TestCase config" should {

         "use default config" {
            testCase.config.tags shouldBe setOf(TagZ)
            testCase.config.timeout shouldBe 6000.milliseconds
         }

         "support overriding enabled".config(enabled = false) {
            fail("shouldn't run")
         }

         "support overriding tags".config(tags = setOf()) {
            testCase.config.tags.shouldBeEmpty()
         }

         "support overriding timeout".config(timeout = 250.milliseconds) {
            delay(500)
         }

         "override only changed values".config(tags = setOf()) {
            testCase.config.timeout shouldBe 6000.milliseconds
         }
      }
   }
}


