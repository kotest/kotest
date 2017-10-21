package io.kotlintest

import io.kotlintest.matchers.*
import io.kotlintest.provided.ProjectConfig
import io.kotlintest.specs.WordSpec
import java.util.concurrent.atomic.AtomicInteger

class ConfigTest : WordSpec() {

  object TagA : Tag()

  private val testCaseInterceptorLog: ThreadLocal<StringBuilder>? = object : ThreadLocal<StringBuilder>() {
    override fun initialValue() = StringBuilder()
  }

  private val verificationInterceptor: (Spec, () -> Unit) -> Unit = { _, spec ->
    spec()
    val expectedLog = "A1.B1.C1.D1.E1.F1.test call.F2.E2.D2.C2."
    ProjectConfig.intercepterLog.toString() shouldEqual expectedLog
  }

  private val specInterceptorA: (Spec, () -> Unit) -> Unit = { _, spec ->
    ProjectConfig.intercepterLog.append("C1.")
    spec()
    ProjectConfig.intercepterLog.append("C2.")
  }

  private val specInterceptorB: (Spec, () -> Unit) -> Unit = { _, spec ->
    ProjectConfig.intercepterLog.append("D1.")
    spec()
    ProjectConfig.intercepterLog.append("D2.")
  }

  private val testCaseinterceptorC: (TestCaseContext, () -> Unit) -> Unit = { _, testCase ->
    testCaseInterceptorLog!!.get().append("E1.")
    testCase()
    testCaseInterceptorLog.get().append("E2.")
  }

  private val testCaseInterceptorD: (TestCaseContext, () -> Unit) -> Unit = { _, testCase ->
    testCaseInterceptorLog!!.get().append("F1.")
    testCase()
    testCaseInterceptorLog.get().append("F2.")
  }

  private val testCaseInterceptorE = { _: TestCaseContext, testCase: () -> Unit ->
    try {
      testCase()
    } catch (ex: RuntimeException) {
      // ignore
    }
  }

  private val testCaseInterceptors = listOf(testCaseinterceptorC, testCaseInterceptorD, testCaseInterceptorE)

  override val defaultTestCaseConfig: TestCaseConfig =
      TestCaseConfig(
          invocations = 3,
          tags = setOf(TagA),
          interceptors = testCaseInterceptors)

  override val specInterceptors = listOf(verificationInterceptor, specInterceptorA, specInterceptorB)

  override val oneInstancePerTest = false

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
      }.config(timeout = 10000.milliseconds, threads = 100, invocations = 100)

      "use default config" {
        invocationCounter2.incrementAndGet()
      }

      "override only actually set values" {
        val testCase = "some test case" {}
        testCase.config(invocations = 2, threads = 4)

        testCase.config.invocations shouldBe 2
        testCase.config.threads shouldBe 4
        testCase.config.tags shouldEqual setOf(TagA)
      }

      "use default config, if no test case config is given" {
        val testCase = "some test case" {}

        testCase.config.invocations shouldBe 3
        testCase.config.threads shouldBe 1
        testCase.config.tags shouldEqual setOf(TagA)
      }.config(invocations = 1)

      val orderVerificationInterceptor: (TestCaseContext, () -> Unit) -> Unit = { _, testCase ->
        testCase()
        ProjectConfig.intercepterLog.append(testCaseInterceptorLog!!.get().toString())
      }

      "should call interceptors in order of definition" {
        testCaseInterceptorLog!!.get().append("test call.")
      }.config(invocations = 1, interceptors = listOf(orderVerificationInterceptor) + testCaseInterceptors)

      "should handle exception with interceptor" {
        throw RuntimeException()
      }.config(invocations = 1)

      "should override interceptors" {
        testCaseInterceptorLog!!.get().toString() should haveLength(0)
      }.config(interceptors = listOf())

      "only run beforeAll once" {
        ProjectConfig.beforeAll shouldBe 1
      }

      "only run afterAll once" {
        // this test spec has not yet completed, and therefore this count should be 0
        // we will also assert this in another test suite, where it should still be 0
        // but at that point at least _one_ test suite will have completed
        ProjectConfig.afterAll shouldBe 0
      }
    }
  }

  override fun interceptSpec(chain: () -> Unit) {
    chain()

    invocationCounter.get() shouldBe 5
    invocationCounter2.get() shouldBe 3
    threadCounter.get() shouldBe 100
  }
}


