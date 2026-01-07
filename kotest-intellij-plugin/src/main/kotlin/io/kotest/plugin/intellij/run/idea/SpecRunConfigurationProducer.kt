package io.kotest.plugin.intellij.run.idea

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.ConfigurationFromContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.dependencies.ModuleDependencies
import io.kotest.plugin.intellij.gradle.GradleUtils
import io.kotest.plugin.intellij.psi.asKtClassOrObjectOrNull
import io.kotest.plugin.intellij.psi.isRunnableSpec
import org.jetbrains.kotlin.lexer.KtKeywordToken
import org.jetbrains.plugins.gradle.service.execution.GradleRunConfiguration
import org.jetbrains.kotlin.lexer.KtToken
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * A run configuration supports creating run configurations from context (by right-clicking a code element in the source editor or the project view).
 *
 * This producer creates run configurations for spec classes (run all).
 */
@Deprecated("Starting with Kotest 6 the preferred method is to run via gradle")
class SpecRunConfigurationProducer : LazyRunConfigurationProducer<KotestRunConfiguration>() {

   override fun getConfigurationFactory(): ConfigurationFactory = KotestConfigurationFactory(KotestConfigurationType())

   /**
    * When two configurations are created from the same context by two different producers, checks if the
    * configuration created by this producer should be preferred over the other one.
    *
    * We return true when the other configuration is NOT a Kotest configuration and NOT a Gradle configuration,
    * to ensure Kotest specs take priority over JUnit (which may claim the class due to Spring Boot test annotations
    * like `@SpringBootTest` that are meta-annotated with `@ExtendWith(SpringExtension.class)`).
    *
    * We do NOT prefer this IDEA-based runner over Gradle configurations, as Gradle is the preferred method
    * for running Kotest tests starting with Kotest 6.
    */
   override fun isPreferredConfiguration(self: ConfigurationFromContext?, other: ConfigurationFromContext?): Boolean {
      val otherConfig = other?.configuration
      // Don't prefer over Gradle or other Kotest configurations
      if (otherConfig is GradleRunConfiguration || otherConfig is KotestRunConfiguration) return false
      // Prefer Kotest over non-Kotest, non-Gradle configurations (like JUnit)
      return true
   }

   /**
    * Returns true if this configuration should replace the other configuration.
    * We replace JUnit configurations when we detect a Kotest spec, but NOT Gradle configurations.
    */
   override fun shouldReplace(self: ConfigurationFromContext, other: ConfigurationFromContext): Boolean {
      val otherConfig = other.configuration
      // Don't replace Gradle or other Kotest configurations
      if (otherConfig is GradleRunConfiguration || otherConfig is KotestRunConfiguration) return false
      // Replace non-Kotest, non-Gradle configurations (like JUnit) with Kotest
      return true
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
      if (GradleUtils.hasGradlePlugin(context.module)) return false

      if (!ModuleDependencies.hasKotest(context.module)) return false

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
            configuration.setSpecsName(classOrObject.fqName?.asString() ?: "")
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
      if (GradleUtils.hasGradlePlugin(context.module)) return false

      if (!ModuleDependencies.hasKotest(context.module)) return false

      val element = context.psiLocation
      if (element != null && element is LeafPsiElement) {
         val spec = element.asKtClassOrObjectOrNull() ?: return false
         if (spec.isRunnableSpec()) {
            return configuration.getTestPath().isNullOrBlank() && configuration.getSpecName() == spec.fqName?.asString()
         }
      }
      return false
   }
}
