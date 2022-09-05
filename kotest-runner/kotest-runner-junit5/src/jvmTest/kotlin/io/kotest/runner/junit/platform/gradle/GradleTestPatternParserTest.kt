package io.kotest.runner.junit.platform.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class GradleTestPatternParserTest : FunSpec() {
   init {

      test("parse package") {
         GradleTestPattern.parse("org.mypackage") shouldBe GradleTestPattern(false, "org.mypackage", null, null)
         GradleTestPattern.parse("org") shouldBe GradleTestPattern(false, "org", null, null)
      }

      test("parse package with wildcard prefix") {
         GradleTestPattern.parse("*.mypackage") shouldBe GradleTestPattern(true, "mypackage", null, null)
      }

      test("parse Classname") {
         GradleTestPattern.parse("MyClass") shouldBe GradleTestPattern(false, null, "MyClass", null)
         GradleTestPattern.parse("A") shouldBe GradleTestPattern(false, null, "A", null)
      }

      test("parse Classname with wildcard prefix") {
         GradleTestPattern.parse("*Spec") shouldBe GradleTestPattern(true, null, "Spec", null)
      }

      test("parse package.Classname") {
         GradleTestPattern.parse("org.mypackage.MyClass") shouldBe GradleTestPattern(
            false,
            "org.mypackage",
            "MyClass",
            null,
         )
         GradleTestPattern.parse("org.A") shouldBe GradleTestPattern(false, "org", "A", null)
      }

      test("parse package.Classname with wildcard") {
         GradleTestPattern.parse("*org.mypackage.MyClass") shouldBe GradleTestPattern(
            true,
            "org.mypackage",
            "MyClass",
            null,
         )
         GradleTestPattern.parse("org.A") shouldBe GradleTestPattern(false, "org", "A", null)
      }

      test("parse package.Classname.path") {
         GradleTestPattern.parse("org.myPackage.MyClass.test") shouldBe
            GradleTestPattern(false, "org.myPackage", "MyClass", "test")
         GradleTestPattern.parse("org.A.test with space") shouldBe GradleTestPattern(
            false,
            "org",
            "A",
            "test with space",
         )
         GradleTestPattern.parse("org.A.test -- delimited and space") shouldBe
            GradleTestPattern(false, "org", "A", "test -- delimited and space")
      }

      test("parse Classname.path") {
         GradleTestPattern.parse("MyClass.test") shouldBe
            GradleTestPattern(false, null, "MyClass", "test")

         GradleTestPattern.parse("*MyClass.test") shouldBe
            GradleTestPattern(true, null, "MyClass", "test")

         GradleTestPattern.parse("A.test with space") shouldBe
            GradleTestPattern(false, null, "A", "test with space")

         GradleTestPattern.parse("A.test -- delimited and space") shouldBe
            GradleTestPattern(false, null, "A", "test -- delimited and space")

         GradleTestPattern.parse("com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionHandlingTest.an AfterProjectListenerException should add marker test") shouldBe
            GradleTestPattern(
               false,
               "com.sksamuel.kotest.runner.junit5",
               "AfterProjectListenerExceptionHandlingTest",
               "an AfterProjectListenerException should add marker test",
            )
      }
   }
}
