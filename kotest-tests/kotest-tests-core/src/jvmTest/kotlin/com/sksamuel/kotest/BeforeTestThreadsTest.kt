package com.sksamuel.kotest

import io.kotest.TestCase
import io.kotest.shouldBe
import io.kotest.specs.FunSpec

class ThreadLocalHolder {

  companion object {
    val threadLocal: ThreadLocal<String> = ThreadLocal()
  }

  val greeting: String
    get() {
      println("app " + Thread.currentThread().name)
      return threadLocal.get()
    }
}

class BeforeTestThreadsTest : FunSpec() {

  override fun beforeTest(testCase: TestCase) {
    println("spec " + Thread.currentThread().name)
    ThreadLocalHolder.threadLocal.set("test")
  }

  init {
    test("when threads == 1 listeners should run on the same thread as the test") {
      println("greetings " + Thread.currentThread().name)
      ThreadLocalHolder().greeting shouldBe "test"
    }
  }
}