package io.kotest.plugin.intellij.run.android

import com.android.tools.idea.projectsystem.AndroidProjectSystem
import com.android.tools.idea.testartifacts.instrumented.AndroidRunConfigurationToken
import com.android.tools.idea.testartifacts.instrumented.AndroidTestRunConfiguration
import com.android.tools.idea.testartifacts.instrumented.AndroidTestRunConfigurationType
import com.intellij.execution.PsiLocation
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.facet.FacetManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.externalSystem.ExternalSystemModulePropertyManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.testFramework.ExtensionTestUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.kotest.matchers.shouldBe
import io.kotest.plugin.intellij.psi.elementAtLine
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.plugins.gradle.settings.GradleProjectSettings
import org.jetbrains.plugins.gradle.settings.GradleSettings
import org.jetbrains.plugins.gradle.settings.TestRunner
import java.nio.file.Paths

class AndroidInstrumentedTestRunConfigurationProducerTest : BasePlatformTestCase() {

   override fun getTestDataPath(): String =
      Paths.get("./src/test/resources/").toAbsolutePath().toString()

   private fun createConfiguration(): AndroidTestRunConfiguration =
      AndroidTestRunConfigurationType.getInstance().factory
         .createTemplateConfiguration(project) as AndroidTestRunConfiguration

   fun testCreateConfigurationFromContextSetsClassName() {

      setupKotestGradleTestTaskMode()
      setupAndroidFacet()

      val psiFiles = myFixture.configureByFiles(
         "/funspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val psiElement = psiFiles[0].elementAtLine(6) ?: error("Could not find PSI element")
      val context = ConfigurationContext.createEmptyContextForLocation(
         PsiLocation(project, myFixture.module, psiElement)
      )

      val runconfig = AndroidInstrumentedTestRunConfigurationProducer().createConfigurationFromContext(context)
      val configuration = runconfig!!.configuration as AndroidTestRunConfiguration
      configuration.CLASS_NAME shouldBe "io.kotest.samples.gradle.FunSpecExampleTest"
      configuration.TESTING_TYPE shouldBe AndroidTestRunConfiguration.TEST_CLASS
      configuration.EXTRA_OPTIONS shouldBe ""
   }

   fun testCreateConfigurationFromContextAddsFilterForIndividualTest() {

      setupKotestGradleTestTaskMode()
      setupAndroidFacet()

      val psiFiles = myFixture.configureByFiles(
         "/funspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val psiElement = psiFiles[0].elementAtLine(22) ?: error("Could not find PSI element")
      val context = ConfigurationContext.createEmptyContextForLocation(
         PsiLocation(project, myFixture.module, psiElement)
      )

      val runconfig = AndroidInstrumentedTestRunConfigurationProducer().createConfigurationFromContext(context)
      val configuration = runconfig!!.configuration as AndroidTestRunConfiguration
      configuration.CLASS_NAME shouldBe "io.kotest.samples.gradle.FunSpecExampleTest"
      configuration.EXTRA_OPTIONS shouldBe "-e INSTRUMENTATION_INCLUDE_PATTERN 'io.kotest.samples.gradle.FunSpecExampleTest.some context -- a nested test'"
      configuration.TESTING_TYPE shouldBe AndroidTestRunConfiguration.TEST_CLASS
   }

   /**
    * When the context has no module, [io.kotest.plugin.intellij.run.RunnerModes.mode] returns null
    * (not GRADLE_TEST_TASK), so [AndroidInstrumentedTestRunConfigurationProducer.isConfigurationFromContext]
    * must return false.
    */
   fun testIsConfigurationFromContextReturnsFalseWhenContextHasNoModule() {
      val factory = KtPsiFactory(project)
      val spec = factory.createClass("class MySpec")
      val context = ConfigurationContext.createEmptyContextForLocation(PsiLocation(spec))

      AndroidInstrumentedTestRunConfigurationProducer()
         .isConfigurationFromContext(createConfiguration(), context) shouldBe false
   }

   /**
    * When the existing configuration CLASS_NAME does not match the Kotest spec that the context
    * points to, [AndroidInstrumentedTestRunConfigurationProducer.isConfigurationFromContext]
    * must return false.
    */
   fun testIsConfigurationFromContextReturnsFalseWhenClassNameDoesNotMatch() {
      setupKotestGradleTestTaskMode()
      setupAndroidFacet()

      val psiFiles = myFixture.configureByFiles(
         "/funspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val specElement = psiFiles[0].elementAtLine(6) ?: error("Could not find PSI element")
      val context = ConfigurationContext.createEmptyContextForLocation(
         PsiLocation(project, myFixture.module, specElement)
      )

      val configuration = createConfiguration()
      configuration.CLASS_NAME = "com.example.DifferentSpec"
      configuration.TESTING_TYPE = AndroidTestRunConfiguration.TEST_CLASS

      AndroidInstrumentedTestRunConfigurationProducer()
         .isConfigurationFromContext(configuration, context) shouldBe false
   }

   /**
    * When the existing configuration CLASS_NAME matches the Kotest spec that the context
    * points to, [AndroidInstrumentedTestRunConfigurationProducer.isConfigurationFromContext]
    * must return true.
    */
   fun testIsConfigurationFromContextReturnsTrueWhenClassNameMatches() {
      setupKotestGradleTestTaskMode()
      setupAndroidFacet()

      val psiFiles = myFixture.configureByFiles(
         "/funspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val specElement = psiFiles[0].elementAtLine(6) ?: error("Could not find PSI element")
      val context = ConfigurationContext.createEmptyContextForLocation(
         PsiLocation(project, myFixture.module, specElement)
      )

      val configuration = createConfiguration()
      configuration.CLASS_NAME = "io.kotest.samples.gradle.FunSpecExampleTest"
      configuration.TESTING_TYPE = AndroidTestRunConfiguration.TEST_CLASS

      AndroidInstrumentedTestRunConfigurationProducer()
         .isConfigurationFromContext(configuration, context) shouldBe true
   }

   /**
    * When the existing configuration CLASS_NAME and test filter match the Kotest test that the context
    * points to, [AndroidInstrumentedTestRunConfigurationProducer.isConfigurationFromContext]
    * must return true.
    */
   fun testIsConfigurationFromContextReturnsTrueWhenClassNameAndTestFilterMatches() {
      setupKotestGradleTestTaskMode()
      setupAndroidFacet()

      val psiFiles = myFixture.configureByFiles(
         "/funspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val specElement = psiFiles[0].elementAtLine(22) ?: error("Could not find PSI element")
      val context = ConfigurationContext.createEmptyContextForLocation(
         PsiLocation(project, myFixture.module, specElement)
      )

      val configuration = createConfiguration()
      configuration.CLASS_NAME = "io.kotest.samples.gradle.FunSpecExampleTest"
      configuration.TESTING_TYPE = AndroidTestRunConfiguration.TEST_CLASS
      configuration.EXTRA_OPTIONS = "-e INSTRUMENTATION_INCLUDE_PATTERN 'io.kotest.samples.gradle.FunSpecExampleTest.some context -- a nested test'"

      AndroidInstrumentedTestRunConfigurationProducer()
         .isConfigurationFromContext(configuration, context) shouldBe true
   }

   /**
    * When the existing configuration CLASS_NAME and test filter don't the Kotest test that the context
    * points to, [AndroidInstrumentedTestRunConfigurationProducer.isConfigurationFromContext]
    * must return false.
    */
   fun testIsConfigurationFromContextReturnsFalseWhenTestFilterDoesNotMatch() {
      setupKotestGradleTestTaskMode()
      setupAndroidFacet()

      val psiFiles = myFixture.configureByFiles(
         "/funspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val specElement = psiFiles[0].elementAtLine(22) ?: error("Could not find PSI element")
      val context = ConfigurationContext.createEmptyContextForLocation(
         PsiLocation(project, myFixture.module, specElement)
      )

      val configuration = createConfiguration()
      configuration.CLASS_NAME = "io.kotest.samples.gradle.FunSpecExampleTest"
      configuration.TESTING_TYPE = AndroidTestRunConfiguration.TEST_CLASS
      configuration.EXTRA_OPTIONS = "-e INSTRUMENTATION_INCLUDE_PATTERN 'a.b.c'"

      AndroidInstrumentedTestRunConfigurationProducer()
         .isConfigurationFromContext(configuration, context) shouldBe false
   }

   /**
    * Configures the test fixture so that
    * [io.kotest.plugin.intellij.run.android.AndroidInstrumentedTestRunConfigurationProducer.isEnabled]
    * returns true and [io.kotest.plugin.intellij.run.RunnerModes.mode] returns
    * [io.kotest.plugin.intellij.run.RunnerMode.GRADLE_TEST_TASK].
    *
    * [io.kotest.plugin.intellij.gradle.GradleUtils.isKotest614OrAbove] has two paths:
    * 1. **Library table (this test)**: a project-level library named
    *    `io.kotest:kotest-framework-engine-jvm:<version>` is present and its version is ≥ 6.1.4.
    * 2. **Source-project detection (real IntelliJ)**: when developing Kotest itself, no versioned
    *    library entry exists; instead [com.intellij.openapi.module.ModuleManager] finds a module
    *    whose name contains "kotest-framework-engine" and returns true automatically.
    *
    * Light fixture tests do not permit adding modules, so we exercise path 1 here by seeding a
    * version ≥ 6.1.4 into the project library table.
    *
    * Guards against duplicate creation since [BasePlatformTestCase] reuses the same project
    * across all tests in the class.
    */
   private fun setupKotestGradleTestTaskMode() {
      // hasKotestEngine: add a module-level library named "kotest-framework-engine-jvm"
      ModuleRootModificationUtil.updateModel(myFixture.module) { model ->
         if (model.moduleLibraryTable.getLibraryByName("kotest-framework-engine-jvm") == null) {
            model.moduleLibraryTable.createLibrary("kotest-framework-engine-jvm")
         }
      }
      WriteAction.runAndWait<Exception> {
         // isKotest614OrAbove (and isKotest61OrAbove): add a project-level library whose name
         // encodes the version. Must be ≥ 6.1.4 so the Android producer's isEnabled check passes.
         val libName = "io.kotest:kotest-framework-engine-jvm:6.1.4"
         val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
         if (libraryTable.getLibraryByName(libName) == null) {
            libraryTable.createLibrary(libName)
         }

         // isGradleTestRunner: In IntelliJ 2025.1, getTestRunner(project, null) returns PLATFORM.
         // We link the module's external project path to a GradleProjectSettings with GRADLE runner.
         val projectPath = project.basePath ?: error("project.basePath is null")
         ExternalSystemModulePropertyManager.getInstance(myFixture.module).setLinkedProjectPath(projectPath)
         val gradleSettings = GradleSettings.getInstance(project)
         if (gradleSettings.getLinkedProjectSettings(projectPath) == null) {
            val settings = GradleProjectSettings()
            settings.externalProjectPath = projectPath
            settings.testRunner = TestRunner.GRADLE
            gradleSettings.linkProject(settings)
         }
      }
   }

   /**
    * Adds an [AndroidFacet] to the test module so that
    * [AndroidInstrumentedTestConfigurator.createFromContext] can locate the facet.
    *
    * Also registers a stub [AndroidRunConfigurationToken] extension for the duration of this test
    * so that [AndroidRunConfigurationToken.getModuleForAndroidTestRunConfiguration] returns
    * non-null. In a light test fixture there is no real Android project system, so the production
    * extensions return null; the stub replaces all extensions via [ExtensionTestUtil.maskExtensions]
    * and is cleaned up automatically via [testRootDisposable] at the end of the test.
    */
   private fun setupAndroidFacet() {
      WriteAction.runAndWait<Exception> {
         val facetManager = FacetManager.getInstance(myFixture.module)
         if (facetManager.getFacetByType(AndroidFacet.ID) == null) {
            val facetType = AndroidFacet.getFacetType()
            val facet =
               facetType.createFacet(myFixture.module, AndroidFacet.NAME, facetType.createDefaultConfiguration(), null)
            val model = facetManager.createModifiableModel()
            model.addFacet(facet)
            model.commit()
         }
      }
      ExtensionTestUtil.maskExtensions(
         AndroidRunConfigurationToken.EP_NAME,
         listOf(object : AndroidRunConfigurationToken<AndroidProjectSystem> {
            override fun isApplicable(projectSystem: AndroidProjectSystem): Boolean = true
            override fun getModuleForAndroidRunConfiguration(
               projectSystem: AndroidProjectSystem,
               module: Module
            ): Module = module

            override fun getModuleForAndroidTestRunConfiguration(
               projectSystem: AndroidProjectSystem,
               module: Module
            ): Module = module
         }),
         testRootDisposable
      )
   }
}
