package io.kotest.plugin.intellij.run

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.ConfigurationFromContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.KotestConfigurationFactory
import io.kotest.plugin.intellij.KotestConfigurationType
import io.kotest.plugin.intellij.KotestRunConfiguration
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.gradle.GradleUtils
import io.kotest.plugin.intellij.psi.enclosingKtClass
import io.kotest.plugin.intellij.styles.SpecStyle

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
   override fun getConfigurationFactory(): ConfigurationFactory = KotestConfigurationFactory(KotestConfigurationType())

   /**
    * Returns true if the given context is applicable to this run producer.
    * This implementation will return true if the source element is a test in any of the [SpecStyle]s.
    */
   override fun setupConfigurationFromContext(configuration: KotestRunConfiguration,
                                              context: ConfigurationContext,
                                              sourceElement: Ref<PsiElement>): Boolean {

// if we have the kotest plugin then we shouldn't use this
      if (GradleUtils.hasGradlePlugin(context.module)) return false

      val element = sourceElement.get()
      if (element != null) {
         val test = findTest(element)
         if (test != null) {

            val ktclass = element.enclosingKtClass()
            if (ktclass != null) {
               configuration.setTestPath(test.testPath())
               configuration.setSpec(ktclass)
               configuration.setModule(context.module)
               configuration.name = generateName(ktclass, test)

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

      // if we have the kotest plugin then we shouldn't use this
      if (GradleUtils.hasGradlePlugin(context.module)) return false

      val element = context.psiLocation
      if (element != null) {
         val test = findTest(element)
         if (test != null) {
            val spec = element.enclosingKtClass()
            return configuration.getTestPath() == test.testPath()
               && configuration.getPackageName().isNullOrBlank()
               && configuration.getSpecName() == spec?.fqName?.asString()
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
      return false
   }

   override fun shouldReplace(self: ConfigurationFromContext, other: ConfigurationFromContext): Boolean {
      return false
   }
}
