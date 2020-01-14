@file:Suppress("OverridingDeprecatedMember")

package com.sksamuel.kotest

import io.kotest.core.Tag
import io.kotest.assertions.fail
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.TestCaseConfig
import io.kotest.provided.ProjectConfig
import io.kotest.shouldBe
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@UseExperimental(ExperimentalTime::class)
class ConfigTest : WordSpec() {

   object TagZ : Tag()

   override fun defaultTestCaseConfig(): TestCaseConfig? = TestCaseConfig(invocations = 3, tags = setOf(TagZ))

   private val invocationCounter = AtomicInteger(0)
   private val invocationCounter2 = AtomicInteger(0)
   private val threadCounter = AtomicInteger(0)

   init {

      "TestCase config" should {
         "support ignored".config(enabled = false) {
            fail("shouldn't run")
         }

         "use default config" {
            invocationCounter2.incrementAndGet()
         }

         "override only actually set values".config(enabled = false) {
         }

         "use default config, if no test case config is given".config(timeout = 1152.milliseconds) {

            //  testCase.config.invocations shouldBe 3
            //   testCase.config.threads shouldBe 1
            //   testCase.config.tags shouldBe setOf(TagZ)
         }

         "only run beforeAll once" {
            ProjectConfig.beforeAll shouldBe 1
         }

         "only run afterAll once" {
            // this test spec has not yet completed, and therefore this count should be 0
            // we will also assert this in another test suite, where it should still be 0
            // but at that point at least _one_ test suite will have completed
            // so that will confirm it is not being fired after every spec
            ProjectConfig.afterAll shouldBe 0
         }
      }
   }
}


