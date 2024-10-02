package com.sksamuel.kotest

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe

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

@EnabledIf(LinuxCondition::class)
class BeforeTestThreadsTest : FunSpec() {

   override suspend fun beforeTest(testCase: TestCase) {
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
