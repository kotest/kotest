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
import com.android.tools.idea.testartifacts.instrumented.AndroidTestConfigurationProducer.Companion.OPTIONS_EP
import com.android.tools.idea.testartifacts.instrumented.AndroidTestRunConfiguration
import com.android.tools.idea.testartifacts.instrumented.AndroidTestRunConfigurationType
import com.android.tools.idea.testartifacts.instrumented.getOptions
import com.android.tools.idea.util.androidFacet
import com.intellij.execution.Location
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.ConfigurationFromContext
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.junit.JavaRunConfigurationProducerBase
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Ref
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtilCore
import io.kotest.plugin.intellij.gradle.GradleUtils
import io.kotest.plugin.intellij.psi.ElementUtils
import io.kotest.plugin.intellij.psi.TestReference
import io.kotest.plugin.intellij.run.RunnerMode
import io.kotest.plugin.intellij.run.RunnerModes
import io.kotest.plugin.intellij.run.gradle.GradleTestFilterBuilder
import io.kotest.plugin.intellij.run.gradle.GradleTestRunNameBuilder
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.android.util.AndroidUtils

/**
 * Creates run configurations for Android instrumented tests.
 *
 * This is based on the [com.android.tools.idea.testartifacts.instrumented.AndroidTestConfigurationProducer]
 * implementation in the Android Studio IDE.
 */
class AndroidInstrumentedTestRunConfigurationProducer :
   JavaRunConfigurationProducerBase<AndroidTestRunConfiguration>() {

   private val logger = logger<AndroidInstrumentedTestRunConfigurationProducer>()

   override fun getConfigurationFactory(): ConfigurationFactory = AndroidTestRunConfigurationType.getInstance().factory

   /**
    * This function is called to customize the blank [configuration] if it is applicable to the [context].
    * If the given context is not something this producer is interested in, then it should return false.
    */
   override fun setupConfigurationFromContext(
      configuration: AndroidTestRunConfiguration,
      context: ConfigurationContext,
      sourceElementRef: Ref<PsiElement>
   ): Boolean {

      if (!isEnabled(context.module?.project)) {
         logger.debug("This producer is not enabled for this project, so it will not contribute")
         return false
      }

      val configurator = AndroidInstrumentedTestConfigurator.createFromContext(context) ?: return false
      if (!configurator.configure(configuration, context)) return false

      // Set context.module to the configuration. It may set non-context module such as
      // pre-defined module in configuration template.
      if (!setupConfigurationModule(context, configuration)) {
         return false
      }

      configuration.EXTRA_OPTIONS = getOptions(configuration.EXTRA_OPTIONS, context, OPTIONS_EP.extensionList, logger)
      logger.debug("Configuration ${configuration.name} setup successfully")
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

      if (!isEnabled(context.module?.project)) {
         logger.debug("This producer is not enabled for this module, so it will not contribute")
         return false
      }

      // we create a new throwaway configuration and configure it and use it to compare to the provided one
      // to see if they are the same (where it matters - eg class, filter). If they are the same, then we know
      // the provided configuration is the same as the one we would create and so should be used.
      val expectedConfig = configurationFactory
         .createTemplateConfiguration(configuration.project) as AndroidTestRunConfiguration
      val configurator = AndroidInstrumentedTestConfigurator.createFromContext(context) ?: return false
      if (!configurator.configure(expectedConfig, context)) return false

      if (configuration.CLASS_NAME != expectedConfig.CLASS_NAME) {
         logger.info("Existing configuration does not have same class name (${configuration.CLASS_NAME} != ${expectedConfig.CLASS_NAME})")
         return false
      }

      if (configuration.TESTING_TYPE != expectedConfig.TESTING_TYPE) {
         logger.info("Existing configuration does not have same testing type (${configuration.TESTING_TYPE} != ${expectedConfig.TESTING_TYPE})")
         return false
      }

      if (expectedConfig.EXTRA_OPTIONS.contains(KotestInstrumentationIncludeTestRunConfigurationOptions.INSTRUMENTATION_INCLUDE_PATTERN_NAME) && (configuration.EXTRA_OPTIONS != expectedConfig.EXTRA_OPTIONS)) {
         logger.info("Existing configuration does not have same filter (${configuration.EXTRA_OPTIONS} != ${expectedConfig.EXTRA_OPTIONS})")
         return false
      }

      return true
   }

   override fun findModule(configuration: AndroidTestRunConfiguration, contextModule: Module?): Module? {
      // In the base class implementation, it assumes that configuration module is null, and if not so,
      // it returns false, which is not always the case with AndroidTestRunConfiguration when the producer
      // is invoked from test result panel.
      // So here we just use either the contextModule's holder module or the configuration module.
      return contextModule?.getModuleSystem()?.module ?: configuration.configurationModule.module
   }

   /**
    * Returns true if [self] configuration should replace [other] configuration.
    *
    * In an IntelliJ custom plugin, the [shouldReplace] method in a RunConfigurationProducer is used to determine
    * if a run configuration generated by your producer should supplant or take precedence over an existing
    * run configuration from another producer.
    */
   override fun shouldReplace(self: ConfigurationFromContext, other: ConfigurationFromContext): Boolean {
      // we return true, because a Kotest Android instrumented test is more specific than a normal Kotest test,
      // and we know that only Kotest contexts will be present here.

      if (!isEnabled(self.configuration.project)) {
         logger.debug("This producer is not enabled for this module, so it will not contribute")
         return false
      }

      return true
   }

   /**
    * Returns true if this run producer should be enabled for the current project.
    *
    * To be enabled, it requires Kotest 6.1.4 or higher, since that's when the InstrumentationFilter
    * was added, needed for individual tests.
    */
   internal fun isEnabled(project: Project?): Boolean {
      return GradleUtils.isKotest614OrAbove(project)
   }
}

/**
 * A helper class responsible for configuring [AndroidTestRunConfiguration]s based on given information.
 */
internal data class AndroidInstrumentedTestConfigurator(
   val facet: AndroidFacet,
   val location: Location<PsiElement>,
   val virtualFile: VirtualFile,
   val testReference: TestReference,
) {

   companion object {

      private val logger = logger<AndroidInstrumentedTestConfigurator>()

      /**
       * Creates a [AndroidInstrumentedTestConfigurator] from a given context if the context is a
       * Kotest spec or test, otherwise returns null.
       */
      fun createFromContext(context: ConfigurationContext): AndroidInstrumentedTestConfigurator? {
         val location = context.location ?: return null
         val facet = AndroidUtils.getAndroidModule(context)?.androidFacet ?: return null
         val virtualFile = PsiUtilCore.getVirtualFile(location.psiElement) ?: return null
         val testref = ElementUtils.findTestReference(location.psiElement) ?: return null
         val configurator = AndroidInstrumentedTestConfigurator(facet, location, virtualFile, testref)
         logger.debug("Created configurator $configurator")
         return configurator
      }
   }

   /**
    * Accepts a [AndroidTestRunConfiguration] and the current context, and if the context is applicable to
    * instrumented tests returns true after configuring the run configuration.
    */
   internal fun configure(
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
         logger.debug("virtual file $virtualFile is not an Android test source, so this producer will not contribute")
         return false
      }

      // check if the current module is a valid AndroidTest module to set up the Run Configuration from
      if (AndroidRunConfigurationToken.getModuleForAndroidTestRunConfiguration(module) == null) {
         logger.debug("Module $module is not a valid AndroidTest module, so this producer will not contribute")
         return false
      }

      val project = module.project
      val targetSelectionMode = AndroidUtils.getDefaultTargetSelectionMode(
         project, AndroidTestRunConfigurationType.getInstance(), AndroidRunConfigurationType.getInstance()
      )
      if (targetSelectionMode != null) {
         logger.debug("Setting target selection mode to $targetSelectionMode")
         configuration.deployTargetContext.targetSelectionMode = targetSelectionMode
      }

      // we always use the test class type as our supported test mode
      configuration.TESTING_TYPE = AndroidTestRunConfiguration.TEST_CLASS
      configuration.CLASS_NAME = testReference.spec.fqName?.asString() ?: ""
      logger.info("Setting configuration.CLASS_NAME to ${configuration.CLASS_NAME}")

      val filter = GradleTestFilterBuilder.builder()
         .withSpec(testReference.spec)
         .withTest(testReference.test)
         .build(includeTestsFlag = false)
      logger.info("Setting configuration.EXTRA_OPTIONS to include filter $filter")

      // only set the test filter if this was a test and not just a spec
      if (testReference.test != null) {
         configuration.EXTRA_OPTIONS = getOptions(
            existingOptions = configuration.EXTRA_OPTIONS,
            context = context,
            extensions = listOf(KotestInstrumentationIncludeTestRunConfigurationOptions(filter)),
            logger = logger
         )
      }

      configuration.name = GradleTestRunNameBuilder.builder()
         .withSpec(testReference.spec)
         .withTest(testReference.test)
         .build()
      logger.debug("Setting configuration.name to ${configuration.name}")

      return true
   }
}

