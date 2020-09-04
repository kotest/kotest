package com.sksamuel.kotest.property.arrow

import arrow.core.Left
import arrow.core.Right
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
         Right(10),
         Right(3),
         Left('a'),
         Right(2),
         Left('x'),
         Left('s'),
         Left('n'),
         Left('t'),
         Left('q'),
         Right(2)
      )
   }

   test("Arb.left should project arbitrary values to left") {
      val lefts = Arb.left(Arb.int(1..10)).take(10, RandomSource.seeded(123456L)).toList()
      lefts shouldContainExactly listOf(
         Left(1),
         Left(3),
         Left(10),
         Left(10),
         Left(1),
         Left(3),
         Left(10),
         Left(4),
         Left(2),
         Left(4)
      )
   }

   test("Arb.right should project arbitrary values to right") {
      val rights = Arb.right(Arb.int(1..10)).take(10, RandomSource.seeded(123456L)).toList()
      rights shouldContainExactly listOf(
         Right(1),
         Right(3),
         Right(10),
         Right(10),
         Right(1),
         Right(3),
         Right(10),
         Right(4),
         Right(2),
         Right(4)
      )
   }
})
