package io.kotest.plugin.intellij.run.gradle

import com.android.tools.idea.testartifacts.instrumented.AndroidTestRunConfiguration
import com.intellij.execution.JavaRunConfigurationExtensionManager
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.ConfigurationFromContext
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.psi.ElementUtils
import io.kotest.plugin.intellij.run.RunnerMode
import io.kotest.plugin.intellij.run.RunnerModes
import org.jetbrains.kotlin.idea.base.util.module
import org.jetbrains.kotlin.idea.gradleJava.run.MultiplatformTestTasksChooser
import org.jetbrains.plugins.gradle.execution.test.runner.GradleTestRunConfigurationProducer
import org.jetbrains.plugins.gradle.service.execution.GradleRunConfiguration

/**
 * Creates run configurations that use the Gradle test task, passing in a `--tests` arg.
 * This producer applies to running all tests in a spec (the double run icon on the spec name), as well
 * as individual tests (the single run icon on the test names).
 *
 * This producer will work with any Kotest version for JVM, since the JVM engine has always supported --tests.
 * For multiplatform though, only Kotest 6.1+ correctly forwards the tests arg to the engine.
 */
class GradleMultiplatformJvmTestTaskRunProducer : GradleTestRunConfigurationProducer() {

   private val logger = logger<GradleMultiplatformJvmTestTaskRunProducer>()
   private val mppTestTasksChooser = MultiplatformTestTasksChooser()

   /**
    * This function is called to set up the given [GradleRunConfiguration] if it is applicable
    * to the current [ConfigurationContext]. If the context is *not* something this producer is interested
    * in, then it should return false.
    *
    * Receives a configuration of the specified type and a [context] containing information about
    * a source code location (accessible by calling getLocation() or getPsiLocation()). The implementation
    * needs to check whether the location is applicable to the configuration type
    * (e.g., if it is in a file of the supported language). If it is, put the correct context-specific
    * settings into the run configuration and return true.
    *
    * Return false otherwise.
    */
   override fun doSetupConfigurationFromContext(
      configuration: GradleRunConfiguration,
      context: ConfigurationContext,
      ref: Ref<PsiElement?>
   ): Boolean {

      if (RunnerModes.mode(context.module) != RunnerMode.GRADLE_TEST_TASK) {
         logger.info("Runner mode is not GRADLE_TEST_TASK so this producer will not contribute")
         return false
      }

      // we will always have an element when running from the gutter or directory
      val element = ref.get() ?: return false
      val location = context.location ?: return false

      val testContext = ElementUtils.findTestContext(element)
      if (testContext == null) {
         logger.info("Test context could not be resolved for $element so this producer will not contribute")
         return false
      }

      // the name will appear in two places - it will be in the run icon chooser in the gutter Run/Debug/Profile etc.,
      // and will also be the name of the configuration in the run configs drop down
      // kotlin.test uses 'class name.method name', so we'll do the same for consistency
      configuration.name = GradleTestRunNameBuilder.builder()
         .withSpec(testContext.spec)
         .withTest(testContext.test)
         .build()
      configuration.settings.externalProjectPath = ExternalSystemApiUtil.getExternalProjectPath(element.module)
      configuration.settings.scriptParameters = ""
      configuration.isRunAsTest = true

      setUniqueNameIfNeeded(configuration.project, configuration)
      JavaRunConfigurationExtensionManager.instance.extendCreatedConfiguration(configuration, location)
      return true
   }

   /**
    * Checks if an existing configuration was created from the specified context.
    * This allows reusing an existing run configuration, which applies to the current context,
    * instead of creating a new one and ignoring the user's customizations in the existing one.
    *
    * Note: If multiple runs return true, one is selected arbitrarily.
    *
    * For example, if a user clicks on a particular run icon and then customizes that run configuration,
    * when they click that run icon again, we should reuse the existing configuration to preserve their customizations.
    */
   override fun doIsConfigurationFromContext(
      configuration: GradleRunConfiguration,
      context: ConfigurationContext
   ): Boolean {

      if (RunnerModes.mode(context.module) != RunnerMode.GRADLE_TEST_TASK) {
         logger.info("Runner mode is not GRADLE_TEST_TASK so this producer will not contribute")
         return false
      }

      val element = context.psiLocation ?: return false
      val testContext = ElementUtils.findTestContext(element) ?: return false
      logger.info("Existing configuration [${configuration.name} taskNames [" + configuration.settings.taskNames + "]")

      // in order for an existing Gradle configuration to be equivalent to what we are looking for, we will
      // check that the test filter matches the test clicked on.
      // we assume that the test filter is the same for all tasks (in the case of multiple tasks), and so
      // we just take the first such filter
      val filter = GradleTestFilterBuilder.builder()
         .withSpec(testContext.spec)
         .withTest(testContext.test)
         .build(false)

      val existingFilter = configuration.settings.taskNames
         .dropWhile { !it.startsWith("--tests") } // find the --tests filter, drop it, take whatever is after it
         .drop(1)
         .take(1).joinToString(" ")
      logger.info("Checking config filter [${existingFilter}] against selected test [${filter}]")

      return existingFilter == filter
   }

   override fun isPreferredConfiguration(self: ConfigurationFromContext, other: ConfigurationFromContext): Boolean {
      // if the other configuration is a Kotest Android instrumented test, that takes priority over running via Gradle
      return when (other.configuration) {
         // we know that a Kotest Android instrumented test configuration will be more specific than a Gradle one
         is AndroidTestRunConfiguration -> false
         else -> true
      }
   }

   /**
    * This executes the first time the configuration is created/executed and is used to populate extra
    * information that may require user input. In our case we show a task (target) chooser so the user
    * can select which target to run against when in commonTest etc. This is then set as the taskNames
    * and on later executions the user is not shown the task chooser again.
    */
   override fun onFirstRun(
      configuration: ConfigurationFromContext,
      context: ConfigurationContext,
      startRunnable: Runnable
   ) {

      val project = context.project

      if (project == null) {
         super.onFirstRun(configuration, context, startRunnable)
         return
      }

      val element = context.psiLocation ?: return
      val testContext = ElementUtils.findTestContext(element) ?: return

      val runConfiguration = configuration.configuration as GradleRunConfiguration
      val dataContext = MultiplatformTestTasksChooser.createContext(context.dataContext, runConfiguration.name)

      // used to pre-filter targets, eg if you are running something that could only be a JVM test, then it would filter
      // down to JVM targets only. When running from the gutter, there is no context, so we pass null, and all targets will appear
      //      val contextualSuffix = when (context.location) {
      //         is PsiMemberParameterizedLocation -> (context.location as? PsiMemberParameterizedLocation)?.paramSetName?.trim('[', ']')
      //         is MethodLocation -> "jvm"
      //         else -> null // from gutters
      //      }

      mppTestTasksChooser.multiplatformChooseTasks(
         project = project,
         dataContext = dataContext,
         elements = listOf(element),
         contextualSuffix = null
      ) { tasks ->
         logger.info("MultiplatformChooseTasks: $tasks")

         val filter = GradleTestFilterBuilder.builder()
            .withSpec(testContext.spec)
            .withTest(testContext.test)
            .build(true)

         // the tasks are a list of groups that clean/test each target, eg ':cleanLinuxX64Test :linuxX64Test'
         // we want to append the test filter after each of the test (not clean) tasks
         val tasksWithFilter = tasks.flatMap { it.values }.flatMap { it.tasks + listOf(filter) }
         logger.info("Chosen tasks with applied filters: $tasksWithFilter")
         runConfiguration.settings.taskNames = tasksWithFilter.toList()
         runConfiguration.settings.scriptParameters = if (tasks.size > 1) "--continue" else ""

         startRunnable.run()
      }
   }

}
