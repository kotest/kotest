package com.sksamuel.kotest

import io.kotest.assertions.*
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.match
import io.kotest.matchers.string.shouldContainOnlyOnce

private fun matcherState() = Pair(errorCollector.errors(), assertionCounter.get())

@Isolate
@OptIn(ExperimentalKotest::class)
class OneTests : FunSpec({
   test("either fails if less than two assertions are executed") {
      val (_, beforeAssertions) = matcherState()
      val message = shouldFail {
         one { "a" shouldBe "a" }
      }.message

      withClue("either should maintain the assertion that was executed in the counter") {
         assertionCounter.get() shouldBe beforeAssertions + 2
      }

      message shouldContainOnlyOnce  "One cannot ensure a mutual exclusion with less than two assertions"
   }

   test("either fails if more than one assertion succeeds") {
      shouldFail {
         one {
            "a" shouldBe "a"
            "b" shouldBe "b"
         }
      }.message shouldContainOnlyOnce "One expected a single assertion to succeed, but more than one succeeded."
   }

   test("one fails if all assertions fail") {
      shouldFail {
         one {
            "a" shouldBe "b"
            "b" shouldBe "c"
         }
      }.message shouldContainOnlyOnce "One expected a single assertion to succeed, but none succeeded."
   }

   test("one succeeds when a single assertion succeeds and many fail") {
      one {
         (0..9).forEach { it shouldBe it + 1 }
         1 shouldBe 1
      }
   }
})
