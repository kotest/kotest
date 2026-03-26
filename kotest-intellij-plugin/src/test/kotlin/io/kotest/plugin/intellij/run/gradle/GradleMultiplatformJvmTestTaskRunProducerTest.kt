package io.kotest.plugin.intellij.run.gradle

import com.intellij.execution.PsiLocation
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.externalSystem.ExternalSystemModulePropertyManager
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.kotest.matchers.shouldBe
import io.kotest.plugin.intellij.psi.elementAtLine
import org.jetbrains.plugins.gradle.service.execution.GradleRunConfiguration
import org.jetbrains.plugins.gradle.settings.GradleProjectSettings
import org.jetbrains.plugins.gradle.settings.GradleSettings
import org.jetbrains.plugins.gradle.settings.TestRunner
import java.nio.file.Paths

/**
 * Tests that [GradleMultiplatformJvmTestTaskRunProducer] sets the `KOTEST_IDEA_PLUGIN` environment
 * variable so the Kotest engine can detect it is running from the IntelliJ plugin.
 */
class GradleMultiplatformJvmTestTaskRunProducerTest : BasePlatformTestCase() {

   override fun getTestDataPath(): String =
      Paths.get("./src/test/resources/").toAbsolutePath().toString()

   /**
    * Calls the protected [GradleMultiplatformJvmTestTaskRunProducer.doSetupConfigurationFromContext]
    * via reflection so that the class need not be made `open` just for testing.
    */
   private fun callDoSetup(
      producer: GradleMultiplatformJvmTestTaskRunProducer,
      configuration: GradleRunConfiguration,
      context: ConfigurationContext,
      ref: Ref<PsiElement?>
   ): Boolean {
      val method = GradleMultiplatformJvmTestTaskRunProducer::class.java.getDeclaredMethod(
         "doSetupConfigurationFromContext",
         GradleRunConfiguration::class.java,
         ConfigurationContext::class.java,
         Ref::class.java
      )
      method.isAccessible = true
      return method.invoke(producer, configuration, context, ref) as Boolean
   }

   /**
    * Verifies that [GradleMultiplatformJvmTestTaskRunProducer.doSetupConfigurationFromContext]
    * sets `KOTEST_IDEA_PLUGIN=true` in the configuration's environment so the Kotest engine
    * knows it is running inside the IntelliJ plugin.
    */
   fun testSetsKotestIdeaPluginEnvVar() {
      setupGradleTestTaskMode()

      val psiFiles = myFixture.configureByFiles(
         "/funspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      // Line 6 is the spec class declaration — a valid spec-level context
      val psiElement = psiFiles[0].elementAtLine(6) ?: error("Could not find PSI element at line 6")
      val context = ConfigurationContext.createEmptyContextForLocation(
         PsiLocation(project, myFixture.module, psiElement)
      )

      val producer = GradleMultiplatformJvmTestTaskRunProducer()
      val configuration = producer.configurationFactory.createTemplateConfiguration(project) as GradleRunConfiguration
      val ref = Ref.create<PsiElement?>(psiElement)

      val result = callDoSetup(producer, configuration, context, ref)

      result shouldBe true
      configuration.settings.env["KOTEST_IDEA_PLUGIN"] shouldBe "true"
   }

   /**
    * Verifies the flag is also present when the context is an individual test (line 22).
    */
   fun testSetsKotestIdeaPluginEnvVarForIndividualTest() {
      setupGradleTestTaskMode()

      val psiFiles = myFixture.configureByFiles(
         "/funspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      // Line 22 is `test("a nested test")` — an individual test context
      val psiElement = psiFiles[0].elementAtLine(22) ?: error("Could not find PSI element at line 22")
      val context = ConfigurationContext.createEmptyContextForLocation(
         PsiLocation(project, myFixture.module, psiElement)
      )

      val producer = GradleMultiplatformJvmTestTaskRunProducer()
      val configuration = producer.configurationFactory.createTemplateConfiguration(project) as GradleRunConfiguration
      val ref = Ref.create<PsiElement?>(psiElement)

      val result = callDoSetup(producer, configuration, context, ref)

      result shouldBe true
      configuration.settings.env["KOTEST_IDEA_PLUGIN"] shouldBe "true"
   }

   /**
    * Configures the test fixture so that [io.kotest.plugin.intellij.run.RunnerModes.mode] returns
    * [io.kotest.plugin.intellij.run.RunnerMode.GRADLE_TEST_TASK].
    *
    * Requires:
    * - A module-level library named `kotest-framework-engine-jvm` (hasKotestEngine = true)
    * - A project-level versioned library ≥ 6.1.x (isKotest61OrAbove = true)
    * - A linked Gradle project with TestRunner.GRADLE (isGradleTestRunner = true)
    */
   private fun setupGradleTestTaskMode() {
      ModuleRootModificationUtil.updateModel(myFixture.module) { model ->
         if (model.moduleLibraryTable.getLibraryByName("kotest-framework-engine-jvm") == null) {
            model.moduleLibraryTable.createLibrary("kotest-framework-engine-jvm")
         }
      }
      WriteAction.runAndWait<Exception> {
         val libName = "io.kotest:kotest-framework-engine-jvm:6.1.3"
         val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
         if (libraryTable.getLibraryByName(libName) == null) {
            libraryTable.createLibrary(libName)
         }

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
}
