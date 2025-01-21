package io.kotest.plugin.intellij.run

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.ConfigurationFromContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.KotestConfigurationFactory
import io.kotest.plugin.intellij.KotestConfigurationType
import io.kotest.plugin.intellij.KotestRunConfiguration
import io.kotest.plugin.intellij.gradle.GradleUtils
import io.kotest.plugin.intellij.psi.asKtClassOrObjectOrNull
import io.kotest.plugin.intellij.psi.isRunnableSpec
import org.jetbrains.kotlin.lexer.KtKeywordToken
import org.jetbrains.kotlin.lexer.KtToken
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * A run configuration supports creating run configurations from context (by right-clicking a code element in the source editor or the project view).
 *
 * This producer creates run configurations for spec classes (run all).
 */
class SpecRunConfigurationProducer : LazyRunConfigurationProducer<KotestRunConfiguration>() {

   override fun getConfigurationFactory(): ConfigurationFactory = KotestConfigurationFactory(KotestConfigurationType())

   override fun isPreferredConfiguration(self: ConfigurationFromContext?, other: ConfigurationFromContext?): Boolean {
      return false
   }

   /**
    * Determines if the context is applicable to this run configuration producer,
    * false if the context is not applicable and the configuration should be discarded.
    */
   override fun setupConfigurationFromContext(
      configuration: KotestRunConfiguration,
      context: ConfigurationContext,
      sourceElement: Ref<PsiElement>
   ): Boolean {

      // if we have the kotest plugin then we shouldn't use this
      if (GradleUtils.hasKotestTask(context.module)) return false

      val element = sourceElement.get()
      if (element != null && element is LeafPsiElement) {

         // we are interested in either the class/object keyword or the identifier associated with it
         val classOrObject: KtClassOrObject = when (element.elementType) {
            is KtKeywordToken -> element.asKtClassOrObjectOrNull()
            is KtToken -> element.parent.asKtClassOrObjectOrNull()
            else -> null
         } ?: return false

         if (classOrObject.isRunnableSpec()) {
            configuration.setSpec(classOrObject)
            configuration.setModule(context.module)
            configuration.name = generateName(classOrObject, null)
            return true
         }
      }
      return false
   }

   // compares the existing configurations to the context in question
   // if one of the configurations matches then this should return true
   override fun isConfigurationFromContext(
      configuration: KotestRunConfiguration,
      context: ConfigurationContext
   ): Boolean {

      // if we have the kotest plugin then we shouldn't use this
      if (GradleUtils.hasKotestTask(context.module)) return false

      val element = context.psiLocation
      if (element != null && element is LeafPsiElement) {
         val spec = element.asKtClassOrObjectOrNull() ?: return false
         if (spec.isRunnableSpec()) {
            return configuration.getTestPath().isNullOrBlank()
               && configuration.getPackageName().isNullOrBlank()
               && configuration.getSpecName() == spec.fqName?.asString()
         }
      }
      return false
   }
}
