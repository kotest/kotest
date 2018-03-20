package io.kotlintest.intellij

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement

class KotlinTestRunConfigurationProducer : RunConfigurationProducer<KotlinTestRunConfiguration>(
    ConfigurationTypeUtil.findConfigurationType(KotlinTestConfigurationType::class.java)
) {

  val logger = Logger.getInstance("io.kotlintest")

  override fun isConfigurationFromContext(configuration: KotlinTestRunConfiguration?,
                                          context: ConfigurationContext?): Boolean {
    val fqn = context?.psiLocation?.containingSpecName()
    return configuration?.specFQN == fqn
  }

  override fun setupConfigurationFromContext(configuration: KotlinTestRunConfiguration?,
                                             context: ConfigurationContext?,
                                             sourceElement: Ref<PsiElement>?): Boolean {
    val fqn = context?.psiLocation?.containingSpecName()
    return if (fqn == null) {
      false
    } else {
      configuration?.specFQN = fqn
      true
    }
  }
}