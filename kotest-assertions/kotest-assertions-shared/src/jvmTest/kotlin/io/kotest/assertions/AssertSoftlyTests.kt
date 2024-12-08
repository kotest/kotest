package io.kotest.assertions

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainInOrder
import io.kotest.matchers.string.shouldNotContain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

//@EnabledIf(LinuxCondition::class)
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
   test("adds an Exception to non-empty collection of assertion failures") {
      val thrown = shouldThrowExactly<MultiAssertionError> {
         assertSoftly {
            "first assertion" shouldBe "First Assertion"
            bespokeDivision(1, 0) shouldBe 1
         }
      }
      thrown.message.shouldContainInOrder(
         """expected:<"First Assertion"> but was:<"first assertion">""",
         "/ by zero"
      )
   }
   test("adds an Exception to an empty collection of assertion failures") {
      val thrown = shouldThrowExactly<MultiAssertionError> {
         assertSoftly {
            bespokeDivision(1, 0) shouldBe 1
            "first assertion" shouldBe "First Assertion"
         }
      }
      thrown.message.shouldContainInOrder(
         "The following assertion failed:",
         "1) / by zero",
      )
      thrown.message.shouldNotContain("""expected:<"First Assertion"> but was:<"first assertion">""")
   }
})

private fun bespokeDivision(a: Int, b: Int) = a / b
