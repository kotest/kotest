package com.sksamuel.kotest.matchers.numerics

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.ints.beEven
import io.kotest.matchers.ints.beOdd
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.ints.shouldBeNegative
import io.kotest.matchers.ints.shouldBePositive
import io.kotest.matchers.ints.shouldBeZero
import io.kotest.matchers.ints.shouldNotBeZero
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import io.kotest.data.forAll
import io.kotest.data.forNone
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.comparables.between
import io.kotest.matchers.comparables.shouldBeBetween

class IntMatchersTest : StringSpec() {
   init {

      "be positive" {
         1.shouldBePositive()

         shouldThrow<AssertionError> {
            (-1).shouldBePositive()
         }.message shouldBe "-1 should be > 0"

         shouldThrow<AssertionError> {
            (0).shouldBePositive()
         }.message shouldBe "0 should be > 0"
      }


      "be negative" {
         (-1).shouldBeNegative()

         shouldThrow<AssertionError> {
            1.shouldBeNegative()
         }.message shouldBe "1 should be < 0"

         shouldThrow<AssertionError> {
            0.shouldBeNegative()
         }.message shouldBe "0 should be < 0"
      }

      "should return expected/actual in intellij format" {
         shouldThrow<AssertionError> {
            1 shouldBe 444
         }.message shouldBe "expected:<444> but was:<1>"
      }

      "shouldBe should support ints" {
         1 shouldBe 1
      }

      "isEven" {
         4 shouldBe beEven()
         3 shouldNotBe beEven()
      }

      "isOdd" {
         3 shouldBe beOdd()
         4 shouldNotBe beOdd()
      }

      "inRange" {
         3 should io.kotest.matchers.ints.beInRange(1..10)
         3 should io.kotest.matchers.ints.beInRange(3..10)
         3 should io.kotest.matchers.ints.beInRange(3..3)
         4 shouldNot io.kotest.matchers.ints.beInRange(3..3)
         4 shouldNot io.kotest.matchers.ints.beInRange(1..3)
      }

      "beGreaterThan" {
         1 should io.kotest.matchers.ints.beGreaterThan(0)
         3.shouldBeGreaterThan(2)

         shouldThrow<AssertionError> {
            2 should io.kotest.matchers.ints.beGreaterThan(3)
         }
      }

      "beLessThan" {
         1 should io.kotest.matchers.ints.beLessThan(2)
         1.shouldBeLessThan(2)

         shouldThrow<AssertionError> {
            2 shouldBe io.kotest.matchers.ints.lt(1)
         }
      }

      "beLessThanOrEqualTo" {
         1 should io.kotest.matchers.ints.beLessThanOrEqualTo(2)
         2.shouldBeLessThanOrEqual(3)

         shouldThrow<AssertionError> {
            2 shouldBe io.kotest.matchers.ints.lte(1)
         }
      }

      "beGreaterThanOrEqualTo" {
         1 should io.kotest.matchers.ints.beGreaterThanOrEqualTo(0)
         3.shouldBeGreaterThanOrEqual(1)

         shouldThrow<AssertionError> {
            2 should io.kotest.matchers.ints.beGreaterThanOrEqualTo(3)
         }
      }

      "between should test for valid interval" {

         val table = table(
            headers("a", "b"),
            row(0, 2),
            row(1, 2),
            row(0, 1),
            row(1, 1)
         )

         forAll(table) { a, b ->
            1 shouldBe between(a, b)
            1.shouldBeBetween(a, b)
         }
      }

      "between should test for invalid interval" {

         val table = table(
            headers("a", "b"),
            row(0, 2),
            row(2, 2),
            row(4, 5),
            row(4, 6)
         )

         forNone(table) { a, b ->
            3 shouldBe between(a, b)
         }
      }

      "shouldBeZero" {
         0.shouldBeZero()
         1.shouldNotBeZero()
         Int.MIN_VALUE.shouldNotBeZero()
         Int.MAX_VALUE.shouldNotBeZero()
      }
   }
}
