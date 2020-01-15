@file:Suppress("OverridingDeprecatedMember")

package com.sksamuel.kotest

import io.kotest.assertions.fail
import io.kotest.core.Tag
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.TestCaseConfig
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.shouldBe
import io.kotest.shouldThrow
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@UseExperimental(ExperimentalTime::class)
class ConfigTest : WordSpec() {

   object TagZ : Tag()

   override fun defaultTestCaseConfig(): TestCaseConfig? =
      TestCaseConfig(tags = setOf(TagZ), timeout = 6000.milliseconds)

   private val invocationCounter = AtomicInteger(0)
   private val threadCounter = AtomicInteger(0)

   init {

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

         shouldThrow<TimeoutCancellationException> {
            "support overriding timeout".config(timeout = 250.milliseconds) {
               delay(500)
            }
         }

         "override only changed values".config(tags = setOf()) {
            testCase.config.timeout shouldBe 6000.milliseconds
         }
      }
   }
}


