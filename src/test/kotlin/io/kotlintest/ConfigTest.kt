package io.kotlintest

import io.kotlintest.specs.WordSpec
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import io.kotlintest.Duration.Companion.milliseconds

class ConfigTest : WordSpec() {

  override val defaultTestCaseConfig: TestConfig = config(invocations = 3)

  val invocationCounter = AtomicInteger(0)
  val invocationCounter2 = AtomicInteger(0)
  val threadCounter = AtomicInteger(0)
  val singleTagShouldHaveRun = AtomicBoolean(false)
  val multiTagShouldHaveRun = AtomicBoolean(false)

  init {
    "TestCase config" should {
      "support invocation parameter" {
        // this test should run 5 times
        invocationCounter.incrementAndGet()
      }.config(invocations = 5)

      "support ignored" {
        fail("shouldn't run")
      }.config(ignored = true)

      System.setProperty("testTags", "bibble,fibble,foo")

      "support single tag" {
        singleTagShouldHaveRun.set(true)
      }.config(tag = "foo", invocations = 1)

      "support multiple tags" {
        multiTagShouldHaveRun.set(true)
      }.config(tags = listOf("foo", "boo"), invocations = 1)

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
    }
  }

  override fun afterAll(): Unit {
    invocationCounter.get() shouldBe 5
    invocationCounter2.get() shouldBe 3
    threadCounter.get() shouldBe 100
    singleTagShouldHaveRun.get() shouldBe true
    multiTagShouldHaveRun.get() shouldBe true
  }
}