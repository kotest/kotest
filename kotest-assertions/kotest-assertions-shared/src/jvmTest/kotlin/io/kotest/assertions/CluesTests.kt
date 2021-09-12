package io.kotest.assertions

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class CluesTests : FunSpec({
   test("withClue should not fail on coroutine thread switch") {
      withContext(Dispatchers.Unconfined) {
         val threadIds = mutableSetOf<Long>()
         withClue("should not fail") {
            threadIds.add(Thread.currentThread().id)
            delay(10)
            threadIds.add(Thread.currentThread().id)
         }
         threadIds shouldHaveSize 2
      }
   }
})
