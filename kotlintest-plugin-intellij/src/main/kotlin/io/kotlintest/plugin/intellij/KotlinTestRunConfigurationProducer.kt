package io.kotlintest.plugin.intellij

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.ConfigurationFromContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import io.kotlintest.plugin.intellij.runmarker.behaviorSpecTestName
import io.kotlintest.plugin.intellij.runmarker.enclosingClassName
import io.kotlintest.plugin.intellij.runmarker.isBehaviorSpecElement

class KotlinTestRunConfigurationProducer :
    RunConfigurationProducer<KotlinTestRunConfiguration>(KotlinTestConfigurationType::class.java) {

  override fun setupConfigurationFromContext(configuration: KotlinTestRunConfiguration,
                                             context: ConfigurationContext,
                                             sourceElement: Ref<PsiElement>): Boolean {
    val element = sourceElement.get()!!
    return if (element.isBehaviorSpecElement()) {
      configuration.setTestName(element.behaviorSpecTestName()!!)
      configuration.setSpecName(element.enclosingClassName()!!)
      configuration.setModule(context.module)
      configuration.setGeneratedName()
      true
    } else {
      false
    }
  }

  override fun isPreferredConfiguration(self: ConfigurationFromContext?, other: ConfigurationFromContext?): Boolean {
    // a kotlintest config will trump anything else
    return self?.configuration is KotlinTestRunConfiguration
  }

  override fun shouldReplace(self: ConfigurationFromContext, other: ConfigurationFromContext): Boolean {
    return self.configuration is KotlinTestRunConfiguration
  }

  override fun createConfigurationFromContext(context: ConfigurationContext?): ConfigurationFromContext? {
    return super.createConfigurationFromContext(context)
  }

  override fun isConfigurationFromContext(configuration: KotlinTestRunConfiguration, context: ConfigurationContext?): Boolean {
    val name = context?.psiLocation?.behaviorSpecTestName()
    return configuration.name == name
  }
}