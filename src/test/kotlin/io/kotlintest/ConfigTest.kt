package io.kotlintest

import io.kotlintest.specs.WordSpec
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class ConfigTest : WordSpec() {

  val invocationCounter = AtomicInteger(0)
  val theadCounter = AtomicInteger(0)
  val singleTagShouldHaveRun = AtomicBoolean(false)
  val multiTagShouldHaveRun = AtomicBoolean(false)

  init {
    "TestCase config" should {
      "support invocation parameter" {
        // this test should run 5 times
        invocationCounter.incrementAndGet()
      }.config(invocations = 5)

      "support ignored" {
        System.exit(1)
      }.config(ignored = true)

      System.setProperty("testTags", "bibble,fibble,foo")

      "support single tag" {
        singleTagShouldHaveRun.set(true)
      }.config(tag = "foo")

      "support multiple tags" {
        multiTagShouldHaveRun.set(true)
      }.config(tags = listOf("foo", "boo"))

      // if we have 100 threads, and each one sleeps for 1000 seconds, then the total time should still be
      // approx 1000. So we set the timeout an order of magnitude higher, and it should never hit
      "support threads parameter" {
        // this test should timeout
        Thread.sleep(1000)
        theadCounter.incrementAndGet()
      }.config(timeout = 10000, threads = 100, invocations = 100)
    }
  }

  override fun afterAll(): Unit {
    if (invocationCounter.get() != 5)
      throw RuntimeException()
    if (theadCounter.get() != 100)
      throw RuntimeException()
    if (singleTagShouldHaveRun.get() == false)
      throw RuntimeException()
    if (multiTagShouldHaveRun.get() == false)
      throw RuntimeException()
  }
}