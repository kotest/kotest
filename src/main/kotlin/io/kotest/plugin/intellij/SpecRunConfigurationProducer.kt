package io.kotest.plugin.intellij

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.psi.buildSuggestedName
import io.kotest.plugin.intellij.psi.enclosingClassClassOrObjectToken
import io.kotest.plugin.intellij.psi.isDirectSubclassOfSpec

/**
 * A run configuration contains the details of a particular run (in the drop down run box).
 * A Run producer is called to configure a [KotestConfiguration] after it has been created.
 *
 * This producer creates run configurations for spec classes (run all).
 */
class SpecRunConfigurationProducer : LazyRunConfigurationProducer<KotestConfiguration>() {

   override fun getConfigurationFactory(): ConfigurationFactory = KotestConfigurationFactory(KotestConfigurationType)

   override fun setupConfigurationFromContext(configuration: KotestConfiguration,
                                              context: ConfigurationContext,
                                              sourceElement: Ref<PsiElement>): Boolean {
      val element = sourceElement.get()
      if (element != null && element is LeafPsiElement) {
         val ktclass = element.enclosingClassClassOrObjectToken()
         if (ktclass != null && ktclass.isDirectSubclassOfSpec()) {
            configuration.setSpec(ktclass)
            configuration.setModule(context.module)
            configuration.setGeneratedName()
            return true
         }
      }
      return false
   }

   // compares the existing configurations to the context in question
   // if one of the configurations matches then this should return true
   override fun isConfigurationFromContext(configuration: KotestConfiguration,
                                           context: ConfigurationContext): Boolean {
      val element = context.psiLocation
      if (element != null && element is LeafPsiElement) {
         val ktclass = element.enclosingClassClassOrObjectToken()
         if (ktclass != null) {
            return configuration.name == buildSuggestedName(ktclass.fqName?.asString(), null, null)
         }
      }
      return false
   }
}
