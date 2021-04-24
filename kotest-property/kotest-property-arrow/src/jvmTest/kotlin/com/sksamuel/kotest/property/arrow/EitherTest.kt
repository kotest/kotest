package com.sksamuel.kotest.property.arrow

import arrow.core.left
import arrow.core.right
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.take
import io.kotest.property.arrow.either
import io.kotest.property.arrow.left
import io.kotest.property.arrow.right

class EitherTest : FunSpec({
   test("Arb.either should generate both left and right") {
      val eithers = Arb.either(Arb.char('a'..'z'), Arb.int(1..10)).take(10, RandomSource.seeded(123456L)).toList()
      eithers shouldContainExactly listOf(
         'h'.left(),
         3.right(),
         10.right(),
         2.right(),
         'x'.left(),
         's'.left(),
         'n'.left(),
         't'.left(),
         'q'.left(),
         2.right()
      )
   }

   test("Arb.left should project arbitrary values to left") {
      val lefts = Arb.left(Arb.int(1..10)).take(10, RandomSource.seeded(123456L)).toList()
      lefts shouldContainExactly listOf(
         1.left(),
         3.left(),
         10.left(),
         10.left(),
         1.left(),
         3.left(),
         10.left(),
         4.left(),
         2.left(),
         4.left()
      )
   }

   test("Arb.right should project arbitrary values to right") {
      val rights = Arb.right(Arb.int(1..10)).take(10, RandomSource.seeded(123456L)).toList()
      rights shouldContainExactly listOf(
         1.right(),
         3.right(),
         10.right(),
         10.right(),
         1.right(),
         3.right(),
         10.right(),
         4.right(),
         2.right(),
         4.right()
      )
   }
})
