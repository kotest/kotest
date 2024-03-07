package io.kotest.matchers.properties

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import shouldHaveValue

class PropertiesKtTest : FunSpec({

   test("KProperty0<T>.shouldHaveValue happy path") {
      Foo("1")::a shouldHaveValue "1"
   }

   test("KProperty0<T>.shouldHaveValue with error message") {
      shouldThrowAny {
         Foo("1")::a shouldHaveValue "2"
      }.message shouldStartWith "Property 'a' should have value 2"
   }

   test("KProperty0<T>.shouldHaveValue with error message should include see-difference formatting") {
      shouldThrowAny {
         Foo("1")::a shouldHaveValue "2"
      }.message shouldContain "expected:<\"2\"> but was:<\"1\">"
   }
})

data class Foo(val a: String)
