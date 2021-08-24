package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldNotBeIn
import io.kotest.property.Arb
import io.kotest.property.EdgeConfig
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.filterNot
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.take
import io.kotest.property.arbitrary.withEdgecases

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
         override fun edgecase(rs: RandomSource): Int? = null
         override fun sample(rs: RandomSource): Sample<Int> = Sample(rs.random.nextInt())
      }

      shouldNotThrow<StackOverflowError> {
         arb.filter { it % 2 == 0 }.take(1000000).toList()
      }
   }

   test("should apply filter to shrinks") {
      val filteredElements = listOf(1, -1)
      val arb = Arb.int(-100..100).filterNot { filteredElements.contains(it) }
      val samples = arb.samples(RandomSource.default()).take(1000)
      samples.forAll { sample ->
         sample.shrinks.value() shouldNotBeIn filteredElements
         sample.shrinks.children.value.forAll {
            it.value() shouldNotBeIn filteredElements
         }
      }
   }
})
