package com.sksamuel.kotest.autoscan

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.AutoScan
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class AutoScanTestListenerTest : WordSpec({
   "@AutoScan TestListeners" should {
      "be detected for all tests" {
         MyTestListener.beforeCounter.get() shouldBe 2
         MyTestListener.afterCounter.get() shouldBe 0
      }
      "even this one!" {
         MyTestListener.beforeCounter.get() shouldBe 3
         MyTestListener.afterCounter.get() shouldBe 1
      }
      MyTestListener.beforeCounter.get() shouldBe 3
      MyTestListener.afterCounter.get() shouldBe 2
   }
})

@AutoScan
object MyTestListener : TestListener {

   val beforeCounter = AtomicInteger(0)
   val afterCounter = AtomicInteger(0)

   override suspend fun beforeTest(testCase: TestCase) {
      if (testCase.spec::class.java.name == AutoScanTestListenerTest::class.java.name)
         beforeCounter.incrementAndGet()
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      if (testCase.spec::class.java.name == AutoScanTestListenerTest::class.java.name)
         afterCounter.incrementAndGet()
   }
}
