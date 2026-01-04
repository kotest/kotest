package com.sksamuel.kotest.eq

import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.eq.DefaultEqResolver
import io.kotest.assertions.eq.Eq
import io.kotest.assertions.eq.EqContext
import io.kotest.assertions.eq.EqResult
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

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

data class Foo(val value: String)
