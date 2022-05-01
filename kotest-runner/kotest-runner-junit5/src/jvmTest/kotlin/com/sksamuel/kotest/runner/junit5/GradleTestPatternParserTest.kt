package com.sksamuel.kotest.runner.junit5

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit.platform.gradle.GradleTestPattern

class GradleTestPatternParserTest : FunSpec() {
   init {

      test("parse package") {
         GradleTestPattern.parse("org.mypackage") shouldBe GradleTestPattern("org.mypackage", null, null)
         GradleTestPattern.parse("org") shouldBe GradleTestPattern("org", null, null)
      }

      test("parse Classname") {
         GradleTestPattern.parse("MyClass") shouldBe GradleTestPattern(null, "MyClass", null)
         GradleTestPattern.parse("A") shouldBe GradleTestPattern(null, "A", null)
      }

      test("parse package.Classname") {
         GradleTestPattern.parse("org.mypackage.MyClass") shouldBe GradleTestPattern("org.mypackage", "MyClass", null)
         GradleTestPattern.parse("org.A") shouldBe GradleTestPattern("org", "A", null)
      }

      test("parse package.Classname.path") {
         GradleTestPattern.parse("org.myPackage.MyClass.test") shouldBe
            GradleTestPattern("org.myPackage", "MyClass", "test")
         GradleTestPattern.parse("org.A.test with space") shouldBe GradleTestPattern("org", "A", "test with space")
         GradleTestPattern.parse("org.A.test -- delimited and space") shouldBe
            GradleTestPattern("org", "A", "test -- delimited and space")
      }

      test("parse Classname.path") {
         GradleTestPattern.parse("MyClass.test") shouldBe GradleTestPattern(null, "MyClass", "test")
         GradleTestPattern.parse("A.test with space") shouldBe GradleTestPattern(null, "A", "test with space")
         GradleTestPattern.parse("A.test -- delimited and space") shouldBe
            GradleTestPattern(null, "A", "test -- delimited and space")
         GradleTestPattern.parse("com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionHandlingTest.an AfterProjectListenerException should add marker test") shouldBe
            GradleTestPattern(
               "com.sksamuel.kotest.runner.junit5",
               "AfterProjectListenerExceptionHandlingTest",
               "an AfterProjectListenerException should add marker test"
            )
      }
   }
}
