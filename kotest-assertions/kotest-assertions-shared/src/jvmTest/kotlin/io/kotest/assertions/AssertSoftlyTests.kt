package io.kotest.assertions

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class AssertSoftlyTests : FunSpec({
   test("assertSoftly should collect errors across multiple coroutine threads") {
      withContext(Dispatchers.Unconfined) {
         val threadIds = mutableSetOf<Long>()
         shouldThrowExactly<MultiAssertionError> {
            assertSoftly {
               Thread.currentThread().run {
                  threadIds.add(Thread.currentThread().id)
                  "assertSoftly block begins on $name, id $id" shouldBe "collected failure"
               }
               delay(50)
               Thread.currentThread().run {
                  threadIds.add(Thread.currentThread().id)
                  "assertSoftly block ends on $name, id $id" shouldBe "collected failure"
               }
            }
         }
         threadIds shouldHaveSize 2
      }
   }
})
