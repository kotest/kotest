//package com.sksamuel.kotest.specs
//
//import io.kotest.core.config.getProject
//import io.kotest.core.test.TestNameCase
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.core.test.DescriptionName
//import io.kotest.matchers.booleans.shouldBeTrue
//import io.kotest.matchers.shouldBe
//
//class TestNameTest : FunSpec() {
//
//   init {
//      val prefix = "Prefix: "
//
//      test("test name case conversion only changes uppercase words correctly") {
//         DescriptionName.TestName(null, "Test URL").displayName(TestNameCase.Lowercase, true) shouldBe "test url"
//         DescriptionName.TestName("Pref OK", "Test URL").displayName(TestNameCase.Lowercase, true) shouldBe "pref oktest url"
//
//         DescriptionName.TestName(null, "Test URL").displayName(TestNameCase.InitialLowercase, true) shouldBe "test URL"
//         DescriptionName.TestName("Pref OK", "Test URL").displayName(TestNameCase.InitialLowercase, true) shouldBe "pref OKtest URL"
//
//         DescriptionName.TestName(null, "test URL").displayName(TestNameCase.Sentence, true) shouldBe "Test URL"
//         DescriptionName.TestName("pref OK", "Test URL").displayName(TestNameCase.Sentence, true) shouldBe "Pref OKtest URL"
//      }
//
//      test("prefix should be placed before name when not null") {
//         DescriptionName.TestName(null, "test").displayName(Project.testNameCase(), true) shouldBe "test"
//         DescriptionName.TestName("pref", "test").displayName(Project.testNameCase(), true) shouldBe "preftest"
//
//         DescriptionName.TestName(null, "Test").displayName(TestNameCase.InitialLowercase, true) shouldBe "test"
//         DescriptionName.TestName(null, "test").displayName(TestNameCase.InitialLowercase, true) shouldBe "test"
//         DescriptionName.TestName("Pref", "Test").displayName(TestNameCase.InitialLowercase, true) shouldBe "preftest"
//         DescriptionName.TestName("Pref", "test").displayName(TestNameCase.InitialLowercase, true) shouldBe "preftest"
//         DescriptionName.TestName("pref", "Test").displayName(TestNameCase.InitialLowercase, true) shouldBe "preftest"
//         DescriptionName.TestName("pref", "test").displayName(TestNameCase.InitialLowercase, true) shouldBe "preftest"
//
//         DescriptionName.TestName(null, "Test").displayName(TestNameCase.Sentence, true) shouldBe "Test"
//         DescriptionName.TestName(null, "test").displayName(TestNameCase.Sentence, true) shouldBe "Test"
//         DescriptionName.TestName("Pref", "Test").displayName(TestNameCase.Sentence, true) shouldBe "Preftest"
//         DescriptionName.TestName("Pref", "test").displayName(TestNameCase.Sentence, true) shouldBe "Preftest"
//         DescriptionName.TestName("pref", "Test").displayName(TestNameCase.Sentence, true) shouldBe "Preftest"
//         DescriptionName.TestName("pref", "test").displayName(TestNameCase.Sentence, true) shouldBe "Preftest"
//      }
//
//      test("Display Name should place bang before name") {
//         val name = "!banged"
//         TestName(null, name).bang.shouldBeTrue()
//         TestName(null, name).displayName(TestNameCase.AsIs, true) shouldBe "!banged"
//
//         listOf("!banged", "!Banged").forEach {
//            TestName(null, it).bang.shouldBeTrue()
//            TestName(null, it).displayName(TestNameCase.InitialLowercase, true) shouldBe "!banged"
//         }
//
//         listOf("!banged", "!Banged").forEach {
//            TestName(null, it).bang.shouldBeTrue()
//            TestName(null, it).displayName(TestNameCase.Sentence, true) shouldBe "!Banged"
//         }
//      }
//
//      test("Display Name should place bang before prefix and name") {
//         val name = "!banged"
//         TestName(prefix, name).bang.shouldBeTrue()
//         TestName(prefix, name).displayName(TestNameCase.AsIs, true) shouldBe "!Prefix: banged"
//
//         listOf("!banged", "!Banged").forEach {
//            TestName(prefix, it).bang.shouldBeTrue()
//            TestName(prefix, it).displayName(TestNameCase.InitialLowercase, true) shouldBe "!prefix: banged"
//         }
//
//         listOf("!banged", "!Banged").forEach {
//            TestName(prefix, it).bang.shouldBeTrue()
//            TestName(prefix, it).displayName(TestNameCase.InitialLowercase, true) shouldBe "!Prefix: banged"
//         }
//      }
//
//      test("Display Name should place focus before name") {
//         val name = "f:Focused"
//         TestName(null, name).focus.shouldBeTrue()
//         TestName(null, name).displayName(TestNameCase.AsIs, true) shouldBe "f:Focused"
//
//         Project.testNameCase(TestNameCase.InitialLowercase)
//         listOf("f:Focused", "f:focused").forEach {
//            TestName(null, it).focus.shouldBeTrue()
//            TestName(null, it).displayName(Project.testNameCase(), true) shouldBe "f:focused"
//         }
//
//         Project.testNameCase(TestNameCase.Sentence)
//         listOf("f:Focused", "f:focused").forEach {
//            TestName(null, it).focus.shouldBeTrue()
//            TestName(null, it).displayName(Project.testNameCase(), true) shouldBe "f:Focused"
//         }
//      }
//
//      test("Display Name should place focus before prefix and name") {
//         val name = "f:Focused"
//         TestName(prefix, name).focus.shouldBeTrue()
//         TestName(prefix, name).displayName(TestNameCase.AsIs, true) shouldBe "f:Prefix: Focused"
//
//         listOf("f:Focused", "f:focused").forEach {
//            TestName(prefix, it).focus.shouldBeTrue()
//            TestName(prefix, it).displayName(TestNameCase.InitialLowercase, true) shouldBe "f:prefix: focused"
//         }
//
//         listOf("f:Focused", "f:focused").forEach {
//            TestName(prefix, it).focus.shouldBeTrue()
//            TestName(prefix, it).displayName(TestNameCase.Sentence, true) shouldBe "f:Prefix: focused"
//         }
//      }
//
//      test("Should bring bang to the start of the test if there's a focus after it") {
//         val name = "!f: BangFocus"
//         TestName(prefix, name).displayName(TestNameCase.AsIs, true) shouldBe "!Prefix: f: BangFocus"
//         TestName(prefix, name).displayName(TestNameCase.InitialLowercase, true) shouldBe "!prefix: f: BangFocus"
//         TestName(prefix, name).displayName(TestNameCase.Sentence, true) shouldBe "!Prefix: f: BangFocus"
//      }
//   }
//}
