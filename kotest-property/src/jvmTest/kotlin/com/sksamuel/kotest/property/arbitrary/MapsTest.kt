package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.take
import io.kotest.property.arbitrary.withEdgecases

@EnabledIf(LinuxCondition::class)
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

   context("Arb.map with individual key arb and value arb") {
      test("should generate map of a specified size") {
         val arbMap = Arb.map(Arb.int(1..10), Arb.string(1..10, Codepoint.alphanumeric()), minSize = 5, maxSize = 10)
         val maps = arbMap.take(1000, RandomSource.seeded(12345L)).toList()
         maps.forAll { it.size shouldBeInRange 5..10 }
      }

      test("should produce shrinks that adhere to minimum size") {
         val arbMap = Arb.map(Arb.int(1..10), Arb.string(1..10, Codepoint.alphanumeric()), minSize = 5, maxSize = 10)
         val maps = arbMap.samples(RandomSource.seeded(12345L)).take(100).toList()
         val shrinks = maps.flatMap { it.shrinks.children.value }
         shrinks.forAll { it.value().size shouldBeInRange 5..10 }
      }

      test("should throw when the cardinality of the key arbitrary does not satisfy the required minimum size") {
         val arbKey = Arb.int(1..3)
         val arbMap = Arb.map(arbKey, Arb.string(1..10), minSize = 5, maxSize = 10)
         shouldThrowWithMessage<IllegalArgumentException>(
            "the minimum size requirement of 5 could not be satisfied after 90 consecutive samples"
         ) {
            arbMap.single(RandomSource.seeded(1234L))
         }
      }
   }


   context("Arb.map with arb pair") {
      test("should generate map of a specified size") {
         val arbPair = Arb.pair(Arb.int(1..10), Arb.string(1..10, Codepoint.alphanumeric()))
         val arbMap = Arb.map(arbPair, minSize = 5, maxSize = 10)
         val maps = arbMap.take(100, RandomSource.seeded(12345L)).toList()
         maps.forAll { it.size shouldBeInRange 5..10 }
      }

      test("should produce shrinks that adhere to minimum size") {
         val arbPair = Arb.pair(Arb.int(1..10), Arb.string(1..10, Codepoint.alphanumeric()))
         val arbMap = Arb.map(arbPair, minSize = 5, maxSize = 10)
         val maps = arbMap.samples(RandomSource.seeded(12345L)).take(100).toList()
         val shrinks = maps.flatMap { it.shrinks.children.value }
         shrinks.forAll {
            it.value().size shouldBeInRange 5..10
         }
      }

      test("should throw when the cardinality of the key arbitrary does not satisfy the required minimum size") {
         val arbPair = Arb.pair(Arb.int(1..3), Arb.string(1..10, Codepoint.alphanumeric()))
         val arbMap = Arb.map(arbPair, minSize = 5, maxSize = 10)
         shouldThrowWithMessage<IllegalArgumentException>(
            "the minimum size requirement of 5 could not be satisfied after 90 consecutive samples"
         ) {
            arbMap.single(RandomSource.seeded(1234L))
         }
      }
   }

   context("Arb.map(arb,arb) edgecases") {
      test("should be empty if minSize is larger than 0") {
         Arb.map(keyArb = Arb.string(), valueArb = Arb.int(), minSize = 1).edgecase(RandomSource.seeded(1234L))
            .shouldBeNull()
      }

      test("should contain empty map if minSize is 0 (default)") {
         Arb.map(keyArb = Arb.string(), valueArb = Arb.int()).edgecase(RandomSource.seeded(1234L))
            .shouldNotBeNull()
            .shouldBeEmpty()
      }
   }

   context("Arb.map(arb) edgecases") {
      test("should be empty if disabled") {
         Arb.map(arb = Arb.pair(Arb.string(), Arb.int()), minSize = 1).edgecase(RandomSource.seeded(1234L))
            .shouldBeNull()
      }

      test("should contain empty map if enabled") {
         Arb.map(arb = Arb.pair(Arb.string(), Arb.int())).edgecase(RandomSource.seeded(1234L))
            .shouldNotBeNull()
            .shouldBeEmpty()
      }
   }

})
