package com.sksamuel.kotest.data

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

@EnabledIf(NotMacOnGithubCondition::class)
class ParamDetectionTest : StringSpec({

   "row1 should detect header names from params" {
      shouldThrow<AssertionError> {
         forAll(
            row(1)
         ) { foo ->
            foo shouldBe 0
         }
      }.message shouldBe "Test failed for (foo, 1) with error expected:<0> but was:<1>"
   }


   "row2 should detect header names from params" {
      shouldThrow<AssertionError> {
         forAll(
            row(1, 2)
         ) { foo, bar ->
            foo * bar shouldBe 0
         }
      }.message shouldBe "Test failed for (foo, 1), (bar, 2) with error expected:<0> but was:<2>"
   }

   "row3 should detect header names from params" {
      shouldThrow<AssertionError> {
         forAll(
            row(1, 2, 3)
         ) { foo, bar, woo ->
            foo * bar * woo shouldBe 0
         }
      }.message shouldBe "Test failed for (foo, 1), (bar, 2), (woo, 3) with error expected:<0> but was:<6>"
   }

   "row4 should detect header names from params" {
      shouldThrow<AssertionError> {
         forAll(
            row(1, 2, 3, 4)
         ) { foo, bar, woo, boo ->
            foo * bar * woo * boo shouldBe 0
         }
      }.message shouldBe "Test failed for (foo, 1), (bar, 2), (woo, 3), (boo, 4) with error expected:<0> but was:<24>"
   }

   "row5 should detect header names from params" {
      shouldThrow<AssertionError> {
         forAll(
            row(1, 2, 3, 4, 5)
         ) { foo, bar, woo, boo, war ->
            foo * bar * woo * boo * war shouldBe 0
         }
      }.message shouldBe "Test failed for (foo, 1), (bar, 2), (woo, 3), (boo, 4), (war, 5) with error expected:<0> but was:<120>"
   }

   "row6 should detect header names from params" {
      shouldThrow<AssertionError> {
         forAll(
            row(1, 2, 3, 4, 5, 6)
         ) { foo, bar, woo, boo, war, tar ->
            foo * bar * woo * boo * war * tar shouldBe 0
         }
      }.message shouldBe "Test failed for (foo, 1), (bar, 2), (woo, 3), (boo, 4), (war, 5), (tar, 6) with error expected:<0> but was:<720>"
   }

   "row7 should detect header names from params" {
      shouldThrow<AssertionError> {
         forAll(
            row(1, 2, 3, 4, 5, 6, 7)
         ) { foo, bar, woo, boo, war, tar, baz ->
            foo * bar * woo * boo * war * tar * baz shouldBe 0
         }
      }.message shouldBe "Test failed for (foo, 1), (bar, 2), (woo, 3), (boo, 4), (war, 5), (tar, 6), (baz, 7) with error expected:<0> but was:<5040>"
   }
})
