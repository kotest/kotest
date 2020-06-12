package com.sksamuel.kotest.specs

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestName
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class TestNameTest : FunSpec() {

   init {
      val prefix = "Prefix: "

      test("prefix should be placed before name when not null") {
         TestName(null, "test").displayName() shouldBe "test"
         TestName("pref", "test").displayName() shouldBe "pref: test"
      }

      test("Display Name should place bang before name") {
         val name = "!banged"
         TestName(null, name).bang.shouldBeTrue()
         TestName(null, name).displayName() shouldBe "!banged"
      }

      test("Display Name should place bang before prefix and name") {
         val name = "!banged"
         TestName(prefix, name).bang.shouldBeTrue()
         TestName(prefix, name).displayName() shouldBe "!Prefix: banged"
      }

      test("Display Name should place focus before name") {
         val name = "f:Focused"
         TestName(null, name).focus.shouldBeTrue()
         TestName(null, name).displayName() shouldBe "f:Focused"
      }

      test("Display Name should place focus before prefix and name") {
         val name = "f:Focused"
         TestName(prefix, name).focus.shouldBeTrue()
         TestName(prefix, name).displayName() shouldBe "f:Prefix: Focused"
      }

      test("Should bring bang to the start of the test if there's a focus after it") {
         val name = "!f: BangFocus"
         TestName(prefix, name).displayName() shouldBe "!Prefix: f: BangFocus"
      }

   }
}
