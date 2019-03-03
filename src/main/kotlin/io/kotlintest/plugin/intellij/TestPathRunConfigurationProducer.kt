package io.kotlintest.plugin.intellij

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.ConfigurationFromContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import io.kotlintest.plugin.intellij.styles.BehaviorSpecStyle
import io.kotlintest.plugin.intellij.styles.DescribeSpecStyle
import io.kotlintest.plugin.intellij.styles.ExpectSpecStyle
import io.kotlintest.plugin.intellij.styles.FeatureSpecStyle
import io.kotlintest.plugin.intellij.styles.FreeSpecStyle
import io.kotlintest.plugin.intellij.styles.FunSpecStyle
import io.kotlintest.plugin.intellij.styles.ShouldSpecStyle
import io.kotlintest.plugin.intellij.styles.SpecStyle
import io.kotlintest.plugin.intellij.styles.StringSpecStyle
import io.kotlintest.plugin.intellij.styles.WordSpecStyle
import io.kotlintest.plugin.intellij.styles.buildSuggestedName
import io.kotlintest.plugin.intellij.styles.enclosingClass
import removeJUnitRunConfigs

abstract class TestPathRunConfigurationProducer(private val style: SpecStyle) :
    RunConfigurationProducer<KotlinTestRunConfiguration>(KotlinTestConfigurationType()) {

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
          removeJUnitRunConfigs(context.project, ktclass.fqName!!.shortName().asString())
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

  override fun isPreferredConfiguration(self: ConfigurationFromContext?, other: ConfigurationFromContext?): Boolean {
    return true
  }

  override fun shouldReplace(self: ConfigurationFromContext, other: ConfigurationFromContext): Boolean {
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