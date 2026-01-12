package io.kotest.plugin.intellij.run.gradle

import com.intellij.execution.JavaRunConfigurationExtensionManager
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.ConfigurationFromContext
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.gradle.GradleUtils
import io.kotest.plugin.intellij.psi.enclosingSpec
import io.kotest.plugin.intellij.run.RunnerMode
import io.kotest.plugin.intellij.run.RunnerModes
import io.kotest.plugin.intellij.styles.SpecStyle
import org.jetbrains.kotlin.idea.gradleJava.run.MultiplatformTestTasksChooser
import org.jetbrains.plugins.gradle.execution.test.runner.GradleTestRunConfigurationProducer
import org.jetbrains.plugins.gradle.service.execution.GradleRunConfiguration
import org.jetbrains.plugins.gradle.util.GradleConstants.SYSTEM_ID

/**
 * Creates run configurations that use the Gradle test task, passing in --tests as the filter.
 * This producer applies to running all tests in a spec (the double run icon on the spec name), as well
 * as individual tests (the single run icon on the test names).
 *
 * This producer will work with any Kotest version for JVM, since the JVM engine has always supported --tests.
 * For multiplatform though, only Kotest 6.1+ correctly forwards the --tests parameter to the engine.
 */
class GradleMultiplatformJvmTestTaskRunProducer : GradleTestRunConfigurationProducer() {

   private val logger = logger<GradleMultiplatformJvmTestTaskRunProducer>()
   private val mppTestTasksChooser = MultiplatformTestTasksChooser()

   private val KEY_FILTER = Key.create<String>("FILTER")

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

      if (RunnerModes.mode(p1.module) != RunnerMode.GRADLE_TEST_TASK) return false

      val project = p1.project ?: return false
      val module = p1.module ?: return false
      if (SYSTEM_ID != p0.settings.externalSystemId) return false

      logger.info("element clicked ${p2.get()}")

      // we must have the element we clicked on as we are running from the gutter
      val element = p2.get() ?: return false

      // we must be in a class or object to define tests,
      // and we will use the FQN of that class or object as the first part of the test filter arg
      val spec = element.enclosingSpec() ?: return false
      val test = SpecStyle.findTest(element)

      // the name will appear in two places - it will be in the run icon drop down after Run/Debug/Profile etc,
      // and will also be the name of the configuration in the run configs drop down
      // kotlin.test uses 'class name.method name', so we'll do the same for consistency
      val runName = GradleTestRunNameBuilder.builder()
         .withSpec(spec)
         .withTest(test)
         .build()

      val externalProjectPath = resolveProjectPath(module) ?: return false
      val location = p1.location ?: return false

      p0.name = runName
      p0.settings.externalProjectPath = externalProjectPath
      p0.settings.scriptParameters = ""
      p0.isRunAsTest = true
      setUniqueNameIfNeeded(project, p0)

      // we can't run analysis inside onFirstRun, so we need to figure out the spec and test now,
      // and use that to create and store a filter string we can use later
      val filter = GradleTestFilterBuilder.builder()
         .withSpec(spec)
         .withTest(test)
         .build()

      logger.info("Gradle test filter created: $filter")
      p0.putUserData<String>(KEY_FILTER, filter)

      JavaRunConfigurationExtensionManager.instance.extendCreatedConfiguration(p0, location)
      return true
   }

   /**
    * Checks if an existing configuration was created from the specified context.
    * This allows reusing an existing run configuration, which applies to the current context,
    * instead of creating a new one and ignoring the user's customizations in the existing one.
    *
    * For example, if a user clicks on a particular run icon, and then customizes that run configuration,
    * say by adding an env variable, when they click that run icon again, we should reuse the existing configuration
    * to preserve their customizations.
    */
   override fun doIsConfigurationFromContext(
      p0: GradleRunConfiguration,
      p1: ConfigurationContext
   ): Boolean {

      // we only want this producer to apply if the project has kotest 6.1+
      val version = GradleUtils.getKotestVersion(p1.module) ?: return false
      if (version.major < 6) return false
      if (version.major == 6 && version.minor < 1) return false

      // in order for an existing Gradle configuration to be equivalent to what we are looking for, we will
      // check that the test filter matches the test clicked on.
      logger.info("doIsConfigurationFromContext: $p0 $p1")
      val element = p1.psiLocation ?: return false
      val test = SpecStyle.findTest(element)
      if (test != null) {
         // if the test that was clicked on is the same as the test that created the run configuration, then
         // this context is applicable and can be used.
         logger.info("Task names from existing run configuration: " + p0.settings.taskNames)
         // todo update for data testing
//            if (test.isDataTest) {
//               val spec = element.enclosingSpec()
//               return spec?.fqName?.asString() == descriptorArg
//            }
//            if (test.descriptorPath() == descriptorArg) return true
      }

      return false
   }


   /**
    * This executes the first time the configuration is created/executed and is used to populate extra
    * information that may require user input. In our case we show task (target) chooser so the user
    * can select which target to run against when in commonTest etc. This is then set as the taskNames
    * and on subsequent executions the user is not shown the task chooser again.
    */
   override fun onFirstRun(
      configuration: ConfigurationFromContext,
      context: ConfigurationContext,
      startRunnable: Runnable
   ) {

      val project = context.project
      val element = context.psiLocation ?: return

      if (project == null) {
         super.onFirstRun(configuration, context, startRunnable)
         return
      }

      val runConfiguration = configuration.configuration as GradleRunConfiguration
      val dataContext = MultiplatformTestTasksChooser.createContext(context.dataContext, runConfiguration.name)

      // used to pre-filter targets, eg if you are running something that could only be a JVM test, then it would filter
      // down to JVM targets only. When running from the gutter, there is no context, so we pass null, and all targets will appear
      //      val contextualSuffix = when (context.location) {
      //         is PsiMemberParameterizedLocation -> (context.location as? PsiMemberParameterizedLocation)?.paramSetName?.trim('[', ']')
      //         is MethodLocation -> "jvm"
      //         else -> null // from gutters
      //      }

      mppTestTasksChooser.multiplatformChooseTasks(project, dataContext, listOf(element), null) { tasks ->
         val module = context.module ?: throw IllegalStateException("Module should not be null")
         logger.info("Tasks chosen: $tasks")

         val settings = runConfiguration.settings
         settings.externalProjectPath = ExternalSystemApiUtil.getExternalProjectPath(module)

         // this was the filter created when the configuration was first created
         val filter = runConfiguration.getUserData<String>(KEY_FILTER) as String

         // the filter needs to be applied to each grouping of tasks, eg ':cleanLinuxX64Test :linuxX64Test'
         // is a single grouping that needs --tests after the final arg
         runConfiguration.settings.taskNames = tasks.flatMap { it.values }.flatMap { it.tasks + listOf(filter) }
         runConfiguration.settings.scriptParameters = if (tasks.size > 1) "--continue" else ""

         setUniqueNameIfNeeded(project, runConfiguration)
         startRunnable.run()
      }
   }
}
