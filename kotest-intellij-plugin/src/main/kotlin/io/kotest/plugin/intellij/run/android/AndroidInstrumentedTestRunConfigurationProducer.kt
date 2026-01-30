package io.kotest.plugin.intellij.run.android

import com.android.tools.idea.projectsystem.AndroidModuleSystem
import com.android.tools.idea.projectsystem.CommonTestType
import com.android.tools.idea.projectsystem.SourceProviderManager
import com.android.tools.idea.projectsystem.androidProjectType
import com.android.tools.idea.projectsystem.containsFile
import com.android.tools.idea.projectsystem.getModuleSystem
import com.android.tools.idea.projectsystem.isContainedBy
import com.android.tools.idea.run.AndroidRunConfigurationType
import com.android.tools.idea.testartifacts.instrumented.AndroidRunConfigurationToken
import com.android.tools.idea.testartifacts.instrumented.AndroidTestConfigurationProducer.Companion.LOGGER
import com.android.tools.idea.testartifacts.instrumented.AndroidTestConfigurationProducer.Companion.OPTIONS_EP
import com.android.tools.idea.testartifacts.instrumented.AndroidTestRunConfiguration
import com.android.tools.idea.testartifacts.instrumented.AndroidTestRunConfigurationType
import com.android.tools.idea.testartifacts.instrumented.TestRunConfigurationOptions
import com.android.tools.idea.testartifacts.instrumented.getOptions
import com.android.tools.idea.util.androidFacet
import com.intellij.execution.Location
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.ConfigurationFromContext
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.junit.JavaRunConfigurationProducerBase
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.module.Module
import com.intellij.openapi.util.Ref
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtilCore
import io.kotest.plugin.intellij.psi.ElementUtils
import io.kotest.plugin.intellij.psi.TestContext
import io.kotest.plugin.intellij.run.RunnerMode
import io.kotest.plugin.intellij.run.RunnerModes
import io.kotest.plugin.intellij.run.gradle.GradleTestFilterBuilder
import io.kotest.plugin.intellij.run.gradle.GradleTestRunNameBuilder
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.android.util.AndroidUtils

class AndroidInstrumentedTestRunConfigurationProducer :
   JavaRunConfigurationProducerBase<AndroidTestRunConfiguration>() {

   private val logger = logger<AndroidInstrumentedTestRunConfigurationProducer>()

   override fun getConfigurationFactory(): ConfigurationFactory = AndroidTestRunConfigurationType.getInstance().factory

   /**
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
   override fun setupConfigurationFromContext(
      configuration: AndroidTestRunConfiguration,
      context: ConfigurationContext,
      sourceElementRef: Ref<PsiElement>
   ): Boolean {

      if (RunnerModes.mode(context.module) != RunnerMode.GRADLE_TEST_TASK) {
         logger.info("Runner mode is not GRADLE_TEST_TASK so this producer will not contribute")
         return false
      }

      val configurator = AndroidInstrumentedTestConfigurator.createFromContext(context) ?: return false
      if (!configurator.configure(configuration, context)) return false

      // Set context.module to the configuration. It may set non-context module such as
      // pre-defined module in configuration template.
      if (!setupConfigurationModule(context, configuration)) {
         return false
      }

      configuration.EXTRA_OPTIONS = getOptions(configuration.EXTRA_OPTIONS, context, OPTIONS_EP.extensionList, LOGGER)
      logger.info("Configuration ${configuration.name} setup successfully")
      return true
   }

   /**
    * If an existing configuration matches the current context, the IDE will suggest that one first to preserve
    * the user's previous customizations (like environment variables or VM options).
    */
   override fun isConfigurationFromContext(
      configuration: AndroidTestRunConfiguration,
      context: ConfigurationContext
   ): Boolean {

      if (RunnerModes.mode(context.module) != RunnerMode.GRADLE_TEST_TASK) {
         logger.info("Runner mode is not GRADLE_TEST_TASK so this producer will not contribute")
         return false
      }

      val expectedConfig = configurationFactory.createTemplateConfiguration(configuration.project)
         as AndroidTestRunConfiguration

      val configurator = AndroidInstrumentedTestConfigurator.createFromContext(context) ?: return false
      if (!configurator.configure(expectedConfig, context)) return false

      if (configuration.TESTING_TYPE != expectedConfig.TESTING_TYPE) {
         logger.info("Existing configuration does not have same testing type")
         return false
      }

      if (!configuration.EXTRA_OPTIONS.contains(getOptions("", context, OPTIONS_EP.extensionList, LOGGER))) {
         return false
      }

      // need to compare class name and tests filter
      logger.info("Existing configuration ${configuration.name} is not a match")
      return false
   }

   override fun findModule(configuration: AndroidTestRunConfiguration, contextModule: Module?): Module? {
      // In the base class implementation, it assumes that configuration module is null, and if not so,
      // it returns false, which is not always the case with AndroidTestRunConfiguration when the producer
      // is invoked from test result panel.
      // So here we just use either the contextModule's holder module or the configuration module.
      return contextModule?.getModuleSystem()?.getHolderModule() ?: configuration.configurationModule.module
   }

   /**
    * Returns true if [self] configuration should replace [other] configuration.
    *
    * In an IntelliJ custom plugin, the [shouldReplace] method in a RunConfigurationProducer is used to determine
    * if a run configuration generated by your producer should supplant or take precedence over an existing
    * run configuration from another producer.
    */
   override fun shouldReplace(self: ConfigurationFromContext, other: ConfigurationFromContext): Boolean {
      // We return true, because an Android instrumented test is more specific than any other we currently support.
      return true
   }
}

/**
 * A helper class responsible for configuring [AndroidTestRunConfiguration] based on given information.
 */
data class AndroidInstrumentedTestConfigurator(
   private val facet: AndroidFacet,
   private val location: Location<PsiElement>,
   private val virtualFile: VirtualFile,
   private val testContext: TestContext,
) {

   companion object {

      private val logger = logger<AndroidInstrumentedTestConfigurator>()

      /**
       * Creates a [AndroidInstrumentedTestConfigurator] from a given context.
       * Returns null if the context is not applicable for an Android instrumentation test.
       */
      fun createFromContext(context: ConfigurationContext): AndroidInstrumentedTestConfigurator? {
         val location = context.location ?: return null
         val module = AndroidUtils.getAndroidModule(context) ?: return null
         val facet = module.androidFacet ?: return null
         val virtualFile = PsiUtilCore.getVirtualFile(location.psiElement) ?: return null
         val testContext = ElementUtils.findTestContext(location.psiElement) ?: return null
         val configurator = AndroidInstrumentedTestConfigurator(facet, location, virtualFile, testContext)
         logger.info("Created configurator $configurator")
         return configurator
      }
   }

   /**
    * Configures a given configuration. If success, it returns true otherwise false.
    * @param configuration a configuration instance to be configured
    */
   fun configure(
      configuration: AndroidTestRunConfiguration,
      context: ConfigurationContext,
   ): Boolean {

      val sourceProviders = SourceProviderManager.getInstance(facet)
      val module = facet.module
      val (androidTestSources, generatedAndroidTestSources) =
         if (module.androidProjectType() == AndroidModuleSystem.Type.TYPE_TEST) {
            sourceProviders.sources to sourceProviders.generatedSources
         } else {
            sourceProviders.deviceTestSources[CommonTestType.ANDROID_TEST] to sourceProviders.generatedDeviceTestSources[CommonTestType.ANDROID_TEST]
         }
      // returns false if a file is not an Android test source
      // checks all the sources
      if (
         androidTestSources?.containsFile(virtualFile) == false &&
         !androidTestSources.isContainedBy(virtualFile) &&
         generatedAndroidTestSources?.containsFile(virtualFile) == false &&
         !generatedAndroidTestSources.isContainedBy(virtualFile)
      ) {
         logger.info("virtual file $virtualFile is not an Android test source, so this producer will not contribute")
         return false
      }

      // check if the current module is a valid AndroidTest module to set up the Run Configuration from
      if (AndroidRunConfigurationToken.getModuleForAndroidTestRunConfiguration(module) == null) {
         logger.info("Module $module is not a valid AndroidTest module, so this producer will not contribute")
         return false
      }

      val project = module.project
      val targetSelectionMode = AndroidUtils.getDefaultTargetSelectionMode(
         project, AndroidTestRunConfigurationType.getInstance(), AndroidRunConfigurationType.getInstance()
      )
      if (targetSelectionMode != null) {
         logger.info("Setting target selection mode to $targetSelectionMode")
         configuration.deployTargetContext.targetSelectionMode = targetSelectionMode
      }

      // we always use the test class type, and if it's a specific test, we'll add an ENV to filter down
      configuration.TESTING_TYPE = AndroidTestRunConfiguration.TEST_CLASS

      logger.info("Setting configuration.CLASS_NAME to ${testContext.spec.fqName?.asString()}")
      configuration.CLASS_NAME = testContext.spec.fqName?.asString() ?: ""

      val filter = GradleTestFilterBuilder.builder()
         .withSpec(testContext.spec)
         .withTest(testContext.test)
         .build(false)

      configuration.EXTRA_OPTIONS =
         getOptions(configuration.EXTRA_OPTIONS, context, listOf(kotestIncludePatternOptions(filter)), logger)

      // the name will appear in two places - it will be in the run icon chooser in the gutter Run/Debug/Profile etc.,
      // and will also be the name of the configuration in the run configs drop down
      // kotlin.test uses 'class name.method name', so we'll do the same for consistency
      configuration.name = GradleTestRunNameBuilder.builder()
         .withSpec(testContext.spec)
         .withTest(testContext.test)
         .build()

      return true
   }

   // see example https://github.com/JetBrains/android/blob/4b00e2c1896e90c096534c857d3b65f6e00694d4/project-system-gradle/src/com/android/tools/idea/run/configuration/AndroidBaselineProfileRunConfiguration.kt#L84
   fun kotestIncludePatternOptions(filter: String): TestRunConfigurationOptions {
      return object : TestRunConfigurationOptions() {
         /**
          * Android docs:
          * You can pass custom parameters (e.g., -e server_url https://api.test.com) and retrieve them within your test
          * code using InstrumentationRegistry.getArguments().getString("server_url"
          */
         override fun getExtraOptions(context: ConfigurationContext): List<String> {
            // should match the name used by the InstrumentationFilter in the JUnit 4 runner
            return listOf("-e INSTRUMENTATION_INCLUDE_PATTERN $filter")
         }
      }
   }
}
