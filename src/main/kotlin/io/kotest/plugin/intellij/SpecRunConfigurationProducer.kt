package io.kotest.plugin.intellij

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.styles.buildSuggestedName
import io.kotest.plugin.intellij.styles.enclosingClassOrObjectForClassOrObjectToken
import io.kotest.plugin.intellij.styles.isAnySpecSubclass
import removeJUnitRunConfigs

class SpecRunConfigurationProducer :
    RunConfigurationProducer<KotestRunConfiguration>(KotestConfigurationType()) {

  override fun setupConfigurationFromContext(configuration: KotestRunConfiguration,
                                             context: ConfigurationContext,
                                             sourceElement: Ref<PsiElement>): Boolean {
    val element = sourceElement.get()
    if (element != null && element is LeafPsiElement) {
      val ktclass = element.enclosingClassOrObjectForClassOrObjectToken()
      if (ktclass != null && ktclass.isAnySpecSubclass()) {
        configuration.setSpec(ktclass)
        configuration.setModule(context.module)
        configuration.setGeneratedName()

        context.project.getComponent(io.kotest.plugin.intellij.ElementLocationCache::class.java).add(ktclass)
        removeJUnitRunConfigs(context.project, ktclass.fqName!!.shortName().asString())
        return true
      }
    }
    return false
  }

  // compares the existing configurations to the context in question
  // if one of the configurations matches then this should return true
  override fun isConfigurationFromContext(configuration: KotestRunConfiguration,
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