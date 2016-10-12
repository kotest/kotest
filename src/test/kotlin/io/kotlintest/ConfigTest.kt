package io.kotlintest

import io.kotlintest.Duration.Companion.milliseconds
import io.kotlintest.specs.WordSpec
import java.util.concurrent.atomic.AtomicInteger

class ConfigTest : WordSpec() {

  object TagA : Tag()

  val testCaseInterceptorLog: ThreadLocal<StringBuilder>? = object : ThreadLocal<StringBuilder>() {
    override fun initialValue() = StringBuilder()
  }

  val verificationInterceptor: (TestBase, () -> Unit) -> Unit = { context, spec ->
    spec()
    val expectedLog = "A1.B1.C1.D1.E1.F1.test call.F2.E2.D2.C2."
    DemoConfig.intercepterLog.toString() shouldEqual expectedLog
  }

  val specInterceptorA: (TestBase, () -> Unit) -> Unit = { context, spec ->
    DemoConfig.intercepterLog.append("C1.")
    spec()
    DemoConfig.intercepterLog.append("C2.")
  }

  val specInterceptorB: (TestBase, () -> Unit) -> Unit = { context, spec ->
    DemoConfig.intercepterLog.append("D1.")
    spec()
    DemoConfig.intercepterLog.append("D2.")
  }

  val testCaseinterceptorC: (TestCaseContext, () -> Unit) -> Unit = { context, testCase ->
    testCaseInterceptorLog!!.get().append("E1.")
    testCase()
    testCaseInterceptorLog!!.get().append("E2.")
  }

  val testCaseInterceptorD: (TestCaseContext, () -> Unit) -> Unit = { context, testCase ->
    testCaseInterceptorLog!!.get().append("F1.")
    testCase()
    testCaseInterceptorLog!!.get().append("F2.")
  }

  val testCaseInterceptorE = { context: TestCaseContext, testCase: () -> Unit ->
    try {
      testCase()
    } catch (ex: RuntimeException) {
      // ignore
    }
  }

  val testCaseInterceptors = listOf(testCaseinterceptorC, testCaseInterceptorD, testCaseInterceptorE)

  override val defaultTestCaseConfig: TestConfig =
      config(
          invocations = 3,
          tag = TagA,
          interceptors = testCaseInterceptors)

  override val specInterceptors = listOf(verificationInterceptor, specInterceptorA, specInterceptorB)

  override val oneInstancePerTest = false

  val invocationCounter = AtomicInteger(0)
  val invocationCounter2 = AtomicInteger(0)
  val threadCounter = AtomicInteger(0)

  init {
    "TestCase config" should {
      "support invocation parameter" {
        // this test should run 5 times
        invocationCounter.incrementAndGet()
      }.config(invocations = 5)

      "support ignored" {
        fail("shouldn't run")
      }.config(ignored = true)

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

      val orderVerificationInterceptor: (TestCaseContext, () -> Unit) -> Unit = { context, testCase ->
        testCase()
        DemoConfig.intercepterLog.append(testCaseInterceptorLog!!.get().toString())
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
    }
  }

  override fun interceptSpec(context: TestBase, spec: () -> Unit): Unit {
    spec()

    invocationCounter.get() shouldBe 5
    invocationCounter2.get() shouldBe 3
    threadCounter.get() shouldBe 100
  }
}


