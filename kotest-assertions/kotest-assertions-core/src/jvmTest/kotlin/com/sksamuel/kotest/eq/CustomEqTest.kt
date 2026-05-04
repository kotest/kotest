package com.sksamuel.kotest.eq

import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.eq.DefaultEqResolver
import io.kotest.assertions.eq.Eq
import io.kotest.assertions.eq.EqContext
import io.kotest.assertions.eq.EqResult
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.bigdecimal.shouldBeEqualIgnoringScale
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.math.BigDecimal

class CustomEqTest : FunSpec() {
   init {

      beforeSpec {
         DefaultEqResolver.register(Foo::class, FooEq)
      }

      test("custom eq should be selected if both sides are the same type") {
         Foo("hello") shouldBe Foo("world")
      }

      test("custom eq should be ignored if either side is not the same type") {
         shouldThrowAny {
            Foo("hello") shouldBe "world"
         }
         shouldThrowAny {
            "hello" shouldBe Foo("world")
         }
      }

      test("custom eq should be used for BigDecimal") {
         val a = BigDecimal("1.00")
         val b = BigDecimal("1")
         a shouldNotBe b

         DefaultEqResolver.register(BigDecimal::class, BigDecimalIgnoreScaleEq)
         a shouldBe b
         DefaultEqResolver.unregister(BigDecimal::class)
      }

      test("custom eq in nested data classes should use custom matcher") {
         val number = BigDecimal("1.00")
         val foo1 = Foo("hello")
         val foo2 = Foo("world")
         foo1 shouldBe foo2 // registered per spec

         val barA = Bar(foo1, "world", number)
         val barB = Bar(foo2, "world", number)
         barA shouldBe barB
      }

      test("custom eq in nested data classes should use multiple matchers") {
         val barA = Bar(Foo("hello"), "world", BigDecimal("1"))
         val barB = Bar(Foo("world"), "world", BigDecimal("1.00"))
         barA shouldNotBe barB
         DefaultEqResolver.register(BigDecimal::class, BigDecimalIgnoreScaleEq)
         barA shouldBe barB
         DefaultEqResolver.unregister(BigDecimal::class)
      }

      test("custom eq in deeply nested data classes should use multiple matchers") {
         val barA = Bar(Foo("hello"), "world", BigDecimal("1"))
         val barB = Bar(Foo("world"), "world", BigDecimal("1.00"))
         val nestedBarA = BarNestedFoo(barA)
         val nestedBarB = BarNestedFoo(barB)
         barA shouldNotBe barB
         nestedBarA shouldNotBe nestedBarB

         DefaultEqResolver.register(BigDecimal::class, BigDecimalIgnoreScaleEq)
         barA shouldBe barB
         nestedBarA shouldBe nestedBarB
         DefaultEqResolver.unregister(BigDecimal::class)
      }
   }
}

object FooEq : Eq<Foo> {
   override fun equals(actual: Foo, expected: Foo, context: EqContext): EqResult {
      return if (actual.value == "hello" && expected.value == "world")
         EqResult.Success
      else EqResult.Failure {
         AssertionErrorBuilder.create().withMessage("I don't like foo").build()
      }
   }
}

object BigDecimalIgnoreScaleEq : Eq<BigDecimal> {
   override fun equals(actual: BigDecimal, expected: BigDecimal, context: EqContext): EqResult {
      val result  = runCatching { actual.shouldBeEqualIgnoringScale(expected) }
      return if (result.isSuccess)
         EqResult.Success
      else EqResult.Failure {
         AssertionErrorBuilder.create().withMessage("BigDecimal values are not equal ignoring scale").build()
      }
   }
}

data class Foo(val value: String)
data class Bar(val foo: Foo, val txt: String, val num: BigDecimal)
data class BarNestedFoo(val bar: Bar)


