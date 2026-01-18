package io.kotest.plugin.intellij.run.gradle

import com.intellij.execution.JavaRunConfigurationExtensionManager
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.ConfigurationFromContext
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.psi.enclosingSpec
import io.kotest.plugin.intellij.run.RunnerMode
import io.kotest.plugin.intellij.run.RunnerModes
import io.kotest.plugin.intellij.styles.SpecStyle
import org.jetbrains.kotlin.analysis.api.KaImplementationDetail
import org.jetbrains.kotlin.analysis.api.permissions.KaAnalysisPermissionRegistry
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
      p0: GradleRunConfiguration,
      p1: ConfigurationContext,
      p2: Ref<PsiElement?>
   ): Boolean {

      if (RunnerModes.mode(p1.module) != RunnerMode.GRADLE_TEST_TASK) {
         logger.info("Runner mode is not GRADLE_TEST_TASK so this producer will not contribute")
         return false
      }

      // we will always have an element when running from the gutter or directory
      val element = p2.get() ?: return false
      val location = p1.location ?: return false

      val testContext = createTestContext(element)
      if (testContext == null) {
         logger.info("Test context could not be resolved for $element so this producer will not contribute")
         return false
      }

      p0.name = testContext.runName
      p0.settings.externalProjectPath = ExternalSystemApiUtil.getExternalProjectPath(element.module)
      p0.settings.scriptParameters = ""
      p0.isRunAsTest = true

      setUniqueNameIfNeeded(p0.project, p0)
      JavaRunConfigurationExtensionManager.instance.extendCreatedConfiguration(p0, location)
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
    * say by adding an env variable, when they click that run icon again, we should reuse the existing configuration
    * to preserve their customizations.
    */
   override fun doIsConfigurationFromContext(
      p0: GradleRunConfiguration,
      p1: ConfigurationContext
   ): Boolean {

      if (RunnerModes.mode(p1.module) != RunnerMode.GRADLE_TEST_TASK) {
         logger.info("Runner mode is not GRADLE_TEST_TASK so this producer will not contribute")
         return false
      }

      val element = p1.psiLocation ?: return false
      val testContext = createTestContext(element) ?: return false
      logger.info("Existing configuration [${p0.name} taskNames [" + p0.settings.taskNames + "]")

      // in order for an existing Gradle configuration to be equivalent to what we are looking for, we will
      // check that the test filter matches the test clicked on.
      // we assume that the test filter is the same for all tasks (in the case of multiple tasks), and so
      // we just take the first such filter
      val existingFilter = p0.settings.taskNames.dropWhile { !it.startsWith("--tests") }.take(2).joinToString(" ")
      logger.info("Checking config filter [${existingFilter}] against selected test [${testContext.filter}]")

      return existingFilter == testContext.filter
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
      val testContext = createTestContext(element) ?: return

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

         // the tasks are a list of groups that clean/test each target, eg ':cleanLinuxX64Test :linuxX64Test'
         // we want to append the test filter after each of the test (not clean) tasks
         val tasksWithFilter = tasks.flatMap { it.values }.flatMap { it.tasks + listOf(testContext.filter) }
         logger.info("Choosen tasks with applied filters: $tasksWithFilter")
         runConfiguration.settings.taskNames = tasksWithFilter.toList()
         runConfiguration.settings.scriptParameters = if (tasks.size > 1) "--continue" else ""

         setOrRemoveDataTestEnvVarIfNeeded(runConfiguration, testContext)
         startRunnable.run()
      }
   }

   /**
    * Sets or removes the KOTEST_TAGS environment variable for data test filtering.
    * If the test context has a data test tag, it sets KOTEST_TAGS to that tag.
    * If not, it removes KOTEST_TAGS from the environment variables.
    * Have to rely on env vars here because Gradle system properties (-D) do not propagate to the test JVM.
    *
    * @param runConfiguration The Gradle run configuration to modify.
    * @param testContext The test context containing the data test tag.
    */
   private fun setOrRemoveDataTestEnvVarIfNeeded(
      runConfiguration: GradleRunConfiguration,
      testContext: TestContext
   ) {
      testContext.dataTestTag.takeIf { it != null }
         ?.let {
            val envVars = runConfiguration.settings.env.toMutableMap()
            envVars["KOTEST_TAGS"] = testContext.dataTestTag
            runConfiguration.settings.env = envVars
         }
         ?: run {
            val envVars = runConfiguration.settings.env.toMutableMap()
            envVars.remove("KOTEST_TAGS")
            runConfiguration.settings.env = envVars
         }
   }

   @OptIn(KaImplementationDetail::class)
   private fun createTestContext(element: PsiElement): TestContext? {

      // we must be in a Kotest spec (class or object), and we will use the FQN of that class or object
      // as the first part of the test filter arg

      val (spec, test) = if (KaAnalysisPermissionRegistry.getInstance().isAnalysisAllowedOnEdt) {
         val spec = element.enclosingSpec() ?: return null
         val test = SpecStyle.findTest(element)
         Pair(spec, test)
      } else {
         try {
            KaAnalysisPermissionRegistry.getInstance().isAnalysisAllowedOnEdt = true
            val spec = element.enclosingSpec() ?: return null
            val test = SpecStyle.findTest(element)
            Pair(spec, test)
         } catch (e: Throwable) {
            logger.warn("Failed to get spec and test in analysis mode", e)
            return null
         } finally {
            KaAnalysisPermissionRegistry.getInstance().isAnalysisAllowedOnEdt = false
         }
      }

      val filter = GradleTestFilterBuilder.builder()
         .withSpec(spec)
         .withTest(test)
         .build()

      /**
       * For data tests, we use tag-based filtering instead of test path filtering.
       * For non data test, we set this to null to allow [setOrRemoveDataTestEnvVarIfNeeded]
       * to remove such env var if it was set previously.
       */
      val dataTestTagMaybe = test?.dataTestTagMaybe()

      // the name will appear in two places - it will be in the run icon chooser in the gutter Run/Debug/Profile etc.,
      // and will also be the name of the configuration in the run configs drop down
      // kotlin.test uses 'class name.method name', so we'll do the same for consistency
      val runName = GradleTestRunNameBuilder.builder()
         .withSpec(spec)
         .withTest(test)
         .build()

      return TestContext(runName, filter, dataTestTagMaybe)
   }

   /**
    * Contains details of the selected test context.
    */
   data class TestContext(
      val runName: String,
      val filter: String, // eg --tests "com.sksamuel.MySpec/a test"
      val dataTestTag: String? = null, // eg "kotest.data.{lineNumber}" for data tests
   )
}
