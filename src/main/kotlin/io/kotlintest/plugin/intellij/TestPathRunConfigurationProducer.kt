package io.kotlintest.plugin.intellij

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import io.kotlintest.plugin.intellij.psi.BehaviorSpecStyle
import io.kotlintest.plugin.intellij.psi.DescribeSpecStyle
import io.kotlintest.plugin.intellij.psi.ExpectSpecStyle
import io.kotlintest.plugin.intellij.psi.FeatureSpecStyle
import io.kotlintest.plugin.intellij.psi.FreeSpecStyle
import io.kotlintest.plugin.intellij.psi.FunSpecStyle
import io.kotlintest.plugin.intellij.psi.ShouldSpecStyle
import io.kotlintest.plugin.intellij.psi.SpecStyle
import io.kotlintest.plugin.intellij.psi.StringSpecStyle
import io.kotlintest.plugin.intellij.psi.WordSpecStyle
import io.kotlintest.plugin.intellij.psi.buildSuggestedName
import io.kotlintest.plugin.intellij.psi.enclosingClass

abstract class TestPathRunConfigurationProducer(private val style: SpecStyle) :
    RunConfigurationProducer<KotlinTestRunConfiguration>(KotlinTestConfigurationType::class.java) {

  override fun setupConfigurationFromContext(configuration: KotlinTestRunConfiguration,
                                             context: ConfigurationContext,
                                             sourceElement: Ref<PsiElement>): Boolean {
    val element = sourceElement.get()
    if (element != null) {
      val testPath = style.testPath(element)
      if (testPath != null) {

        val ktclass = element.enclosingClass()
        if (ktclass != null) {

          configuration.setTestName(testPath)
          configuration.setSpec(ktclass)
          configuration.setModule(context.module)
          configuration.setGeneratedName()

          context.project.getComponent(ElementLocationCache::class.java).add(ktclass)

          return true
        }
      }
    }

    return false
  }

  // compares the existing configurations to the context in question
  // if one of the configurations matches then this should return true
  override fun isConfigurationFromContext(configuration: KotlinTestRunConfiguration,
                                          context: ConfigurationContext): Boolean {
    val element = context.psiLocation
    if (element != null) {
      val testPath = style.testPath(element)
      if (testPath != null) {
        val spec = element.enclosingClass()
        val name = buildSuggestedName(spec?.fqName?.asString(), testPath)
        return configuration.name == name
      }
    }
    return false
  }
}

class FunSpecRunConfigurationProducer : TestPathRunConfigurationProducer(FunSpecStyle)
class BehaviorSpecRunConfigurationProducer : TestPathRunConfigurationProducer(BehaviorSpecStyle)
class ShouldSpecRunConfigurationProducer : TestPathRunConfigurationProducer(ShouldSpecStyle)
class StringSpecRunConfigurationProducer : TestPathRunConfigurationProducer(StringSpecStyle)
class WordSpecRunConfigurationProducer : TestPathRunConfigurationProducer(WordSpecStyle)
class FeatureSpecRunConfigurationProducer : TestPathRunConfigurationProducer(FeatureSpecStyle)
class ExpectSpecRunConfigurationProducer : TestPathRunConfigurationProducer(ExpectSpecStyle)
class FreeSpecRunConfigurationProducer : TestPathRunConfigurationProducer(FreeSpecStyle)
class DescribeSpecRunConfigurationProducer : TestPathRunConfigurationProducer(DescribeSpecStyle)