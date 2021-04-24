package com.sksamuel.kotest.property.arrow

import arrow.core.none
import arrow.core.some
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.take
import io.kotest.property.arrow.none
import io.kotest.property.arrow.option
import io.kotest.property.arrow.some

class OptionTest : FunSpec({
   test("Arb.some should apply arbitrary values to some") {
      Arb.some(Arb.int(1..10)).take(10, RandomSource.seeded(123456L)).toList() shouldContainExactly listOf(
         1.some(),
         3.some(),
         10.some(),
         10.some(),
         1.some(),
         3.some(),
         10.some(),
         4.some(),
         2.some(),
         4.some()
      )
   }

   test("Arb.none should always yield none") {
      Arb.none<Int>().take(10, RandomSource.seeded(123456L)).toList() shouldContainExactly
         generateSequence { none<Int>() }.take(10).toList()
   }

   test("Arb.option should apply arbitrary values to an option which may be empty") {
      Arb.option(Arb.int(1..10)).take(10, RandomSource.seeded(123456L)).toList() shouldContainExactly listOf(
         1.some(),
         none(),
         none(),
         4.some(),
         none(),
         none(),
         7.some(),
         6.some(),
         none(),
         2.some()
      )
   }
})
