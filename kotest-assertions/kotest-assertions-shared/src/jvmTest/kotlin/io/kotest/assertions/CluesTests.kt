package io.kotest.assertions

import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class CluesTests : FunSpec({
   test("withClue should not fail on coroutine thread switch") {
      withContext(Dispatchers.Unconfined) {
         withClue("should not fail") {
            Thread.currentThread().run { println("withClue block begins on $name, id $id") }
            delay(10)
            Thread.currentThread().run { println("withClue block ends on $name, id $id") }
         }
      }
   }
})
