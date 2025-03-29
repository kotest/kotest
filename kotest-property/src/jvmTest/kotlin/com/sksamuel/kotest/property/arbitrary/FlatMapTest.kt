package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import io.kotest.property.Arb
import io.kotest.property.EdgeConfig
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.take
import io.kotest.property.arbitrary.withEdgecases

@EnabledIf(NotMacOnGithubCondition::class)
class FlatMapTest : FunSpec() {
   init {
      test("Arb.flatMap should compute probabilistic edge cases") {

         val arbString = Arb.int(1..10).withEdgecases(1, 2).flatMap { a ->
            Arb.double().withEdgecases(1.0, 2.0).flatMap { b ->
               Arb.string().withEdgecases("foo", "bar").map { c ->
                  "$a $b $c"
               }
            }
         }
         val edges = arbString
            .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
            .take(10)
            .map { it.value }
            .toList()

         edges shouldContainExactly listOf(
            "2 2.0 bar",
            "2 2.0 bar",
            "1 1.0 foo",
            "1 2.0 foo",
            "2 1.0 bar",
            "2 1.0 bar",
            "2 2.0 bar",
            "2 1.0 foo",
            "2 2.0 bar",
            "2 2.0 foo"
         )
      }

      test("Arb.flatMap should replace null edge cases with samples") {

         val arbString = Arb.int(1..2).withEdgecases(emptyList()).flatMap { a ->
            Arb.int(3..4).withEdgecases(emptyList()).flatMap { b ->
               Arb.string().withEdgecases("foo", "bar").map { c ->
                  "$a $b $c"
               }
            }
         }

         val edges = arbString
            .edgecases(100, RandomSource.seeded(1234L))
            .toList()

         edges shouldContainExactly listOf(
            "2 4 bar",
            "1 3 foo",
            "1 4 foo",
            "2 3 bar",
            "2 3 foo",
            "2 4 foo",
            "1 3 bar",
            "1 4 bar",
         )
      }


      test("Arb.flatMap should generate dependent Arb") {
         Arb.int(1..10).flatMap { Arb.int(1..it * it) }.take(15, RandomSource.seeded(3242344L))
            .toList() shouldContainExactly
            listOf(
               16,
               1,
               6,
               23,
               6,
               17,
               3,
               1,
               44,
               24,
               25,
               8,
               2,
               3,
               22
            )
      }

      test("Arb.flatMap composition should not exhaust call stack") {
         var arb: Arb<Int> = Arb.int(-3..3)
         repeat(10000) {
            arb = arb.flatMap { value ->
               Arb.int(-3..3).flatMap {
                  Arb.of(value + it)
               }
            }
         }
         val result = shouldNotThrowAny { arb.single(RandomSource.seeded(1234L)) }
         result shouldBe 49
      }

      test("should yield a new immutable arb") {
         val firstArb: Arb<Int> = Arb.int(-3..3)
         val secondArb: Arb<Int> = firstArb.flatMap { Arb.int(10..30) }
         val thirdArb: Arb<String> = secondArb.flatMap { Arb.string(3, Codepoint.alphanumeric()) }

         firstArb shouldNotBeSameInstanceAs secondArb
         firstArb shouldNotBeSameInstanceAs thirdArb
         secondArb shouldNotBeSameInstanceAs thirdArb

         firstArb.single(RandomSource.seeded(1234L)) shouldBe 3
         secondArb.single(RandomSource.seeded(1234L)) shouldBe 28
         thirdArb.single(RandomSource.seeded(1234L)) shouldBe "tID"
      }
   }
}
