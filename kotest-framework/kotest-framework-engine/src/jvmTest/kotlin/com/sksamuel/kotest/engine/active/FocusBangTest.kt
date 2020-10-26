package com.sksamuel.kotest.engine.active

import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.TestCase
import io.kotest.core.test.isBang
import io.kotest.core.test.isFocused
import io.kotest.matchers.shouldBe

class FocusBangTest : FreeSpec() {
   init {
      "test case with f: prefix" - {
         "should be focused when top level" {
            val test = TestCase.test(FocusBangTest::class.toDescription().appendTest("f: a"), this@FocusBangTest) {}
            test.isFocused() shouldBe true
         }
         "should not be focused when nested" {
            val test = TestCase.test(
               FocusBangTest::class.toDescription().appendTest("a").appendTest("f: b"),
               this@FocusBangTest
            ) {}
            test.isFocused() shouldBe false
         }
      }

      "test case with ! prefix" - {
         "should be banged" {
            val test = TestCase.test(FocusBangTest::class.toDescription().appendTest("!a"), this@FocusBangTest) {}
            test.isBang() shouldBe true
         }
      }

      "test case with no prefix" - {
         "should not be focused" {
            val test = TestCase.test(FocusBangTest::class.toDescription().appendTest("a"), this@FocusBangTest) {}
            test.isFocused() shouldBe false
         }
         "should not be banged" {
            val test = TestCase.test(FocusBangTest::class.toDescription().appendTest("a"), this@FocusBangTest) {}
            test.isBang() shouldBe false
         }
      }
   }
}
