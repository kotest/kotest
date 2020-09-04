package com.sksamuel.kotest.property.arrow

import arrow.core.None
import arrow.core.Some
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
         Some(1),
         Some(3),
         Some(10),
         Some(10),
         Some(1),
         Some(3),
         Some(10),
         Some(4),
         Some(2),
         Some(4)
      )
   }

   test("Arb.none should always yield none") {
      Arb.none<Int>().take(10, RandomSource.seeded(123456L)).toList() shouldContainExactly
         generateSequence { None }.take(10).toList()
   }

   test("Arb.option should apply arbitrary values to an option which may be empty") {
      Arb.option(Arb.int(1..10)).take(10, RandomSource.seeded(123456L)).toList() shouldContainExactly listOf(
         Some(1),
         None,
         None,
         Some(4),
         None,
         None,
         Some(7),
         Some(6),
         None,
         Some(2)
      )
   }
})
