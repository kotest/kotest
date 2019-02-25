package io.kotlintest.plugin.intellij

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotlintest.plugin.intellij.psi.buildSuggestedName
import io.kotlintest.plugin.intellij.psi.enclosingClassOrObjectForClassOrObjectToken
import io.kotlintest.plugin.intellij.psi.isSpec

class SpecRunConfigurationProducer :
    RunConfigurationProducer<KotlinTestRunConfiguration>(KotlinTestConfigurationType::class.java) {

  override fun setupConfigurationFromContext(configuration: KotlinTestRunConfiguration,
                                             context: ConfigurationContext,
                                             sourceElement: Ref<PsiElement>): Boolean {
    val element = sourceElement.get()
    if (element != null && element is LeafPsiElement) {
      val ktclass = element.enclosingClassOrObjectForClassOrObjectToken()
      if (ktclass != null && ktclass.isSpec()) {
        configuration.setSpec(ktclass)
        configuration.setModule(context.module)
        configuration.setGeneratedName()

        context.project.getComponent(ElementLocationCache::class.java).add(ktclass)

        return true
      }
    }
    return false
  }

  // compares the existing configurations to the context in question
  // if one of the configurations matches then this should return true
  override fun isConfigurationFromContext(configuration: KotlinTestRunConfiguration,
                                          context: ConfigurationContext): Boolean {
    val element = context.psiLocation
    if (element != null && element is LeafPsiElement) {
      val ktclass = element.enclosingClassOrObjectForClassOrObjectToken()
      if (ktclass != null) {
        return configuration.name == buildSuggestedName(ktclass.fqName?.asString(), null)
      }
    }
    return false
  }
}