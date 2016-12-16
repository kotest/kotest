package io.kotlintest.matchers

import io.kotlintest.specs.FreeSpec

class ComparableMatchersTest : FreeSpec() {

  class ComparableExample(val underlying: Int) : Comparable<ComparableExample> {
    override fun compareTo(other: ComparableExample): Int {
      return when {
        underlying == other.underlying -> 0
        underlying >  other.underlying -> 1
        else                           -> -1
      }
    }
  }

  init {
    "Comparable matchers" - {

      val cn = ComparableExample(-100)
      val cz = ComparableExample(0)
      val cp = ComparableExample(100)

      "for `>` (greater than) comparison" - {
        "should pass test for valid values" {
          forAll(arrayOf(Pair(cp, cz), Pair(cz, cn))) {
            it.first should be gt it.second
          }
        }

        "should throw exception for equal values" {
          forAll(arrayOf(cn, cz, cp)) {
            shouldThrow<AssertionError> { it should be gt it }
          }
        }
      }

      "for `<` (less than) comparison" - {
        "should pass test for valid values" {
          forAll(arrayOf(Pair(cn, cz), Pair(cz, cp))) {
            it.first should be lt it.second
          }
        }

        "should throw exception for equal values" {
          forAll(arrayOf(cn, cz, cp)) {
            shouldThrow<AssertionError> { it should be lt it }
          }
        }
      }

      "for `>=` (greater than or equal) comparison" - {
        "should pass test for valid values" {
          forAll(arrayOf(Pair(cp, cp), Pair(cp, cz), Pair(cz, cz), Pair(cz, cn), Pair(cn, cn))) {
            it.first should be gte it.second
          }
        }

        "should throw exception for lesser values" {
          forAll(arrayOf(Pair(cn, cz), Pair(cz, cp))) {
            shouldThrow<AssertionError> { it.first should be gte it.second }
          }
        }
      }

      "for `<=` (less than or equal) comparison" - {
        "should pass test for valid values" {
          forAll(arrayOf(Pair(cn, cn), Pair(cn, cz), Pair(cz, cz), Pair(cz, cp), Pair(cp, cp))) {
            it.first should be lte it.second
          }
        }

        "should throw exception for greater values" {
          forAll(arrayOf(Pair(cp, cz), Pair(cz, cn))) {
            shouldThrow<AssertionError> { it.first should be lte it.second }
          }
        }
      }
    }
  }
}