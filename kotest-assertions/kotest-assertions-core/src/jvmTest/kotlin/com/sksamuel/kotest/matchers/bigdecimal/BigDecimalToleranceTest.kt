package com.sksamuel.kotest.matchers.bigdecimal

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.bigdecimal.plusOrMinus
import io.kotest.matchers.doubles.percent
import io.kotest.matchers.bigdecimal.shouldBeWithinPercentageOf
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bigDecimal
import io.kotest.property.checkAll
import java.math.BigDecimal

class BigDecimalToleranceTest : FunSpec({

   test("double with tolerance should include tolerance in error message") {
      shouldThrowAny {
         BigDecimal("1.0") shouldBe (BigDecimal("1.5") plusOrMinus BigDecimal("0.4"))
      }.message shouldBe "1.0 should be equal to 1.5 within tolerance of 0.4 (lowest acceptable value is 1.1; highest acceptable value is 1.9)"
   }

   test("Allow for percentage tolerance") {
      BigDecimal("1.5") shouldBe (BigDecimal("1.0") plusOrMinus 50.percent)
      BigDecimal("1.5") shouldNotBe (BigDecimal("2.0") plusOrMinus 10.percent)

      BigDecimal("-1.5") shouldBe (BigDecimal("-1.0") plusOrMinus 50.percent)
      BigDecimal("-1.5") shouldNotBe (BigDecimal("-2.0") plusOrMinus 10.percent)
   }

   context("Percentage") {

      test("Match equal numbers") {
         checkAll(Arb.bigDecimal(), Arb.bigDecimal(BigDecimal("0.00001"), BigDecimal("5.0"))) { value, percentage ->
            value.shouldBeWithinPercentageOf(value, percentage)
         }
      }

      test("Refuse negative percentage") {
         shouldThrow<IllegalArgumentException> {
            BigDecimal("1.0").shouldBeWithinPercentageOf(BigDecimal("1.0"), BigDecimal("-0.1"))
         }
      }

      test("Match close enough numbers") {
         checkAll(Arb.bigDecimal(), Arb.bigDecimal(BigDecimal("0.00001"), BigDecimal("5.0"))) { value, percentage ->
            val delta = value.times(percentage / BigDecimal("100"))
            (value + delta).shouldBeWithinPercentageOf(value, percentage)
            (value - delta).shouldBeWithinPercentageOf(value, percentage)
         }
      }
   }
})

