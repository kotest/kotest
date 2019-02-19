package io.kotlintest.plugin.intellij

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.ConfigurationFromContext
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
import io.kotlintest.plugin.intellij.psi.enclosingClassName

abstract class KotlinTestRunConfigurationProducer(private val style: SpecStyle) :
    RunConfigurationProducer<KotlinTestRunConfiguration>(KotlinTestConfigurationType::class.java) {

  override fun setupConfigurationFromContext(configuration: KotlinTestRunConfiguration,
                                             context: ConfigurationContext,
                                             sourceElement: Ref<PsiElement>): Boolean {
    val element = sourceElement.get()!!
    val name = style.testPath(element)
    return if (name == null) false else {
      configuration.setTestName(name)
      configuration.setSpecName(element.enclosingClassName()!!)
      configuration.setModule(context.module)
      configuration.setGeneratedName()
      true
    }
  }

  // compares the existing configurations to the context in question
  // if one of the configurations matches then this should return true
  override fun isConfigurationFromContext(configuration: KotlinTestRunConfiguration, context: ConfigurationContext): Boolean {
    val element = context.psiLocation
    val testName = if (element == null) null else style.testPath(element)
    val specName = element?.enclosingClassName()
    val name = buildSuggestedName(specName, testName)
    return configuration.name == name
  }

  override fun isPreferredConfiguration(self: ConfigurationFromContext?, other: ConfigurationFromContext?): Boolean {
    // a kotlintest config will trump anything else
    return self?.configuration is KotlinTestRunConfiguration
  }

  override fun shouldReplace(self: ConfigurationFromContext, other: ConfigurationFromContext): Boolean {
    return self.configuration is KotlinTestRunConfiguration
  }
}

class FunSpecRunConfigurationProducer : KotlinTestRunConfigurationProducer(FunSpecStyle)
class BehaviorSpecRunConfigurationProducer : KotlinTestRunConfigurationProducer(BehaviorSpecStyle)
class ShouldSpecRunConfigurationProducer : KotlinTestRunConfigurationProducer(ShouldSpecStyle)
class StringSpecRunConfigurationProducer : KotlinTestRunConfigurationProducer(StringSpecStyle)
class WordSpecRunConfigurationProducer : KotlinTestRunConfigurationProducer(WordSpecStyle)
class FeatureSpecRunConfigurationProducer : KotlinTestRunConfigurationProducer(FeatureSpecStyle)
class ExpectSpecRunConfigurationProducer : KotlinTestRunConfigurationProducer(ExpectSpecStyle)
class FreeSpecRunConfigurationProducer : KotlinTestRunConfigurationProducer(FreeSpecStyle)
class DescribeSpecRunConfigurationProducer : KotlinTestRunConfigurationProducer(DescribeSpecStyle)