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
      GradleTestFilterBuilder.builder().withSpec(spec).build() shouldBe ""
   }

   fun testWithClass() {
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
      GradleTestFilterBuilder.builder().withSpec(spec).withTest(test).build() shouldBe ""
   }

}
