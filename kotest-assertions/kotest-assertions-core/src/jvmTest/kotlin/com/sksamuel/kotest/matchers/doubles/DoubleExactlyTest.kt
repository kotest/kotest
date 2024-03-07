package com.sksamuel.kotest.matchers.doubles

import io.kotest.assertions.AssertionsConfigSystemProperties
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.extensions.system.withSystemProperty
import io.kotest.matchers.doubles.exactly
import io.kotest.matchers.doubles.shouldBeExactly
import io.kotest.matchers.doubles.shouldNotBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.checkAll

class DoubleExactlyTest : FreeSpec() {

   init {

      "for every numeric Double" - {
         "Should be exactly itself" {
            checkAll(100, numericDoubles) {
               it shouldExactlyMatch it
            }
         }

         "Should not be exactly" - {
            "Any number smaller than itself" {
               checkAll(100, nonMinNorMaxValueDoubles) {
                  it shouldNotMatchExactly it.slightlySmaller()
               }
            }
            "Any number bigger than itself" {
               checkAll(100, nonMinNorMaxValueDoubles) {
                  it shouldNotMatchExactly it.slightlyGreater()
               }
            }
            "Anything that's not numeric" {
               checkAll(100, numericDoubles) {
                  nonNumericDoubles.forEach { nonNumeric ->
                     it shouldNotMatchExactly nonNumeric
                  }
               }
            }
         }
      }

      "For non-numeric doubles" - {

         "NaN" - {
            "should be exactly NaN" {
               Double.NaN shouldBeExactly Double.NaN
            }

            "should not be exactly NaN when NaN equality is disabled" {
               withSystemProperty(AssertionsConfigSystemProperties.disableNaNEquality, "true") {
                  Double.NaN shouldNotMatchExactly Double.NaN
               }
            }

            "Should not be exactly any non-NaN" - {
               checkAll(100, numericDoubles) {
                  Double.NaN shouldNotMatchExactly it
               }
            }

            "should not be exactly infinity" {
               Double.Companion.NaN shouldNotMatchExactly Double.Companion.POSITIVE_INFINITY
               Double.Companion.NaN shouldNotMatchExactly Double.Companion.NEGATIVE_INFINITY
            }
         }

         "Positive Infinity" - {

            "Should be exactly" - {
               "Itself" {
                  Double.Companion.POSITIVE_INFINITY shouldExactlyMatch Double.Companion.POSITIVE_INFINITY
               }
            }

            "Should not be exactly" - {

               "Any numeric double" {
                  checkAll(100, numericDoubles) {
                     Double.POSITIVE_INFINITY shouldNotMatchExactly it
                  }
               }

               "Any other non-numeric double" {
                  Double.Companion.POSITIVE_INFINITY shouldNotMatchExactly Double.Companion.NEGATIVE_INFINITY
                  Double.Companion.POSITIVE_INFINITY shouldNotMatchExactly Double.Companion.NaN
               }
            }

         }

         "Negative Infinity" - {

            "Should be exactly" - {
               "Itself" {
                  Double.Companion.NEGATIVE_INFINITY shouldExactlyMatch Double.Companion.NEGATIVE_INFINITY
               }
            }

            "Should not be exactly" - {

               "Any numeric double" {
                  checkAll(100, numericDoubles) {
                     Double.NEGATIVE_INFINITY shouldNotMatchExactly it
                  }
               }

               "Any other non-numeric double" {
                  Double.Companion.NEGATIVE_INFINITY shouldNotMatchExactly Double.Companion.POSITIVE_INFINITY
                  Double.Companion.NEGATIVE_INFINITY shouldNotMatchExactly Double.Companion.NaN
               }
            }
         }
      }
   }

   private infix fun Double.shouldExactlyMatch(other: Double) {
      this shouldBeExactly other
      this shouldBe exactly(other)
      this shouldThrowExceptionOnNotExactly other
   }

   private infix fun Double.shouldNotMatchExactly(other: Double) {
      this shouldNotBe exactly(other)
      this shouldNotBeExactly other
      this shouldThrowExceptionOnExactly other
   }

   private infix fun Double.shouldThrowExceptionOnNotExactly(other: Double) {
      shouldThrowAssertionError("$this should not equal $other",
         { this shouldNotBeExactly other },
         { this shouldNotBe exactly(other) }
      )
   }

   private infix fun Double.shouldThrowExceptionOnExactly(other: Double) {
      shouldThrowAssertionError("$this is not equal to expected value $other",
         { this shouldBeExactly other },
         { this shouldBe exactly(other) }
      )
   }

   private fun shouldThrowAssertionError(message: String, vararg expression: () -> Any?) {
      expression.forEach {
         val exception = shouldThrow<AssertionError>(it)
         exception.message shouldBe message
      }
   }
}
