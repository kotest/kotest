package com.sksamuel.kotest.eq

import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.eq.DefaultEqResolver
import io.kotest.assertions.eq.Eq
import io.kotest.assertions.eq.EqContext
import io.kotest.assertions.eq.EqResult
import io.kotest.assertions.eq.shouldBe
import io.kotest.assertions.eq.shouldNotBe
import io.kotest.assertions.eq.withEqs
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.math.BigDecimal

class WithEqsTest : FunSpec() {
   init {

      test("withEqs overrides apply only for the single comparison they are attached to") {
         val a = BigDecimal("1.00")
         val b = BigDecimal("1")

         a shouldNotBe b

         a withEqs {
            register<BigDecimal>(BigDecimalIgnoreScaleEq)
         } shouldBe b

         a shouldNotBe b
      }

      test("withEqs overrides propagate into nested data class properties") {
         val barA = Bar(Foo("hello"), "world", BigDecimal("1"))
         val barB = Bar(Foo("hello"), "world", BigDecimal("1.00"))

         barA shouldNotBe barB

         barA withEqs {
            register<BigDecimal>(BigDecimalIgnoreScaleEq)
         } shouldBe barB
      }

      test("withEqs overrides propagate into Map values") {
         val a = mapOf("USD" to BigDecimal("3.14"), "EUR" to BigDecimal("2.99"))
         val b = mapOf("USD" to BigDecimal("3.140"), "EUR" to BigDecimal("2.990"))

         a shouldNotBe b

         a withEqs {
            register<BigDecimal>(BigDecimalIgnoreScaleEq)
         } shouldBe b
      }

      test("withEqs overrides propagate into Collection elements") {
         val a = listOf(BigDecimal("1.00"), BigDecimal("2.00"))
         val b = listOf(BigDecimal("1"), BigDecimal("2"))

         a shouldNotBe b

         a withEqs {
            register<BigDecimal>(BigDecimalIgnoreScaleEq)
         } shouldBe b
      }

      test("withEqs overrides survive deeper nesting (Map -> data class -> BigDecimal)") {
         val a = mapOf("rate" to Bar(Foo("hello"), "world", BigDecimal("1")))
         val b = mapOf("rate" to Bar(Foo("hello"), "world", BigDecimal("1.00")))

         a shouldNotBe b

         a withEqs {
            register<BigDecimal>(BigDecimalIgnoreScaleEq)
         } shouldBe b
      }

      test("withEqs failure path still throws on mismatch") {
         shouldThrowAny {
            BigDecimal("1.00") withEqs {
               register<BigDecimal>(BigDecimalIgnoreScaleEq)
            } shouldBe BigDecimal("2")
         }
      }

      test("withEqs does not leak overrides to subsequent global comparisons") {
         BigDecimal("1.00") withEqs {
            register<BigDecimal>(BigDecimalIgnoreScaleEq)
         } shouldBe BigDecimal("1")

         BigDecimal("1.00") shouldNotBe BigDecimal("1")
      }

      test("withEqs override wins over a globally registered Eq for the same type") {
         try {
            DefaultEqResolver.register(BigDecimal::class, RejectAllBigDecimalsEq)

            shouldThrowAny {
               BigDecimal("1") shouldBe BigDecimal("1")
            }

            BigDecimal("1.00") withEqs {
               register<BigDecimal>(BigDecimalIgnoreScaleEq)
            } shouldBe BigDecimal("1")
         } finally {
            DefaultEqResolver.unregister(BigDecimal::class)
         }
      }

      test("withEqs override does not apply when runtime types of actual and expected differ") {
         val a: Number = BigDecimal("1.00")
         val b: Number = 1L

         shouldThrowAny {
            a withEqs {
               register<BigDecimal>(BigDecimalIgnoreScaleEq)
            } shouldBe b
         }
      }

      test("withEqs shouldNotBe throws when override reports equality, returns actual otherwise") {
         val a = BigDecimal("1.00")
         val b = BigDecimal("1")
         val c = BigDecimal("2")

         shouldThrowAny {
            a withEqs {
               register<BigDecimal>(BigDecimalIgnoreScaleEq)
            } shouldNotBe b
         }

         a withEqs {
            register<BigDecimal>(BigDecimalIgnoreScaleEq)
         } shouldNotBe c
      }

      test("withEqs shouldNotBe override propagates into nested data class properties") {
         val barA = Bar(Foo("hello"), "world", BigDecimal("1"))
         val barB = Bar(Foo("hello"), "world", BigDecimal("1.00"))

         shouldThrowAny {
            barA withEqs {
               register<BigDecimal>(BigDecimalIgnoreScaleEq)
            } shouldNotBe barB
         }
      }

      test("withEqs shouldNotBe override wins over a globally registered Eq for the same type") {
         try {
            DefaultEqResolver.register(BigDecimal::class, RejectAllBigDecimalsEq)

            BigDecimal("1") shouldNotBe BigDecimal("1")

            shouldThrowAny {
               BigDecimal("1.00") withEqs {
                  register<BigDecimal>(BigDecimalIgnoreScaleEq)
               } shouldNotBe BigDecimal("1")
            }
         } finally {
            DefaultEqResolver.unregister(BigDecimal::class)
         }
      }

      test("register can be called multiple times for the same type — last call wins") {
         val a = BigDecimal("1.00")
         val b = BigDecimal("1")

         a withEqs {
            register<BigDecimal>(RejectAllBigDecimalsEq)
            register<BigDecimal>(BigDecimalIgnoreScaleEq)
         } shouldBe b
      }
   }
}

private object RejectAllBigDecimalsEq : Eq<BigDecimal> {
   override fun equals(actual: BigDecimal, expected: BigDecimal, context: EqContext): EqResult =
      EqResult.Failure {
         AssertionErrorBuilder.create()
            .withMessage("RejectAllBigDecimalsEq rejects everything")
            .build()
      }
}
