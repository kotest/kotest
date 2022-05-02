package io.kotest.runner.junit.platform.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class GradleTestPatternParserTest : FunSpec() {
   init {

      test("parse package") {
         GradleTestPattern.parse("org.mypackage") shouldBe GradleTestPattern("org.mypackage", null, emptyList())
         GradleTestPattern.parse("org") shouldBe GradleTestPattern("org", null, emptyList())
      }

      test("parse Classname") {
         GradleTestPattern.parse("MyClass") shouldBe GradleTestPattern(null, "MyClass", emptyList())
         GradleTestPattern.parse("A") shouldBe GradleTestPattern(null, "A", emptyList())
      }

      test("parse package.Classname") {
         GradleTestPattern.parse("org.mypackage.MyClass") shouldBe GradleTestPattern("org.mypackage", "MyClass", emptyList())
         GradleTestPattern.parse("org.A") shouldBe GradleTestPattern("org", "A", emptyList())
      }

      test("parse package.Classname.path") {
         GradleTestPattern.parse("org.myPackage.MyClass.test") shouldBe
            GradleTestPattern("org.myPackage", "MyClass", listOf("test"))
         GradleTestPattern.parse("org.A.test with space") shouldBe GradleTestPattern("org", "A", listOf("test with space"))
         GradleTestPattern.parse("org.A.test -- delimited and space") shouldBe
            GradleTestPattern("org", "A", listOf("test", "delimited and space"))
      }

      test("parse Classname.path") {
         GradleTestPattern.parse("MyClass.test") shouldBe GradleTestPattern(null, "MyClass", listOf("test"))
         GradleTestPattern.parse("A.test with space") shouldBe GradleTestPattern(null, "A", listOf("test with space"))
         GradleTestPattern.parse("A.test -- delimited and space") shouldBe
            GradleTestPattern(null, "A", listOf("test", "delimited and space"))
         GradleTestPattern.parse("com.sksamuel.kotest.runner.junit5.AfterProjectListenerExceptionHandlingTest.an AfterProjectListenerException should add marker test") shouldBe
            GradleTestPattern(
               "com.sksamuel.kotest.runner.junit5",
               "AfterProjectListenerExceptionHandlingTest",
               listOf("an AfterProjectListenerException should add marker test"),
            )
      }
   }
}
