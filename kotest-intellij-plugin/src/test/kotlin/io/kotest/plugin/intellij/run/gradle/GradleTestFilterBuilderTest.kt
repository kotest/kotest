package io.kotest.plugin.intellij.run.gradle

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.kotest.matchers.shouldBe
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.TestName
import io.kotest.plugin.intellij.TestType
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtPsiFactory

class GradleTestFilterBuilderTest : BasePlatformTestCase() {

   fun testWithSpec() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MyTestClass { fun hello() {} }")
      GradleTestFilterBuilder.builder().withSpec(spec).build(true) shouldBe "--tests 'MyTestClass'"
   }

   fun testWithSpecAndTest() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MyTestClass { fun hello() {} }")
      val test = Test(
         name = TestName(prefix = null, name = "foo", interpolated = false),
         parent = null,
         specClassName = spec,
         testType = TestType.Container,
         xdisabled = false,
         psi = spec,
         isDataTest = false
      )
      GradleTestFilterBuilder.builder().withSpec(spec).withTest(test).build(true) shouldBe "--tests 'MyTestClass.foo'"
   }

   fun testWithoutTestsFlag() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MyTestClass { fun hello() {} }")
      val test = Test(
         name = TestName(prefix = null, name = "foo", interpolated = false),
         parent = null,
         specClassName = spec,
         testType = TestType.Container,
         xdisabled = false,
         psi = spec,
         isDataTest = false
      )
      GradleTestFilterBuilder.builder().withSpec(spec).withTest(test).build(false) shouldBe "'MyTestClass.foo'"
   }

   fun testWithSpecAndNestedTest() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MyTestClass { fun hello() {} }")
      val root = Test(
         name = TestName(prefix = null, name = "foo", interpolated = false),
         parent = null,
         specClassName = spec,
         testType = TestType.Container,
         xdisabled = false,
         psi = spec,
         isDataTest = false
      )
      val test = Test(
         name = TestName(prefix = null, name = "bar", interpolated = false),
         parent = root,
         specClassName = spec,
         testType = TestType.Test,
         xdisabled = false,
         psi = spec,
         isDataTest = false
      )
      GradleTestFilterBuilder.builder()
         .withSpec(spec)
         .withTest(test)
         .build(true) shouldBe "--tests 'MyTestClass.foo -- bar'"
   }

   fun testSingleQuoteInTestNameIsEscaped() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MyTestClass { fun hello() {} }")
      val test = Test(
         name = TestName(prefix = null, name = "it's a test", interpolated = false),
         parent = null,
         specClassName = spec,
         testType = TestType.Test,
         xdisabled = false,
         psi = spec,
         isDataTest = false
      )
      GradleTestFilterBuilder.builder()
         .withSpec(spec)
         .withTest(test)
         .build(true) shouldBe "--tests 'MyTestClass.it'\\''s a test'"
   }

   fun testSingleQuoteInNestedTestNameIsEscaped() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MyTestClass { fun hello() {} }")
      val root = Test(
         name = TestName(prefix = null, name = "parent's context", interpolated = false),
         parent = null,
         specClassName = spec,
         testType = TestType.Container,
         xdisabled = false,
         psi = spec,
         isDataTest = false
      )
      val test = Test(
         name = TestName(prefix = null, name = "child's test", interpolated = false),
         parent = root,
         specClassName = spec,
         testType = TestType.Test,
         xdisabled = false,
         psi = spec,
         isDataTest = false
      )
      GradleTestFilterBuilder.builder()
         .withSpec(spec)
         .withTest(test)
         .build(true) shouldBe "--tests 'MyTestClass.parent'\\''s context -- child'\\''s test'"
   }

   fun testMultipleSingleQuotesInTestNameAreAllEscaped() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MyTestClass { fun hello() {} }")
      val test = Test(
         name = TestName(prefix = null, name = "it's 'special'", interpolated = false),
         parent = null,
         specClassName = spec,
         testType = TestType.Test,
         xdisabled = false,
         psi = spec,
         isDataTest = false
      )
      GradleTestFilterBuilder.builder()
         .withSpec(spec)
         .withTest(test)
         .build(false) shouldBe "'MyTestClass.it'\\''s '\\''special'\\'''"
   }

   fun testNewlineInTestNameIsStripped() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MyTestClass { fun hello() {} }")
      val test = Test(
         name = TestName(prefix = null, name = "line one\nline two", interpolated = false),
         parent = null,
         specClassName = spec,
         testType = TestType.Test,
         xdisabled = false,
         psi = spec,
         isDataTest = false
      )
      GradleTestFilterBuilder.builder()
         .withSpec(spec)
         .withTest(test)
         .build(true) shouldBe "--tests 'MyTestClass.line one line two'"
   }

   fun testCRLFInTestNameIsStripped() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MyTestClass { fun hello() {} }")
      val test = Test(
         name = TestName(prefix = null, name = "line one\r\nline two", interpolated = false),
         parent = null,
         specClassName = spec,
         testType = TestType.Test,
         xdisabled = false,
         psi = spec,
         isDataTest = false
      )
      GradleTestFilterBuilder.builder()
         .withSpec(spec)
         .withTest(test)
         .build(true) shouldBe "--tests 'MyTestClass.line one line two'"
   }

   fun testWhitespaceInTestNameIsTrimmed() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MyTestClass { fun hello() {} }")
      val test = Test(
         name = TestName(prefix = null, name = "  foo  ", interpolated = false),
         parent = null,
         specClassName = spec,
         testType = TestType.Test,
         xdisabled = false,
         psi = spec,
         isDataTest = false
      )
      GradleTestFilterBuilder.builder()
         .withSpec(spec)
         .withTest(test)
         .build(true) shouldBe "--tests 'MyTestClass.foo'"
   }

}
