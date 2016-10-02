package io.kotlintest

import io.kotlintest.Duration.Companion.milliseconds
import io.kotlintest.specs.WordSpec
import java.util.concurrent.atomic.AtomicInteger

class ConfigTest : WordSpec() {

  object TagA : Tag()

  override val defaultTestCaseConfig: TestConfig =
      config(
          invocations = 3,
          tag = TagA,
          interceptors = listOf(::interceptorA, ::interceptorB, ::interceptorC))

  override val interceptors = listOf(::specInterceptorA, ::specInterceptorB)

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

      // if we have 100 threads, and each one sleeps for 1000 seconds, then the total time should still be
      // approx 1000. So we set the timeout an order of magnitude higher, and it should never hit
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

      "should handle exception with interceptor" {
        throw RuntimeException()
      }.config(invocations = 1)

      "should override interceptors" {
        // TODO test, that no interceptor has been called
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

fun specInterceptorA(context: TestBase, spec: () -> Unit) {
  println("A before spec")
  spec()
  println("A after spec")
}


fun specInterceptorB(context: TestBase, spec: () -> Unit) {
  println("B before spec")
  spec()
  println("B after spec")
}

fun interceptorA(context: TestCaseContext, testCase: () -> Unit) {
  println("A") // TODO replace with assertion
  testCase()
}


fun interceptorB(context: TestCaseContext, testCase: () -> Unit) {
  println("B") // TODO replace with assertion
  testCase()
}


fun interceptorC(context: TestCaseContext, testCase: () -> Unit) {
  try {
    testCase()
  } catch (ex: RuntimeException) {
    println("caught") // TODO replace with assertion
  }
}
