package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.take
import io.kotest.property.arbitrary.withEdgecases

class FilterTest : FunSpec({

   test("filter should filter elements") {
      Arb.int(1..10).withEdgecases(2, 4, 6).filter { it % 2 == 0 }
         .take(1000, RandomSource.seeded(3242344L))
         .toList().distinct().sorted() shouldContainExactly listOf(2, 4, 6, 8, 10)
   }

   test("should be stack safe") {
      val arb = object : Arb<Int>() {
         override fun edgecases(): List<Int> = emptyList()
         override fun sample(rs: RandomSource): Sample<Int> = Sample(rs.random.nextInt())
      }

      shouldNotThrow<StackOverflowError> {
         arb.filter { it % 2 == 0 }.take(1000000).toList()
      }
   }
})
