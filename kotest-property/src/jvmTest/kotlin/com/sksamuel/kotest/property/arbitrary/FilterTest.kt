package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldNotBeIn
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.kotest.property.Arb
import io.kotest.property.EdgeConfig
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.filterIsInstance
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.take
import io.kotest.property.arbitrary.withEdgecases

@EnabledIf(LinuxOnlyGithubCondition::class)
class FilterTest : FunSpec({

   test("should filter elements") {
      Arb.int(1..10).withEdgecases(2, 4, 6).filter { it % 2 == 0 }
         .take(1000, RandomSource.seeded(3242344L))
         .toList().distinct().sorted() shouldContainExactly listOf(2, 4, 6, 8, 10)
   }

   test("should filter edge cases") {
      val arb = Arb.int(1..10).withEdgecases(1, 2, 3).filter { it % 2 == 0 }
      val edgeCases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(5)
         .map { it.value }
         .toList()
      edgeCases shouldContainExactly listOf(2, 2, 2, 2, 2)
   }

   test("should be stack safe") {
      val arb = object : Arb<Int>() {
         override fun edgecase(rs: RandomSource): Sample<Int>? = null
         override fun sample(rs: RandomSource): Sample<Int> = Sample(rs.random.nextInt())
      }

      shouldNotThrow<StackOverflowError> {
         arb.filter { it % 2 == 0 }.take(1000000).toList()
      }
   }

   test("should apply filter to shrinks") {
      val arbEvenInts = Arb.int(-100..100).filter { it % 2 == 0 }
      val oddNumbers = (-100..100).filter { it % 2 != 0 }

      arbEvenInts.samples().take(100).forAll { sample ->
         sample.shrinks.value() shouldNotBeIn oddNumbers
         sample.shrinks.children.value.forAll {
            it.value() shouldNotBeIn oddNumbers
         }
      }
   }

   test("Arb.filter composition should not exhaust call stack") {
      var arb: Arb<Int> = Arb.of(0, 1)
      repeat(10000) {
         arb = arb.filter { it == 0 }
      }
      val result = shouldNotThrowAny { arb.single(RandomSource.seeded(1234L)) }
      result shouldBe 0
   }

   test("Arb.filterIsInstance should only keep instances of the given type") {
      val arb: Arb<Any> = Arb.of(1, "2", 3.0, "4", 5)
      val filtered = arb.filterIsInstance<String>()
      val result = filtered.samples().take(100).map { it.value }
      result.forAll { it should beInstanceOf(String::class) }
   }
})
