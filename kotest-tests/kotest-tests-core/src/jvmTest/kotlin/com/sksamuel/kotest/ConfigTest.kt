@file:Suppress("OverridingDeprecatedMember")

package com.sksamuel.kotest

import io.kotest.Tag
import io.kotest.assertions.fail
import io.kotest.core.TestCaseConfig
import io.kotest.provided.ProjectConfig
import io.kotest.shouldBe
import io.kotest.specs.WordSpec
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@UseExperimental(ExperimentalTime::class)
class ConfigTest : WordSpec() {

  object TagZ : Tag()

  override val defaultTestCaseConfig: TestCaseConfig = TestCaseConfig(invocations = 3, tags = setOf(TagZ))

  private val invocationCounter = AtomicInteger(0)
  private val invocationCounter2 = AtomicInteger(0)
  private val threadCounter = AtomicInteger(0)

  init {

    "TestCase config" should {
      "support invocation parameter".config(invocations = 5) {
        // this test should run 5 times
        invocationCounter.incrementAndGet()
      }

      "support ignored".config(enabled = false) {
        fail("shouldn't run")
      }

      // If we have 100 threads, and each one sleeps for 100 milliseconds, then the total time
      // should still be approx 100 ms as each of our threads will block for 100ms.
      // So we set the timeout an order of magnitude higher to account for a bit of thread
      // context switching and it should never hit.
      "support threads parameter".config(timeout = 2000.milliseconds, threads = 100, invocations = 100) {
        // this test should not timeout
        Thread.sleep(100)
        threadCounter.incrementAndGet()
      }

      "use default config" {
        invocationCounter2.incrementAndGet()
      }

      "override only actually set values".config(invocations = 2, threads = 4) {
        //  testCase.config.invocations shouldBe 2
        // testCase.config.threads shouldBe 4
        //  testCase.config.tags shouldBe setOf(TagZ)
      }

      "use default config, if no test case config is given".config(invocations = 1) {

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


