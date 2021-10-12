package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.take
import io.kotest.property.arbitrary.withEdgecases

class MapsTest : FunSpec({
   context("Arb.pair") {
      test("should generate a pair of values from given arb key / value") {
         val arbKey = Arb.int(1..10)
         val arbValue = Arb.string(1..10, Codepoint.alphanumeric())
         val arbPair: Arb<Pair<Int, String>> = Arb.pair(arbKey, arbValue)

         arbPair.take(5, RandomSource.seeded(1234L)).toList() shouldContainExactly listOf(
            Pair(6, "tI"),
            Pair(6, "i7"),
            Pair(9, "wmWkyqH"),
            Pair(7, "J"),
            Pair(7, "V")
         )
      }

      test("should generate edgecases from key and value arbs") {
         val arbKey = Arb.int(1..10).withEdgecases(5)
         val arbValue = Arb.string(1..10, Codepoint.alphanumeric()).withEdgecases("edge")
         val arbPair: Arb<Pair<Int, String>> = Arb.pair(arbKey, arbValue)

         arbPair.edgecases(rs = RandomSource.seeded(1234L)).take(5).toList() shouldContainExactly listOf(
            Pair(5, "edge"),
            Pair(5, "awmWkyqH8"),
            Pair(5, "V"),
            Pair(7, "edge"),
            Pair(5, "LOFBhzunuV")
         )
      }
   }
})
