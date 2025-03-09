package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.shuffle
import io.kotest.property.arbitrary.slice
import io.kotest.property.arbitrary.subsequence
import io.kotest.property.arbitrary.take

@EnabledIf(LinuxCondition::class)
class CombinationsTest : FunSpec({

   test("shuffle should maintain all elements") {
      Arb.shuffle(listOf(1, 2, 3, 4, 5)).take(100).toList().forEach {
         it.shouldContainAll(1, 2, 3, 4, 5)
      }
   }

   test("shuffle should randomize") {
      Arb.shuffle(listOf(1, 2, 3, 4, 5)).take(10, RandomSource.seeded(12345L)).toList()
         .shouldContainExactly(
            listOf(1, 3, 5, 4, 2),
            listOf(3, 1, 5, 4, 2),
            listOf(3, 5, 4, 2, 1),
            listOf(4, 1, 5, 2, 3),
            listOf(5, 2, 4, 1, 3),
            listOf(3, 2, 4, 1, 5),
            listOf(5, 3, 4, 2, 1),
            listOf(4, 3, 2, 5, 1),
            listOf(2, 1, 5, 4, 3),
            listOf(3, 4, 2, 5, 1)
         )
   }

   test("subsequence should contain the empty list") {
      Arb.subsequence(listOf(1, 2, 3, 4, 5)).take(1000).toSet().shouldContain(emptyList())
   }

   test("subsequence should contain the original list") {
      Arb.subsequence(listOf(1, 2, 3, 4, 5)).take(1000).toSet().shouldContain(listOf(1, 2, 3, 4, 5))
   }

   test("slice") {
      val actual = Arb.slice(listOf(1, 2, 3, 4, 5)).take(10000).toList()
      actual.map { it.size }.distinct().shouldContainAll(0, 1, 2, 3, 4, 5)
      actual.filter { it.isNotEmpty() }.map { it[0] }.distinct().shouldContainAll(1, 2, 3, 4, 5)
   }
})
