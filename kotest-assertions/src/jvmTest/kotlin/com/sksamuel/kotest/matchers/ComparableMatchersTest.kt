package com.sksamuel.kotest.matchers

import io.kotest.forAll
import io.kotest.matchers.comparables.beGreaterThan
import io.kotest.matchers.comparables.beGreaterThanOrEqualTo
import io.kotest.matchers.comparables.beLessThan
import io.kotest.matchers.comparables.beLessThanOrEqualTo
import io.kotest.matchers.comparables.compareTo
import io.kotest.matchers.comparables.gt
import io.kotest.matchers.comparables.gte
import io.kotest.matchers.comparables.lt
import io.kotest.matchers.comparables.lte
import io.kotest.properties.assertAll
import io.kotest.should
import io.kotest.shouldBe
import io.kotest.shouldNot
import io.kotest.shouldThrow
import io.kotest.specs.FreeSpec

class ComparableMatchersTest : FreeSpec() {

  class ComparableExample(private val underlying: Int) : Comparable<ComparableExample> {
    override fun compareTo(other: ComparableExample): Int {
      return when {
        underlying == other.underlying -> 0
        underlying > other.underlying -> 1
        else -> -1
      }
    }
  }

  init {

    val cn = ComparableExample(-100)
    val cz = ComparableExample(0)
    val cp = ComparableExample(100)

    "Comparable matchers" - {

      "beLessThan (`<`) comparison" - {

        "should pass test for lesser values" {
          forAll(arrayOf(Pair(cn, cz), Pair(cz, cp))) {
            it.first shouldBe lt(it.second)
            it.first should beLessThan(it.second)
          }
        }

        "should throw exception for equal values" {
          forAll(arrayOf(cn, cz, cp)) {
            shouldThrow<AssertionError> { it shouldBe lt(it) }
            shouldThrow<AssertionError> { it should beLessThan(it) }
          }
        }

        "should throw exception for greater values" {
          forAll(arrayOf(Pair(cp, cz), Pair(cz, cn))) {
            shouldThrow<AssertionError> { it.first shouldBe lt(it.second) }
            shouldThrow<AssertionError> { it.first should beLessThan(it.second) }
          }
        }

      }

      "beLessThanOrEqualTo (`<=`) comparison" - {

        "should pass for lesser or equal values" {
          forAll(arrayOf(Pair(cn, cn), Pair(cn, cz), Pair(cz, cz), Pair(cz, cp), Pair(cp, cp))) {
            it.first shouldBe lte(it.second)
            it.first should beLessThanOrEqualTo(it.second)
          }
        }

        "should throw exception for greater values" {
          forAll(arrayOf(Pair(cp, cz), Pair(cz, cn))) {
            shouldThrow<AssertionError> { it.first shouldBe lte(it.second) }
            shouldThrow<AssertionError> { it.first should beLessThanOrEqualTo(it.second) }
          }
        }

      }

      "beGreaterThan (`>`) comparison" - {

        "should pass for greater values" {
          forAll(arrayOf(Pair(cp, cz), Pair(cz, cn))) {
            it.first shouldBe gt(it.second)
            it.first should beGreaterThan(it.second)
          }
        }

        "should throw exception for equal values" {
          forAll(arrayOf(cn, cz, cp)) {
            shouldThrow<AssertionError> { it shouldBe gt(it) }
            shouldThrow<AssertionError> { it should beGreaterThan(it) }
          }
        }

        "should throw exception for lesser values" {
          forAll(arrayOf(Pair(cn, cz), Pair(cz, cp))) {
            shouldThrow<AssertionError> { it.first shouldBe gt(it.second) }
            shouldThrow<AssertionError> { it.first should beGreaterThan(it.second) }
          }
        }

      }

      "beGreaterThanOrEqualTo (`>=`) comparison" - {

        "should pass for greater than or equal values" {
          forAll(arrayOf(Pair(cp, cp), Pair(cp, cz), Pair(cz, cz), Pair(cz, cn), Pair(cn, cn))) {
            it.first shouldBe gte(it.second)
            it.first should beGreaterThanOrEqualTo(it.second)
          }
        }

        "should throw exception for lesser values" {
          forAll(arrayOf(Pair(cn, cz), Pair(cz, cp))) {
            shouldThrow<AssertionError> { it.first shouldBe gte(it.second) }
            shouldThrow<AssertionError> { it.first should beGreaterThanOrEqualTo(it.second) }
          }
        }

      }

      "compareTo" - {

        "should pass for equal values" {
          assertAll { a: Int, b: Int ->
            if (a == b)
              a should compareTo(b, Comparator { o1, o2 -> o1 - o2 })
            else
              a shouldNot compareTo(b, Comparator { o1, o2 -> o1 - o2 })
          }
        }
      }
    }

  }

}
