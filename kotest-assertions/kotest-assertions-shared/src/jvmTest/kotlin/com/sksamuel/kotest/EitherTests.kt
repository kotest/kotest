package com.sksamuel.kotest

import io.kotest.assertions.*
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainOnlyOnce

private fun matcherState() = Pair(errorCollector.errors(), assertionCounter.get())

@Isolate
@OptIn(ExperimentalKotest::class)
class EitherTests : FunSpec({
   test("either adds errors and assertions to their respective trackers on failure") {
      var beforeState: Pair<List<Throwable>, Int>? = null
      var afterState: Pair<List<Throwable>, Int>? = null

      shouldFail {
         all {
            beforeState = matcherState()

            either { // error 3
               1 shouldBe 2 // error 1
               2 shouldBe 3 // error 2
            }

            afterState = matcherState()
         }
      }

      withClue("an assertion is added for each of the matchers") {
         val before = beforeState?.second.shouldNotBeNull()
         val after = afterState?.second.shouldNotBeNull()

         after - before shouldBe 2
      }

      withClue("throwables are added to the error collector for the matchers and the either") {
         val before = beforeState?.first.shouldNotBeNull()
         val after = afterState?.first.shouldNotBeNull()

         after.size - before.size shouldBe 3
      }
   }

   test("either restores the error tracker and sets the assertion tracker correctly on success") {
      val (beforeErrors, beforeAssertions) = matcherState()

      either {
         "a" shouldBe "b"
         "a" shouldBe "a"
      }

      val (afterErrors, afterAssertions) = matcherState()

      withClue("the assertions counter should be updated with how many assertions were used in either") {
         afterAssertions shouldBe beforeAssertions + 2
      }

      withClue("since the either was successful there should be no new errors in the error tracker") {
         afterErrors shouldContainAll beforeErrors
      }
   }

   test("either fails if less than two assertions are executed") {
      val (_, beforeAssertions) = matcherState()
      val message = shouldFail {
         either { "a" shouldBe "a" }
      }.message

      withClue("either should maintain the assertion that was executed in the counter") {
         assertionCounter.get() shouldBe beforeAssertions + 2
      }

      message shouldContainOnlyOnce  "Either cannot ensure a mutual exclusion with less than two assertions"
   }

   test("either fails if more than one assertion succeeds") {
      shouldFail {
         either {
            "a" shouldBe "a"
            "b" shouldBe "b"
         }
      }.message shouldContainOnlyOnce "Either expected a single assertion to succeed, but more than one succeeded."
   }

   test("either fails if all assertions fail") {
      shouldFail {
         either {
            "a" shouldBe "b"
            "b" shouldBe "c"
         }
      }.message shouldContainOnlyOnce "Either expected a single assertion to succeed, but none succeeded."
   }

   test("either succeeds when a single assertion succeeds and many fail") {
      either {
         (0..9).forEach { it shouldBe it + 1 }
         1 shouldBe 1
      }
   }
})
