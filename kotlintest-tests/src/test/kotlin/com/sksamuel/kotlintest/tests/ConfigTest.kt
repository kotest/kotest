@file:Suppress("OverridingDeprecatedMember")

package com.sksamuel.kotlintest.tests

import io.kotlintest.Tag
import io.kotlintest.TestCaseConfig
import io.kotlintest.fail
import io.kotlintest.provided.ProjectConfig
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

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

      // If we have 100 threads, and each one sleeps for 1000 milliseconds, then the total time
      // should still be approx 1000 ms. So we set the timeout an order of magnitude higher, and it
      // should never hit.
      "support threads parameter".config(timeout = Duration.ofMillis(10000), threads = 100, invocations = 100) {
        // this test should timeout
        Thread.sleep(1000)
        threadCounter.incrementAndGet()
      }

      "use default config" {
        invocationCounter2.incrementAndGet()
      }

      "override only actually set values" {
        val testCase = "some test case".config(invocations = 2, threads = 4) {}
        //  testCase.config.invocations shouldBe 2
        // testCase.config.threads shouldBe 4
        //  testCase.config.tags shouldBe setOf(TagZ)
      }

      "use default config, if no test case config is given" {
        val testCase = "some test case".config(invocations = 1) {}

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


