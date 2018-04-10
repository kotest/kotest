@file:Suppress("OverridingDeprecatedMember")

package com.sksamuel.kotlintest.tests

import io.kotlintest.Tag
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestResult
import io.kotlintest.extensions.SpecExtension
import io.kotlintest.extensions.SpecInterceptContext
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.extensions.TestCaseInterceptContext
import io.kotlintest.fail
import io.kotlintest.matchers.haveLength
import io.kotlintest.provided.ProjectConfig
import io.kotlintest.shouldBe
import io.kotlintest.shouldHave
import io.kotlintest.specs.WordSpec
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

class ConfigTest : WordSpec() {

  object TagA : Tag()

  private val testCaseInterceptorLog: ThreadLocal<StringBuilder>? = object : ThreadLocal<StringBuilder>() {
    override fun initialValue() = StringBuilder()
  }

  private val verificationInterceptor = object : SpecExtension {
    override fun intercept(context: SpecInterceptContext, process: () -> Unit) {
      process()
      val expectedLog = "A1.B1.C1.D1.E1.F1.test call.F2.E2.D2.C2."
      ProjectConfig.intercepterLog.toString() shouldBe expectedLog
    }
  }

  private val specInterceptorA = object : SpecExtension {
    override fun intercept(context: SpecInterceptContext, process: () -> Unit) {
      ProjectConfig.intercepterLog.append("C1.")
      process()
      ProjectConfig.intercepterLog.append("C2.")
    }
  }

  private val specInterceptorB = object : SpecExtension {
    override fun intercept(context: SpecInterceptContext, process: () -> Unit) {
      ProjectConfig.intercepterLog.append("D1.")
      process()
      ProjectConfig.intercepterLog.append("D2.")
    }
  }

  private val testCaseinterceptorC = object : TestCaseExtension {
    override fun intercept(context: TestCaseInterceptContext,
                           test: (TestCaseConfig, (TestResult) -> Unit) -> Unit,
                           complete: (TestResult) -> Unit) {
      testCaseInterceptorLog!!.get().append("E1.")
      test(context.config, {
        testCaseInterceptorLog.get().append("E2.")
        complete(it)
      })
    }
  }

  private val testCaseInterceptorD = object : TestCaseExtension {
    override fun intercept(context: TestCaseInterceptContext,
                           test: (TestCaseConfig, (TestResult) -> Unit) -> Unit,
                           complete: (TestResult) -> Unit) {
      testCaseInterceptorLog!!.get().append("F1.")
      test(context.config, {
        testCaseInterceptorLog.get().append("F2.")
        complete(it)
      })
    }
  }

  private val testCaseInterceptorE = object : TestCaseExtension {
    override fun intercept(context: TestCaseInterceptContext,
                           test: (TestCaseConfig, (TestResult) -> Unit) -> Unit,
                           complete: (TestResult) -> Unit) {
      return try {
        test(context.config, { complete(it) })
      } catch (ex: RuntimeException) {
        // ignore
        complete(TestResult.Ignored)
      }
    }
  }

  private val testCaseInterceptors = listOf(testCaseinterceptorC, testCaseInterceptorD, testCaseInterceptorE)

  override val defaultTestCaseConfig: TestCaseConfig =
      TestCaseConfig(
          invocations = 3,
          tags = setOf(TagA),
          extensions = testCaseInterceptors)

  override fun extensions() = listOf(verificationInterceptor, specInterceptorA, specInterceptorB)

  private val invocationCounter = AtomicInteger(0)
  private val invocationCounter2 = AtomicInteger(0)
  private val threadCounter = AtomicInteger(0)

  init {
    "TestCase config" should {
      "support invocation parameter" {
        // this test should run 5 times
        invocationCounter.incrementAndGet()
      }.config(invocations = 5)

      "support ignored" {
        fail("shouldn't run")
      }.config(enabled = false)

      // If we have 100 threads, and each one sleeps for 1000 milliseconds, then the total time
      // should still be approx 1000 ms. So we set the timeout an order of magnitude higher, and it
      // should never hit.
      "support threads parameter" {
        // this test should timeout
        Thread.sleep(1000)
        threadCounter.incrementAndGet()
      }.config(timeout = Duration.ofMillis(10000), threads = 100, invocations = 100)

      "use default config" {
        invocationCounter2.incrementAndGet()
      }

      "override only actually set values" {
        val testCase = "some test case" {}
        testCase.config(invocations = 2, threads = 4)

        testCase.config.invocations shouldBe 2
        testCase.config.threads shouldBe 4
        testCase.config.tags shouldBe setOf(TagA)
      }

      "use default config, if no test case config is given" {
        val testCase = "some test case" {}

        testCase.config.invocations shouldBe 3
        testCase.config.threads shouldBe 1
        testCase.config.tags shouldBe setOf(TagA)
      }.config(invocations = 1)

      val orderVerificationInterceptor = object : TestCaseExtension {
        override fun intercept(context: TestCaseInterceptContext,
                               test: (TestCaseConfig, (TestResult) -> Unit) -> Unit,
                               complete: (TestResult) -> Unit) {
          test(context.config, {
            ProjectConfig.intercepterLog.append(testCaseInterceptorLog!!.get().toString())
            complete(it)
          })
        }
      }

      "should call interceptors in order of definition" {
        testCaseInterceptorLog!!.get().append("test call.")
      }.config(invocations = 1, extensions = listOf(orderVerificationInterceptor) + testCaseInterceptors)

      "should override interceptors" {
        testCaseInterceptorLog!!.get().toString() shouldHave haveLength(0)
      }.config(extensions = listOf())

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


