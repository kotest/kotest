package io.kotest.plugin.intellij.run.gradle

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.kotest.matchers.shouldBe
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.TestName
import io.kotest.plugin.intellij.TestType
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtPsiFactory

class GradleTestRunNameBuilderTest : BasePlatformTestCase() {

   fun testWithSpec() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MyTestClass { fun hello() {} }")
      GradleTestRunNameBuilder.builder().withSpec(spec).build() shouldBe "MyTestClass"
   }

   fun testWithRootTest() {
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
      GradleTestRunNameBuilder.builder().withSpec(spec).withTest(test).build() shouldBe "MyTestClass.foo"
   }


   fun testWithNestedTest() {
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
      GradleTestRunNameBuilder.builder().withSpec(spec).withTest(test).build() shouldBe "MyTestClass.foo bar"
   }
}
