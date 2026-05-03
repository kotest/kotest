@file:Suppress("DEPRECATION")

package io.kotest.plugin.intellij.run.idea

import com.intellij.execution.PsiLocation
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.psi.PsiManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.kotest.matchers.shouldBe
import java.nio.file.Paths

/**
 * Tests that [SpecPlatformRunConfigurationProducer] does not offer a run configuration when the
 * context element is a package/directory. Package-level runs are handled exclusively by
 * [PackageRunConfigurationProducer].
 */
class SpecPlatformRunConfigurationProducerTest : BasePlatformTestCase() {

   override fun getTestDataPath(): String =
      Paths.get("./src/test/resources/").toAbsolutePath().toString()

   /**
    * Adds a module-level library whose name contains "kotest-framework-engine" so that
    * [io.kotest.plugin.intellij.dependencies.ModuleDependencies.hasKotestEngine] returns true.
    * Without a versioned project-level library and without a linked Gradle project the runner
    * mode will be [io.kotest.plugin.intellij.run.RunnerMode.LEGACY], which is the mode handled
    * by [SpecPlatformRunConfigurationProducer].
    */
   private fun setupLegacyRunnerMode() {
      ModuleRootModificationUtil.updateModel(myFixture.module) { model ->
         model.moduleLibraryTable.createLibrary("kotest-framework-engine-jvm")
      }
   }

   /**
    * When the context element is a [com.intellij.psi.PsiDirectory] (i.e. the user has selected
    * a package in the project view), [SpecPlatformRunConfigurationProducer] must not produce a
    * run configuration. The package-scoped run is the responsibility of
    * [PackageRunConfigurationProducer].
    */
   fun testSpecProducerDoesNotContributeForPackageElement() {
      setupLegacyRunnerMode()

      val vDir = myFixture.tempDirFixture.findOrCreateDir("io/kotest/samples")
      val psiDir = PsiManager.getInstance(project).findDirectory(vDir)
         ?: error("Could not get PsiDirectory for virtual directory")

      val context = ConfigurationContext.createEmptyContextForLocation(
         PsiLocation(project, myFixture.module, psiDir)
      )

      val runConfig = SpecPlatformRunConfigurationProducer().createConfigurationFromContext(context)
      runConfig shouldBe null
   }
}
