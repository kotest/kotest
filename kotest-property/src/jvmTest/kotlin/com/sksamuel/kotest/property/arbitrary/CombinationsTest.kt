package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.property.Arb
import io.kotest.property.arbitrary.shuffle
import io.kotest.property.arbitrary.subsequence
import io.kotest.property.arbitrary.take

class CombinationsTest : FunSpec({

   test("shuffle should maintain all elements") {
      Arb.shuffle(listOf(1, 2, 3, 4, 5)).take(100).forAll {
         it.shouldContainAll(1, 2, 3, 4, 5)
      }
   }

   test("shuffle should randomize") {
      Arb.shuffle(listOf(1, 2, 3, 4, 5)).take(100).toSet().size.shouldBeGreaterThan(1)
   }

   test("subsequence should contain the empty list") {
      Arb.subsequence(listOf(1, 2, 3, 4, 5)).take(1000).toSet().shouldContain(emptyList())
   }

   test("subsequence should contain the original list") {
      Arb.subsequence(listOf(1, 2, 3, 4, 5)).take(1000).toSet().shouldContain(listOf(1, 2, 3, 4, 5))
   }
})
