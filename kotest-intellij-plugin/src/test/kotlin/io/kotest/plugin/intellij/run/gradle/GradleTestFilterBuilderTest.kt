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

   fun testSinglePeriodInTestNameIsReplacedWithWildcard() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MyTestClass { fun hello() {} }")
      val test = Test(
         name = TestName(prefix = null, name = "version 1.0 test", interpolated = false),
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
         .build(true) shouldBe "--tests 'MyTestClass.version 1*0 test'"
   }

   fun testMultiplePeriodsInTestNameAreReplacedWithWildcards() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MyTestClass { fun hello() {} }")
      val test = Test(
         name = TestName(prefix = null, name = "my method with 1.2.3 periods", interpolated = false),
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
         .build(true) shouldBe "--tests 'MyTestClass.my method with 1*2*3 periods'"
   }

   fun testPeriodsInNestedTestNameAreReplacedWithWildcards() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MyTestClass { fun hello() {} }")
      val root = Test(
         name = TestName(prefix = null, name = "context 1.0", interpolated = false),
         parent = null,
         specClassName = spec,
         testType = TestType.Container,
         xdisabled = false,
         psi = spec,
         isDataTest = false
      )
      val test = Test(
         name = TestName(prefix = null, name = "test 2.5.1 behaviour", interpolated = false),
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
         .build(true) shouldBe "--tests 'MyTestClass.context 1*0 -- test 2*5*1 behaviour'"
   }

   fun testPeriodAtStartOfTestNameIsReplacedWithWildcard() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MyTestClass { fun hello() {} }")
      val test = Test(
         name = TestName(prefix = null, name = ".leading period", interpolated = false),
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
         .build(true) shouldBe "--tests 'MyTestClass.*leading period'"
   }

   fun testPeriodAtEndOfTestNameIsReplacedWithWildcard() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MyTestClass { fun hello() {} }")
      val test = Test(
         name = TestName(prefix = null, name = "trailing period.", interpolated = false),
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
         .build(true) shouldBe "--tests 'MyTestClass.trailing period*'"
   }

   fun testPeriodAndSingleQuoteCombinedAreHandledCorrectly() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MyTestClass { fun hello() {} }")
      val test = Test(
         name = TestName(prefix = null, name = "it's version 1.0", interpolated = false),
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
         .build(true) shouldBe "--tests 'MyTestClass.it'\\''s version 1*0'"
   }

   fun testPeriodAndNewlineCombinedAreHandledCorrectly() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MyTestClass { fun hello() {} }")
      val test = Test(
         name = TestName(prefix = null, name = "version\n1.0 test", interpolated = false),
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
         .build(true) shouldBe "--tests 'MyTestClass.version 1*0 test'"
   }
}
