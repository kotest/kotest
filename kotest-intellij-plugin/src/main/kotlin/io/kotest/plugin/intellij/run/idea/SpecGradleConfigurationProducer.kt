package io.kotest.plugin.intellij.run.idea

import com.intellij.execution.Location
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.ConfigurationFromContext
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.psi.asKtClassOrObjectOrNull
import io.kotest.plugin.intellij.run.RunnerMode
import io.kotest.plugin.intellij.run.RunnerModes
import org.jetbrains.kotlin.asJava.toLightClass
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.plugins.gradle.execution.test.runner.TestClassGradleConfigurationProducer
import org.jetbrains.plugins.gradle.service.execution.GradleRunConfiguration

/**
 * Runs a Kotest spec using the standard gradle `task` test.
 */
@Suppress("DEPRECATION")
@Deprecated("Starting with Kotest 6.1 the preferred method is use GradleMultiplatformJvmTestTaskRunProducer")
class SpecGradleConfigurationProducer : TestClassGradleConfigurationProducer() {

   private val logger = logger<SpecGradleConfigurationProducer>()

   /**
    * When two configurations are created from the same context by two different producers, checks if the
    * configuration created by this producer should be preferred over the other one.
    *
    * We return true when the other configuration is NOT a Gradle run configuration, to ensure Kotest specs
    * take priority over JUnit (which may claim the class due to Spring Boot test annotations like
    * `@SpringBootTest` that are meta-annotated with `@ExtendWith(SpringExtension.class)`).
    */
   override fun isPreferredConfiguration(self: ConfigurationFromContext, other: ConfigurationFromContext): Boolean {
      // Prefer this over non GradleRunConfiguration
      return other.configuration !is GradleRunConfiguration
   }

   /**
    * Returns true if [self] configuration should replace the other configuration.
    * We replace JUnit configurations when we detect a Kotest spec.
    */
   override fun shouldReplace(self: ConfigurationFromContext, other: ConfigurationFromContext): Boolean {
      // Replace non GradleRunConfiguration with this
      return other.configuration !is GradleRunConfiguration
   }

   override fun setupConfigurationFromContext(
      configuration: GradleRunConfiguration,
      context: ConfigurationContext,
      sourceElement: Ref<PsiElement?>
   ): Boolean {

      if (RunnerModes.mode(context.module) != RunnerMode.LEGACY) {
         logger.info("Runner mode is not LEGACY so this producer will not contribute")
         return false
      }

      return super.setupConfigurationFromContext(configuration, context, sourceElement)
   }

   override fun createConfigurationFromContext(context: ConfigurationContext): ConfigurationFromContext? {

      if (RunnerModes.mode(context.module) != RunnerMode.LEGACY) {
         logger.info("Runner mode is not LEGACY so this producer will not contribute")
         return null
      }

      return super.createConfigurationFromContext(context)
   }

   override fun getPsiClassForLocation(contextLocation: Location<*>): PsiClass? {
      val leaf = contextLocation.psiElement
      if (leaf is LeafPsiElement) {
         val spec = leaf.asKtClassOrObjectOrNull()
         if (spec is KtClass) {
            return spec.toLightClass()
         }
      }
      return super.getPsiClassForLocation(contextLocation)
   }
}
