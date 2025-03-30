package com.sksamuel.kotest.data

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.row
import kotlinx.coroutines.delay

@EnabledIf(NotMacOnGithubCondition::class)
class SuspendTest : FunSpec({

   test("forAll1 should support suspend functions") {
      forAll(
         row(1)
      ) {
         delay(10)
      }
   }

   test("forAll2 should support suspend functions") {
      forAll(
         row(1, 2)
      ) { _, _ ->
         delay(10)
      }
   }

   test("forAll3 should support suspend functions") {
      forAll(
         row(1, 2, 3)
      ) { _, _, _ ->
         delay(10)
      }
   }

   test("forAll4 should support suspend functions") {
      forAll(
         row(1, 2, 3, 4)
      ) { _, _, _, _ ->
         delay(10)
      }
   }

   test("forAll5 should support suspend functions") {
      forAll(
         row(1, 2, 3, 4, 5)
      ) { _, _, _, _, _ ->
         delay(10)
      }
   }

   test("forAll6 should support suspend functions") {
      forAll(
         row(1, 2, 3, 4, 5, 6)
      ) { _, _, _, _, _, _ ->
         delay(10)
      }
   }


   test("forAll7 should support suspend functions") {
      forAll(
         row(1, 2, 3, 4, 5, 6, 7)
      ) { _, _, _, _, _, _, _ ->
         delay(10)
      }
   }
})
