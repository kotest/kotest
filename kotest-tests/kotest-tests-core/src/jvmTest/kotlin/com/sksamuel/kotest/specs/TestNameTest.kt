package com.sksamuel.kotest.specs

import io.kotest.engine.config.Project
import io.kotest.core.test.TestNameCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestName
import io.kotest.core.test.format
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class TestNameTest : FunSpec() {

   init {
      val prefix = "Prefix: "

      test("test name case conversion only changes uppercase words correctly") {
         TestName(null, "Test URL").format(TestNameCase.Lowercase, true) shouldBe "test url"
         TestName("Pref OK", "Test URL").format(TestNameCase.Lowercase, true) shouldBe "pref oktest url"

         TestName(null, "Test URL").format(TestNameCase.InitialLowercase, true) shouldBe "test URL"
         TestName("Pref OK", "Test URL").format(TestNameCase.InitialLowercase, true) shouldBe "pref OKtest URL"

         TestName(null, "test URL").format(TestNameCase.Sentence, true) shouldBe "Test URL"
         TestName("pref OK", "Test URL").format(TestNameCase.Sentence, true) shouldBe "Pref OKtest URL"
      }

      test("prefix should be placed before name when not null") {
         TestName(null, "test").format(Project.testNameCase(), true) shouldBe "test"
         TestName("pref", "test").format(Project.testNameCase(), true) shouldBe "preftest"

         TestName(null, "Test").format(TestNameCase.InitialLowercase, true) shouldBe "test"
         TestName(null, "test").format(TestNameCase.InitialLowercase, true) shouldBe "test"
         TestName("Pref", "Test").format(TestNameCase.InitialLowercase, true) shouldBe "preftest"
         TestName("Pref", "test").format(TestNameCase.InitialLowercase, true) shouldBe "preftest"
         TestName("pref", "Test").format(TestNameCase.InitialLowercase, true) shouldBe "preftest"
         TestName("pref", "test").format(TestNameCase.InitialLowercase, true) shouldBe "preftest"

         TestName(null, "Test").format(TestNameCase.Sentence, true) shouldBe "Test"
         TestName(null, "test").format(TestNameCase.Sentence, true) shouldBe "Test"
         TestName("Pref", "Test").format(TestNameCase.Sentence, true) shouldBe "Preftest"
         TestName("Pref", "test").format(TestNameCase.Sentence, true) shouldBe "Preftest"
         TestName("pref", "Test").format(TestNameCase.Sentence, true) shouldBe "Preftest"
         TestName("pref", "test").format(TestNameCase.Sentence, true) shouldBe "Preftest"
      }

      test("Display Name should place bang before name") {
         val name = "!banged"
         TestName(null, name).bang.shouldBeTrue()
         TestName(null, name).format(TestNameCase.AsIs, true) shouldBe "!banged"

         listOf("!banged", "!Banged").forEach {
            TestName(null, it).bang.shouldBeTrue()
            TestName(null, it).format(TestNameCase.InitialLowercase, true) shouldBe "!banged"
         }

         listOf("!banged", "!Banged").forEach {
            TestName(null, it).bang.shouldBeTrue()
            TestName(null, it).format(TestNameCase.Sentence, true) shouldBe "!Banged"
         }
      }

      test("Display Name should place bang before prefix and name") {
         val name = "!banged"
         TestName(prefix, name).bang.shouldBeTrue()
         TestName(prefix, name).format(TestNameCase.AsIs, true) shouldBe "!Prefix: banged"

         listOf("!banged", "!Banged").forEach {
            TestName(prefix, it).bang.shouldBeTrue()
            TestName(prefix, it).format(TestNameCase.InitialLowercase, true) shouldBe "!prefix: banged"
         }

         listOf("!banged", "!Banged").forEach {
            TestName(prefix, it).bang.shouldBeTrue()
            TestName(prefix, it).format(TestNameCase.InitialLowercase, true) shouldBe "!Prefix: banged"
         }
      }

      test("Display Name should place focus before name") {
         val name = "f:Focused"
         TestName(null, name).focus.shouldBeTrue()
         TestName(null, name).format(TestNameCase.AsIs, true) shouldBe "f:Focused"

         Project.testNameCase(TestNameCase.InitialLowercase)
         listOf("f:Focused", "f:focused").forEach {
            TestName(null, it).focus.shouldBeTrue()
            TestName(null, it).format(Project.testNameCase(), true) shouldBe "f:focused"
         }

         Project.testNameCase(TestNameCase.Sentence)
         listOf("f:Focused", "f:focused").forEach {
            TestName(null, it).focus.shouldBeTrue()
            TestName(null, it).format(Project.testNameCase(), true) shouldBe "f:Focused"
         }
      }

      test("Display Name should place focus before prefix and name") {
         val name = "f:Focused"
         TestName(prefix, name).focus.shouldBeTrue()
         TestName(prefix, name).format(TestNameCase.AsIs, true) shouldBe "f:Prefix: Focused"

         listOf("f:Focused", "f:focused").forEach {
            TestName(prefix, it).focus.shouldBeTrue()
            TestName(prefix, it).format(TestNameCase.InitialLowercase, true) shouldBe "f:prefix: focused"
         }

         listOf("f:Focused", "f:focused").forEach {
            TestName(prefix, it).focus.shouldBeTrue()
            TestName(prefix, it).format(TestNameCase.Sentence, true) shouldBe "f:Prefix: focused"
         }
      }

      test("Should bring bang to the start of the test if there's a focus after it") {
         val name = "!f: BangFocus"
         TestName(prefix, name).format(TestNameCase.AsIs, true) shouldBe "!Prefix: f: BangFocus"
         TestName(prefix, name).format(TestNameCase.InitialLowercase, true) shouldBe "!prefix: f: BangFocus"
         TestName(prefix, name).format(TestNameCase.Sentence, true) shouldBe "!Prefix: f: BangFocus"
      }
   }
}
