package com.sksamuel.kotest

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.Description
import io.kotest.core.test.TestCase
import io.kotest.core.test.isBang
import io.kotest.core.test.isFocused
import io.kotest.shouldBe

class FocusBangTest : FunSpec() {

   init {

      test("test case is focused should return true for top level f: test") {
         val test = TestCase.test(Description.spec("f: my test"), this@FocusBangTest) {}
         test.isFocused() shouldBe true
      }

      test("test case is focused should return false for top level test without f: prefix") {
         val test = TestCase.test(Description.spec("my test"), this@FocusBangTest) {}
         test.isFocused() shouldBe false
      }

      test("is bang should return true for tests with ! prefix") {
         val test = TestCase.test(Description.spec("!my test"), this@FocusBangTest) {}
         test.isBang() shouldBe true
      }

      test("is bang should return false for tests without ! prefix") {
         val test = TestCase.test(Description.spec("my test"), this@FocusBangTest) {}
         test.isBang() shouldBe false
      }
   }

}
