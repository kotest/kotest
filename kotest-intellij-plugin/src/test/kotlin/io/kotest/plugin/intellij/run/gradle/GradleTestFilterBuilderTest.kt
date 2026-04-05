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

   fun testNewlineInTestNameIsReplacedWithSpace() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MyTestClass { fun hello() {} }")
      val test = Test(
         name = TestName(prefix = null, name = "a test\nwith newline", interpolated = false),
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
         .build(true) shouldBe "--tests 'MyTestClass.a test with newline'"
   }

   fun testCrlfInTestNameIsReplacedWithSpace() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MyTestClass { fun hello() {} }")
      val test = Test(
         name = TestName(prefix = null, name = "a test\r\nwith crlf", interpolated = false),
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
         .build(true) shouldBe "--tests 'MyTestClass.a test with crlf'"
   }

   fun testCarriageReturnInTestNameIsReplacedWithSpace() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MyTestClass { fun hello() {} }")
      val test = Test(
         name = TestName(prefix = null, name = "a test\rwith cr", interpolated = false),
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
         .build(true) shouldBe "--tests 'MyTestClass.a test with cr'"
   }

   fun testPeriodInTestNameIsReplacedWithWildcard() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MySpec { fun hello() {} }")
      val test = Test(
         name = TestName(prefix = null, name = "test with 1.2.3", interpolated = false),
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
         .build(true) shouldBe "--tests 'MySpec.test with 1*2*3'"
   }

   fun testMultiplePeriodsInTestNameAreAllReplacedWithWildcards() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MySpec { fun hello() {} }")
      val test = Test(
         name = TestName(prefix = null, name = "assert a.b equals c.d.e", interpolated = false),
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
         .build(true) shouldBe "--tests 'MySpec.assert a*b equals c*d*e'"
   }

   fun testPeriodInNestedTestNameIsReplacedWithWildcard() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MySpec { fun hello() {} }")
      val root = Test(
         name = TestName(prefix = null, name = "context with v1.0", interpolated = false),
         parent = null,
         specClassName = spec,
         testType = TestType.Container,
         xdisabled = false,
         psi = spec,
         isDataTest = false
      )
      val test = Test(
         name = TestName(prefix = null, name = "name with periods 1.2.3 and more", interpolated = false),
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
         .build(true) shouldBe "--tests 'MySpec.context with v1*0 -- name with periods 1*2*3 and more'"
   }

   fun testNewlineInNestedTestNameIsReplacedWithSpace() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MyTestClass { fun hello() {} }")
      val root = Test(
         name = TestName(prefix = null, name = "parent\ncontext", interpolated = false),
         parent = null,
         specClassName = spec,
         testType = TestType.Container,
         xdisabled = false,
         psi = spec,
         isDataTest = false
      )
      val test = Test(
         name = TestName(prefix = null, name = "child\ntest", interpolated = false),
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
         .build(true) shouldBe "--tests 'MyTestClass.parent context -- child test'"
   }
}
