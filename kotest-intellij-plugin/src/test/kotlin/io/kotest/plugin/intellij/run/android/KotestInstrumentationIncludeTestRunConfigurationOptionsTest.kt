package io.kotest.plugin.intellij.run.android

import com.intellij.execution.PsiLocation
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtPsiFactory

class KotestInstrumentationIncludeTestRunConfigurationOptionsTest : BasePlatformTestCase() {

   fun testShouldAddFilter() {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MyTestClass { fun hello() {} }")
      KotestInstrumentationIncludeTestRunConfigurationOptions("a.b.c")
         .getExtraOptions(ConfigurationContext.createEmptyContextForLocation(PsiLocation(spec))) shouldBe
         listOf("-e INSTRUMENTATION_INCLUDE_PATTERN a.b.c")
   }

}
