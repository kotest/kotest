package io.kotest.plugin.intellij

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.ConfigurationFromContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.notifications.DependencyChecker
import io.kotest.plugin.intellij.psi.buildSuggestedName
import io.kotest.plugin.intellij.psi.enclosingClass
import io.kotest.plugin.intellij.styles.SpecStyle
import io.kotest.plugin.intellij.styles.Test

/**
 * A run configuration creates the details of a particular run (in the drop down run box).
 *
 * A Run producer is called to create a [KotestRunConfiguration] from the [KotestConfigurationFactory]
 * and then again to configure it with a context.
 *
 * This producer creates run configurations for individual tests.
 */
class TestPathRunConfigurationProducer : LazyRunConfigurationProducer<KotestRunConfiguration>() {

   /**
    * Returns the [KotestConfigurationFactory] used to create [KotestRunConfiguration]s.
    */
   override fun getConfigurationFactory(): ConfigurationFactory = KotestConfigurationFactory(KotestConfigurationType)

   /**
    * Returns true if the given context is applicable to this run producer.
    * This implementation will return true if the source element is a test in any of the [SpecStyle]s.
    */
   override fun setupConfigurationFromContext(configuration: KotestRunConfiguration,
                                              context: ConfigurationContext,
                                              sourceElement: Ref<PsiElement>): Boolean {

      val element = sourceElement.get()
      if (element != null) {
         val test = findTest(element)
         if (test != null) {

            val ktclass = element.enclosingClass()
            if (ktclass != null) {

               if (context.module != null)
                  if (!DependencyChecker.checkMissingDependencies(context.module)) return false

               configuration.setTestName(test.path)
               configuration.setSpec(ktclass)
               configuration.setModule(context.module)
               configuration.setGeneratedName()

               return true
            }
         }
      }

      return false
   }

   // compares the existing configurations to the context in question
   // if one of the configurations matches then this should return true
   override fun isConfigurationFromContext(configuration: KotestRunConfiguration,
                                           context: ConfigurationContext): Boolean {
      val element = context.psiLocation
      if (element != null) {
         val test = findTest(element)
         if (test != null) {
            val spec = element.enclosingClass()
            val name = buildSuggestedName(spec?.fqName?.asString(), test.path)
            return configuration.name == name
         }
      }
      return false
   }

   private fun findTest(element: PsiElement): Test? {
      return SpecStyle.styles.asSequence()
         .filter { it.isContainedInSpec(element) }
         .mapNotNull { it.findAssociatedTest(element) }
         .firstOrNull()
   }

   /**
    * When two configurations are created from the same context by two different producers, checks if the configuration created by
    * this producer should be discarded in favor of the other one.
    *
    * We always return true because no one else should be creating Kotest configurations.
    */
   override fun isPreferredConfiguration(self: ConfigurationFromContext?, other: ConfigurationFromContext?): Boolean {
      return true
   }

   override fun shouldReplace(self: ConfigurationFromContext, other: ConfigurationFromContext): Boolean {
      return false
   }
}
