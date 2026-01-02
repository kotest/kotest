package com.sksamuel.kotest.eq

import io.kotest.assertions.eq.DefaultEqResolver
import io.kotest.assertions.eq.Eq
import io.kotest.assertions.eq.EqContext
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CustomEqTest : FunSpec() {
   init {

      beforeSpec {
         DefaultEqResolver.register(Foo::class, fooEq)
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

val fooEq = object : Eq<Foo> {
   override fun equals(actual: Foo, expected: Foo, context: EqContext): Throwable? {
      return if (actual.value == "hello" && expected.value == "world") null else RuntimeException("foo")
   }
}

data class Foo(val value: String)
