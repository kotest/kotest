package io.kotest.assertions.nondeterministic

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class ContinuallyTest : FunSpec() {

   init {
      test("pass tests that succeed for the entire duration") {
         continually(500.milliseconds) {
            (System.currentTimeMillis() > 0) shouldBe true
         }
      }

      test("pass tests with null values") {
         val test = continually(500.milliseconds) {
            null shouldBe null
         }
      }

      test("use interval function") {
         val config = continuallyConfig<Unit> {
            duration = 700.milliseconds
            intervalFn = DurationFn { 300.milliseconds }
         }
         var k = 0
         continually(config) {
            (System.currentTimeMillis() > 0) shouldBe true
            k++
         }
         k shouldBe 3
      }

      test("invoke the listener for each successfull inovcation") {
         var listened = 0
         var invoked = 0
         val config = continuallyConfig<Unit> {
            duration = 500.milliseconds
            listener = { _, _ -> listened++ }
         }
         continually(config) {
            (System.currentTimeMillis() > 0) shouldBe true
            invoked++
         }
         invoked shouldBe listened
      }

      test("fail broken tests immediately") {
         shouldThrowAny {
            continually(12.hours) {
               false shouldBe true
            }
         }
      }

      test("fail should throw the underlying error") {
         shouldThrowExactly<AssertionError> {
            continually(12.hours) {
               throw AssertionError("boom")
            }
         }.message shouldBe "boom"
      }

      test("fail tests start off as passing then fail within the period") {
         var n = 0
         val e = shouldThrow<Throwable> {
            continually(3.seconds) {
               delay(10)
               (n++ < 10) shouldBe true
            }
         }
         val r =
            "Test failed after \\d+ms; expected to pass for 3000ms; attempted 100 times\nUnderlying failure was: 100 should be < 100".toRegex()
         e.message?.matches(r) ?: (false shouldBe true)
      }
   }
}
