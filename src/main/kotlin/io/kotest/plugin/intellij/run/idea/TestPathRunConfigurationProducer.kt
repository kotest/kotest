package io.kotest.plugin.intellij.run.idea

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.ConfigurationFromContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.dependencies.ModuleDependencies
import io.kotest.plugin.intellij.gradle.GradleUtils
import io.kotest.plugin.intellij.psi.enclosingKtClass
import io.kotest.plugin.intellij.styles.SpecStyle
import org.jetbrains.plugins.gradle.service.execution.GradleRunConfiguration

/**
 * A run configuration creates the details of a particular run (in the drop down run box).
 *
 * A Run producer is called to create a [KotestRunConfiguration] from the [KotestConfigurationFactory]
 * and then again to configure it with a context.
 *
 * This producer creates run configurations for individual tests.
 */
@Deprecated("Starting with Kotest 6 the preferred method is to run via gradle")
class TestPathRunConfigurationProducer : LazyRunConfigurationProducer<KotestRunConfiguration>() {

   /**
    * Returns the [KotestConfigurationFactory] used to create [KotestRunConfiguration]s.
    */
   override fun getConfigurationFactory(): ConfigurationFactory = KotestConfigurationFactory(KotestConfigurationType())

   /**
    * Returns true if the given context is applicable to this run producer.
    * This implementation will return true if the source element is a test in any of the [SpecStyle]s.
    */
   override fun setupConfigurationFromContext(
      configuration: KotestRunConfiguration,
      context: ConfigurationContext,
      sourceElement: Ref<PsiElement>
   ): Boolean {

      // if we have the kotest plugin then we shouldn't use this
      if (GradleUtils.hasGradlePlugin(context.module)) return false

      // if we don't have the kotest engine on the classpath then we shouldn't use this producer
      if (!ModuleDependencies.hasKotest(context.module)) return false

      val element = sourceElement.get()
      if (element != null) {
         val test = findTest(element)
         if (test != null) {

            val ktclass = element.enclosingKtClass()
            if (ktclass != null) {
               if (test.isDataTest) {
                  configuration.setTestPath(null)
                  configuration.setInclude(null)
               } else {
                  configuration.setTestPath(test.testPath())
                  configuration.setInclude(test.descriptorPath())
               }
               configuration.setSpecsName(ktclass.fqName?.asString().toString())
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
   override fun isConfigurationFromContext(
      configuration: KotestRunConfiguration,
      context: ConfigurationContext
   ): Boolean {

      // if we have the kotest plugin then we shouldn't use this
      if (GradleUtils.hasGradlePlugin(context.module)) return false

      // if we don't have the kotest engine on the classpath then we shouldn't use this producer
      if (!ModuleDependencies.hasKotest(context.module)) return false

      val element = context.psiLocation
      if (element != null) {
         val test = findTest(element)
         if (test != null) {
            val spec = element.enclosingKtClass()
            return configuration.getTestPath() == test.testPath()
               && configuration.getPackageName().isNullOrBlank()
               && configuration.getInclude() == test.descriptorPath()
               && configuration.getSpecName() == spec?.fqName?.asString()
         }
      }
      return false
   }

   private fun findTest(element: PsiElement): Test? {
      return SpecStyle.Companion.styles.asSequence()
         .filter { it.isContainedInSpec(element) }
         .mapNotNull { it.findAssociatedTest(element) }
         .firstOrNull()
   }

   /**
    * When two configurations are created from the same context by two different producers, checks if the configuration created by
    * this producer should be preferred over the other one.
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
}
