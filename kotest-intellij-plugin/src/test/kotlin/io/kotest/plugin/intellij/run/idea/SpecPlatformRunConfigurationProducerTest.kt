@file:Suppress("DEPRECATION")

package io.kotest.plugin.intellij.run.idea

import com.intellij.execution.PsiLocation
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.jetbrains.kotlin.analysis.api.permissions.KaAllowAnalysisOnEdt
import org.jetbrains.kotlin.analysis.api.permissions.allowAnalysisOnEdt
import org.jetbrains.kotlin.lexer.KtTokens
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

   /**
    * Regression: clicking on the class identifier (rather than the literal `class` keyword)
    * used to make every run create a brand-new configuration on Maven projects, because
    * `isConfigurationFromContext` only resolved the spec when the context leaf was the
    * keyword token. Setup, by contrast, also accepted the identifier via the leaf's parent.
    * The producer must report the configuration it just created as belonging to that context.
    */
   @OptIn(KaAllowAnalysisOnEdt::class)
   fun testIsConfigurationFromContextMatchesAfterSetup() {
      setupLegacyRunnerMode()

      val psiFiles = myFixture.configureByFiles(
         "/funspec.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val psiFile = psiFiles[0]
      val document = PsiDocumentManager.getInstance(project).getDocument(psiFile)
         ?: error("Could not load document for funspec.kt")

      // Anchor the context on the class identifier "FunSpecExampleTest" — the bug only
      // surfaced when the leaf was the identifier (not the `class` keyword), since the
      // gutter and editor caret typically resolve to the identifier rather than the keyword.
      val classLineStart = document.getLineStartOffset(5) // 0-based, line 6 in source
      val identifierOffset = classLineStart + "class ".length
      val psiElement = psiFile.findElementAt(identifierOffset)
         ?: error("Could not find element at identifier offset")

      // Sanity-check that we picked the identifier leaf, not the keyword.
      val leaf = psiElement.shouldBeInstanceOf<LeafPsiElement>()
      leaf.elementType shouldBe KtTokens.IDENTIFIER

      val context = ConfigurationContext.createEmptyContextForLocation(
         PsiLocation(project, myFixture.module, psiElement)
      )

      val producer = SpecPlatformRunConfigurationProducer()
      runReadAction {
         allowAnalysisOnEdt {
            val runConfig = producer.createConfigurationFromContext(context)
               ?: error("Producer should have produced a configuration for the spec class")

            val kotestConfig = runConfig.configuration as KotestRunConfiguration
            kotestConfig.getSpecName() shouldBe "io.kotest.samples.gradle.FunSpecExampleTest"

            producer.isConfigurationFromContext(kotestConfig, context) shouldBe true
         }
      }
   }
}
