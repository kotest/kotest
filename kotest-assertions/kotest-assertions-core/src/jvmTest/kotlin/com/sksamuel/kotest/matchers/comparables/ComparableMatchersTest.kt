package com.sksamuel.kotest.matchers.comparables

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.comparables.beGreaterThan
import io.kotest.matchers.comparables.beGreaterThanOrEqualTo
import io.kotest.matchers.comparables.beLessThan
import io.kotest.matchers.comparables.beLessThanOrEqualTo
import io.kotest.matchers.comparables.compareTo
import io.kotest.matchers.comparables.gt
import io.kotest.matchers.comparables.gte
import io.kotest.matchers.comparables.lt
import io.kotest.matchers.comparables.lte
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.comparables.shouldNotBeEqualComparingTo
import io.kotest.matchers.equality.FieldsEqualityCheckConfig
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.property.checkAll

typealias MyMap = Map<String, String>

class ComparableMatchersTest : FreeSpec() {

   class ComparableExample(
      private val underlying: Int
   ) : Comparable<ComparableExample> {
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
               arrayOf(cn to cz, cz to cp).forAll {
                  it.first shouldBe lt(it.second)
                  it.first should beLessThan(it.second)
                  it.first shouldBeLessThan it.second
               }
            }

            "should throw exception for equal values" {
               arrayOf(cn, cz, cp).forAll {
                  shouldThrow<AssertionError> { it shouldBe lt(it) }
                  shouldThrow<AssertionError> { it should beLessThan(it) }
                  shouldThrow<AssertionError> { it shouldBeLessThan it }
               }
            }

            "should throw exception for greater values" {
               arrayOf(cp to cz, cz to cn).forAll {
                  shouldThrow<AssertionError> { it.first shouldBe lt(it.second) }
                  shouldThrow<AssertionError> { it.first should beLessThan(it.second) }
                  shouldThrow<AssertionError> { it.first shouldBeLessThan it.second }
               }
            }

         }

         "beLessThanOrEqualTo (`<=`) comparison" - {

            "should pass for lesser or equal values" {
               arrayOf(cn to cn, cn to cz, cz to cz, cz to cp, cp to cp).forAll {
                  it.first shouldBe lte(it.second)
                  it.first should beLessThanOrEqualTo(it.second)
                  it.first shouldBeLessThanOrEqualTo it.second
               }
            }

            "should throw exception for greater values" {
               arrayOf(cp to cz, cz to cn).forAll {
                  shouldThrow<AssertionError> { it.first shouldBe lte(it.second) }
                  shouldThrow<AssertionError> { it.first should beLessThanOrEqualTo(it.second) }
                  shouldThrow<AssertionError> { it.first shouldBeLessThanOrEqualTo it.second }
               }
            }

         }

         "beGreaterThan (`>`) comparison" - {

            "should pass for greater values" {
               arrayOf(cp to cz, cz to cn).forAll {
                  it.first shouldBe gt(it.second)
                  it.first should beGreaterThan(it.second)
                  it.first shouldBeGreaterThan it.second
               }
            }

            "should throw exception for equal values" {
               arrayOf(cn, cz, cp).forAll {
                  shouldThrow<AssertionError> { it shouldBe gt(it) }
                  shouldThrow<AssertionError> { it should beGreaterThan(it) }
                  shouldThrow<AssertionError> { it shouldBeGreaterThan it }
               }
            }

            "should throw exception for lesser values" {
               arrayOf(cn to cz, cz to cp).forAll {
                  shouldThrow<AssertionError> { it.first shouldBe gt(it.second) }
                  shouldThrow<AssertionError> { it.first should beGreaterThan(it.second) }
                  shouldThrow<AssertionError> { it.first shouldBeGreaterThan it.second }
               }
            }

         }

         "beGreaterThanOrEqualTo (`>=`) comparison" - {

            "should pass for greater than or equal values" {
               arrayOf(cp to cp, cp to cz, cz to cz, cz to cn, cn to cn).forAll {
                  it.first shouldBe gte(it.second)
                  it.first should beGreaterThanOrEqualTo(it.second)
                  it.first shouldBeGreaterThanOrEqualTo it.second
               }
            }

            "should throw exception for lesser values" {
               arrayOf(cn to cz, cz to cp).forAll {
                  shouldThrow<AssertionError> { it.first shouldBe gte(it.second) }
                  shouldThrow<AssertionError> { it.first should beGreaterThanOrEqualTo(it.second) }
                  shouldThrow<AssertionError> { it.first shouldBeGreaterThanOrEqualTo it.second }
               }
            }
         }

         "compareTo" - {

            "should pass for equal values" {
               checkAll { a: Int, b: Int ->
                  if (a == b) {
                     a should compareTo(b, Comparator { o1, o2 -> o1 - o2 })
                     a.shouldBeEqualComparingTo(b, Comparator { o1, o2 -> o1 - o2 })
                     a shouldBeEqualComparingTo b
                  } else {
                     a shouldNot compareTo(b, Comparator { o1, o2 -> o1 - o2 })
                     a.shouldNotBeEqualComparingTo(b, Comparator { o1, o2 -> o1 - o2 })
                     a shouldNotBeEqualComparingTo b
                  }
               }
            }

            "work for alias to java type" {

               data class Widget(val things: MyMap, val ignoreMe: String)

               val widget = Widget(mapOf("a" to "b"), ignoreMe = "blah")
               widget.shouldBeEqualToComparingFields(
                  Widget(mapOf("a" to "b"), ignoreMe = "foo"),
                  FieldsEqualityCheckConfig(propertiesToExclude = listOf(Widget::ignoreMe))
               )
            }
         }
      }
   }
}
