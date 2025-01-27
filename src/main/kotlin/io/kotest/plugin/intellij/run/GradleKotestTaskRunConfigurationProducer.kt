package io.kotest.plugin.intellij.run

import com.intellij.execution.JavaRunConfigurationExtensionManager
import com.intellij.execution.RunManager
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.ConfigurationFromContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.gradle.GradleUtils
import io.kotest.plugin.intellij.psi.enclosingKtClass
import io.kotest.plugin.intellij.psi.enclosingSpec
import io.kotest.plugin.intellij.styles.SpecStyle
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.plugins.gradle.execution.build.CachedModuleDataFinder
import org.jetbrains.plugins.gradle.service.execution.GradleExternalTaskConfigurationType
import org.jetbrains.plugins.gradle.service.execution.GradleRunConfiguration
import org.jetbrains.plugins.gradle.util.GradleModuleData

/**
 * Runs a Kotest individual test or spec using the `kotest` gradle task.
 *
 * This uses a [GradleRunConfiguration] which is an intellij provided ExternalSystemRunConfiguration
 * that runs gradle tasks.
 *
 * Intellij 242+ provides a GradleRunConfigurationProducer but that isn't part of 241, so until that is
 * no longer supported we will need to extend [LazyRunConfigurationProducer].
 */
class GradleKotestTaskRunConfigurationProducer : LazyRunConfigurationProducer<GradleRunConfiguration>() {

   override fun getConfigurationFactory(): ConfigurationFactory {
      return GradleExternalTaskConfigurationType.getInstance().factory
   }

   /**
    * When two configurations are created from the same context by two different producers, checks if the
    * configuration created by this producer should be discarded in favor of the other one.
    */
   override fun isPreferredConfiguration(self: ConfigurationFromContext, other: ConfigurationFromContext): Boolean {
      // we need this gradle configuration to take precedence over the previous kotest run that ran a java process directly,
      // but we only want to do this if the user has enabled the kotest gradle plugin
      // we don't have access to the module at this point, but we can assume that the presence of this configuration
      // means the [isConfigurationFromContext] set it up and did the detection for us.
      return true
   }

   /**
    * Returns true if this configuration should replace the other configuration.
    *
    * // todo determine what the logic should be here, sometimes we create a new configuration, sometimes we don't,
    *
    * notes: seems to be invoked when showing the line marker drop downs
    */
   override fun shouldReplace(self: ConfigurationFromContext, other: ConfigurationFromContext): Boolean {
      println("Should replace ${self.configuration.name} over ${other.configuration.name}")
      // we need this gradle configuration to take precedence over the previous kotest run that ran a java process directly,
      // but we only want to do this if the user has enabled the kotest gradle plugin
      // we don't have access to the module at this point, but we can assume that the presence of this configuration
      // means the [isConfigurationFromContext] set it up and did the detection for us.
      return true
   }

   /**
    *
    * This function is called to customize the blank [configuration] if it is applicable to the [context].
    * If the given configuration is not something this producer is interested in, then it should return false.
    *
    * Receives a blank configuration of the specified type and a [context] containing information about
    * a source code location (accessible by calling getLocation() or getPsiLocation()). The implementation
    * needs to check whether the location is applicable to the configuration type
    * (e.g., if it is in a file of the supported language). If it is, put the correct context-specific
    * settings into the run configuration and return true.
    *
    * Return false otherwise.
    */
   @Suppress("UnstableApiUsage")
   override fun setupConfigurationFromContext(
      configuration: GradleRunConfiguration,
      context: ConfigurationContext,
      sourceElement: Ref<PsiElement>
   ): Boolean {

      // we must have kotest as a task configured in gradle for this run producer to be applicable
      if (!GradleUtils.hasGradlePlugin(context.module)) return false

      val project = context.project ?: return false
      val module = context.module ?: return false
      val gradleModuleData = CachedModuleDataFinder.getGradleModuleData(module) ?: return false

      // we must have the element we clicked on as we are running from the gutter
      val element = sourceElement.get()
      if (element == null) return false

      // we must be in a class or object to define tests,
      // and we will use the FQN of that class or object as the candidates class list, so the kotest
      // launcher doesn't need to be passed more than one class
      val spec = element.enclosingSpec() ?: return false
      val test = SpecStyle.findTest(element)
      println("Test = $test")

      // this is the path to the project on the file system
      val externalProjectPath = GradleUtils.resolveProjectPath(module) ?: return false

      // this is the psi element associated with the run, needed by the java run extension manager
      val location = context.location ?: return false

      configuration.name = configurationName(spec, test)
      configuration.isDebugServerProcess = false
      // if we set this to true then intellij will send output to its own gradle test console,
      // but we want to display our own SMTestRunnerConsole
      configuration.isRunAsTest = false
      configuration.putUserData<Boolean>(Key.create<Boolean>("kotest"), true)

      val runManager = RunManager.getInstance(project)
      runManager.setUniqueNameIfNeeded(configuration)

      // note: configuration.settings.externalSystemId is set for us
      configuration.settings.externalProjectPath = externalProjectPath
      configuration.settings.scriptParameters = ""
      configuration.settings.taskNames = taskNames(gradleModuleData, spec, test)
      println("Task names: " + configuration.settings.taskNames.toString())

      JavaRunConfigurationExtensionManager.instance.extendCreatedConfiguration(configuration, location)
      return true
   }

   private fun configurationName(spec: KtClassOrObject, test: Test?): String {
      return GradleTestRunNameBuilder.builder()
         .withSpec(spec)
         .withTest(test)
         .build()
   }

   @Suppress("UnstableApiUsage")
   private fun taskNames(gradleModuleData: GradleModuleData, spec: KtClassOrObject, test: Test?): List<String> {
      return GradleTaskNamesBuilder.builder(gradleModuleData)
         .withCandidate(spec)
         .withTest(test)
         .build()
   }

   /**
    * Checks if a configuration was created from the specified context.
    * This method allows reusing an existing run configuration, which applies to the current context,
    * instead of creating a new one and possibly ignoring the user's customizations in the existing one
    */
   override fun isConfigurationFromContext(
      configuration: GradleRunConfiguration,
      context: ConfigurationContext
   ): Boolean {

      // we must have kotest as a task configured in gradle for this run producer to be applicable
      if (!GradleUtils.hasGradlePlugin(context.module)) return false

      // if kotest is not a task this configuration is running, then this isn't a configuration we can re-use
      // eg, we might be passed another gradle run configuration that was running build or clean etc.
      // we just see if any of the tasks start with kotest, eg kotestJs, kotestJvm or just the plain kotest task
      if (!GradleUtils.hasKotestTask(configuration.settings.taskNames)) return false

      val element = context.psiLocation
      if (element != null) {
         val test = SpecStyle.findTest(element)
         if (test != null) {
            val specClass: KtClass? = element.enclosingKtClass() ?: return false
            configuration.settings.taskNames.forEach { println("Trying to find task name $it") }

            // todo we need to compare the test path with the test path from the context
            // for now we'll just make a new one each time as we figure it out

//            return configuration.getTestPath() == test.testPath()
//               && configuration.getPackageName().isNullOrBlank()
//               && configuration.getSpecName() == spec?.fqName?.asString()
         }
      }
      return false
   }
}

