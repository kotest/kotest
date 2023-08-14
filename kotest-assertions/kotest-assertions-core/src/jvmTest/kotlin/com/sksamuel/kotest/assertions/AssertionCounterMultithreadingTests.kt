package com.sksamuel.kotest.assertions

import io.kotest.assertions.assertionCounter
import io.kotest.assertions.assertionCounterContextElement
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class AssertionCounterMultithreadingTests : FunSpec({
   test("assertionCounter should work across coroutine thread switch") {
      withContext(Dispatchers.Unconfined + assertionCounterContextElement) {
         val threadIds = mutableSetOf<Long>()
         assertionCounter.inc()
         threadIds.add(Thread.currentThread().id)
         delay(10)
         assertionCounter.inc()
         threadIds.add(Thread.currentThread().id)
         assertionCounter.get() shouldBe 2
         threadIds shouldHaveSize 2
      }
   }
})
