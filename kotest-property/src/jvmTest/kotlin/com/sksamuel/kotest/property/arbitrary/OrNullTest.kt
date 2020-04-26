package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.ints.shouldBeBetween
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.orNull
import io.kotest.property.forAll

class OrNullTest : FunSpec({
   test("Arb.orNull().edgecases() should contain null") {
      Arb.int().orNull().edgecases() shouldContain null
   }
   test("Arb.orNull() should add null values to those generated") {
      val iterations = 1000
      val classifications =
         forAll(iterations, Arb.int().orNull()) { num ->
            classify(num == null, "null", "non-null")
            true
         }.classifications()
      classifications["null"]?.shouldBeBetween(300, 600)
      classifications["non-null"]?.shouldBeBetween(300, 600)
   }
   test("null probability values can be specified") {
      listOf(0.0, 0.1, 0.25, 0.5, 0.75, 0.9, 1.0)
         .forAll { p: Double ->
            val nullCount = Arb.long().orNull(nullProbability = p).values(RandomSource.Default)
               .map(Sample<Long?>::value)
               .take(1000)
               .filter { it == null }
               .count()

            (nullCount.toDouble() / 1000) shouldBe (p plusOrMinus 0.05)
         }
   }
   test("invalid null probability raise an IllegalArgumentException") {
      listOf(1.01, -0.1, 4.9, 99.9)
         .forAll { illegalVal ->
            shouldThrow<IllegalArgumentException> { Arb.long().orNull(nullProbability = illegalVal) }
         }
   }
   test("functions can be supplied to determine null frequency") {
      listOf(true, false).forAll { isNextNull: Boolean ->
         val allNull = Arb.int().orNull(isNextNull = { isNextNull }).values(RandomSource.Default)
            .map(Sample<Int?>::value)
            .take(100)
            .all { it == null }

         allNull shouldBe isNextNull
      }
   }
})
