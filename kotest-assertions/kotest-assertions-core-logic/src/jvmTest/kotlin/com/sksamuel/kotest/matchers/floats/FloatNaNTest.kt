package com.sksamuel.kotest.matchers.floats

import com.sksamuel.kotest.matchers.doubles.numericFloats
import io.kotest.assertions.AssertionsConfigSystemProperties
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.withSystemProperty
import io.kotest.matchers.floats.beNaN
import io.kotest.matchers.floats.shouldBeExactly
import io.kotest.matchers.floats.shouldBeNaN
import io.kotest.matchers.floats.shouldNotBeExactly
import io.kotest.matchers.floats.shouldNotBeNaN
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.property.checkAll

class FloatNaNTest : FunSpec() {
   init {
      context("NaN matcher") {

         test("Every numeric float should not be NaN") {
            checkAll(100, numericFloats) {
               it.shouldNotMatchNaN()
            }
         }

         test("The non-numeric floats") {
            Float.NaN.shouldMatchNaN()
            Float.POSITIVE_INFINITY.shouldNotMatchNaN()
            Float.NEGATIVE_INFINITY.shouldNotMatchNaN()
         }

         test("NaN should be exactly NaN") {
            Float.NaN shouldBeExactly Float.NaN
            shouldThrowAny {
               Float.NaN shouldNotBeExactly Float.NaN
            }
         }

         test("NaN should not be exactly NaN when NaN equality is disabled") {
            withSystemProperty(AssertionsConfigSystemProperties.DISABLE_NAN_NEQUALITY, "true") {
               Float.NaN shouldNotBeExactly Float.NaN
               shouldThrowAny {
                  Float.NaN shouldBeExactly Float.NaN
               }
            }
         }
      }

   }

   private fun Float.shouldMatchNaN() {
      this should beNaN()
      this.shouldBeNaN()

      this.shouldThrowExceptionOnNotBeNaN()
   }

   private fun Float.shouldNotMatchNaN() {
      this shouldNot beNaN()
      this.shouldNotBeNaN()

      this.shouldThrowExceptionOnBeNaN()
   }

   private fun Float.shouldThrowExceptionOnNotBeNaN() {
      shouldThrowAssertionError("$this should not be NaN",
         { this.shouldNotBeNaN() },
         { this shouldNot beNaN() })
   }

   private fun Float.shouldThrowExceptionOnBeNaN() {
      shouldThrowAssertionError("$this should be NaN",
         { this.shouldBeNaN() },
         { this should beNaN() })
   }

   private fun shouldThrowAssertionError(message: String, vararg expression: () -> Any?) {
      expression.forEach {
         val exception = shouldThrow<AssertionError>(it)
         exception.message shouldBe message
      }
   }
}
