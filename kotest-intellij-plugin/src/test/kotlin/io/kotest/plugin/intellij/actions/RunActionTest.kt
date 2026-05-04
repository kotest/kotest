package io.kotest.plugin.intellij.actions

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
import io.kotest.plugin.intellij.psi.enclosingKtClass
import io.kotest.plugin.intellij.run.gradle.GradleMultiplatformJvmTestTaskRunProducer
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.plugins.gradle.service.execution.GradleRunConfiguration
import org.jetbrains.plugins.gradle.settings.GradleProjectSettings
import org.jetbrains.plugins.gradle.settings.GradleSettings
import org.jetbrains.plugins.gradle.settings.TestRunner
import java.nio.file.Paths

/**
 * Tests the contract between the action dispatch layer ([runSpec], [runTest]) and the run
 * configuration producers.
 *
 * The action layer owns two decisions:
 * 1. **PSI anchor selection** — [runSpec] passes
 *    [nameIdentifier][org.jetbrains.kotlin.psi.KtNamedDeclaration.nameIdentifier] (not the
 *    [KtClassOrObject] itself) because [io.kotest.plugin.intellij.psi.enclosingSpec] uses
 *    `getStrictParentOfType<KtClassOrObject>()` which starts from the element's *parent*.
 * 2. **Mode-based producer routing** — the `when` branch in [runSpec]/[runTest] selects
 *    [GradleMultiplatformJvmTestTaskRunProducer] for
 *    [GRADLE_TEST_TASK][io.kotest.plugin.intellij.run.RunnerMode.GRADLE_TEST_TASK] and
 *    [GradleKotestTaskRunProducer][io.kotest.plugin.intellij.run.gradle.GradleKotestTaskRunProducer]
 *    for [GRADLE_KOTEST_TASK][io.kotest.plugin.intellij.run.RunnerMode.GRADLE_KOTEST_TASK].
 *
 * This class tests the matrix of **(anchor type × mode)** to verify that the correct
 * producer accepts the anchor the action layer would pass, and declines otherwise.
 */
@Suppress("DEPRECATION")
class RunActionTest : BasePlatformTestCase() {

   override fun getTestDataPath(): String =
      Paths.get("./src/test/resources/").toAbsolutePath().toString()

   // ── PSI anchor invariant ───────────────────────────────────────────────────

   /**
    * Verifies the structural reason that [runSpec] must use
    * [nameIdentifier][org.jetbrains.kotlin.psi.KtNamedDeclaration.nameIdentifier]:
    *
    * - `getStrictParentOfType<KtClassOrObject>()` on the spec class itself returns null,
    *   because it starts from the element's *parent* and a top-level class has no enclosing
    *   class.
    * - The same call on `nameIdentifier` returns the spec class, because `nameIdentifier`
    *   is a child whose strict parent is the class declaration.
    *
    * This is the root invariant that both [GradleMultiplatformJvmTestTaskRunProducer] and
    * [GradleKotestTaskRunProducer][io.kotest.plugin.intellij.run.gradle.GradleKotestTaskRunProducer]
    * rely on via [enclosingSpec][io.kotest.plugin.intellij.psi.enclosingSpec].
    */
   fun testNameIdentifierResolvesToSpecViaStrictParent() {
      val specClass = specClass()
      val nameIdentifier = specClass.nameIdentifier
         ?: error("Spec class has no nameIdentifier")

      specClass.getStrictParentOfType<KtClassOrObject>() shouldBe null
      nameIdentifier.getStrictParentOfType<KtClassOrObject>() shouldBe specClass
   }

   // ── GRADLE_TEST_TASK mode × anchor type ────────────────────────────────────

   /**
    * [runSpec] passes `nameIdentifier` → producer accepts.
    */
   fun testGradleTestTask_acceptsSpecNameIdentifier() {
      setupGradleTestTaskMode()
      callDoSetupForGradleTestTask(specNameIdentifier()) shouldBe true
   }

   /**
    * Negative: passing the [KtClassOrObject] directly would break — producer declines.
    * This is why [runSpec] uses `nameIdentifier` instead.
    */
   fun testGradleTestTask_declinesSpecClassDirectly() {
      setupGradleTestTaskMode()
      callDoSetupForGradleTestTask(specClass()) shouldBe false
   }

   /**
    * [runTest] passes the test element directly → producer accepts.
    */
   fun testGradleTestTask_acceptsTestElement() {
      setupGradleTestTaskMode()
      callDoSetupForGradleTestTask(testElement()) shouldBe true
   }

   // ── LEGACY mode: Gradle producer declines all anchors ──────────────────────

   /**
    * In [LEGACY][io.kotest.plugin.intellij.run.RunnerMode.LEGACY] mode the Gradle producer's
    * mode guard rejects early, so [runSpec]/[runTest] correctly fall through to the legacy path.
    */
   fun testLegacy_gradleTestTaskProducerDeclinesSpecNameIdentifier() {
      setupLegacyRunnerMode()
      callDoSetupForGradleTestTask(specNameIdentifier()) shouldBe false
   }

   fun testLegacy_gradleTestTaskProducerDeclinesTestElement() {
      setupLegacyRunnerMode()
      callDoSetupForGradleTestTask(testElement()) shouldBe false
   }

   // ── PSI anchor helpers ─────────────────────────────────────────────────────

   private fun loadFixture() = myFixture.configureByFiles(
      "/funspec.kt",
      "/io/kotest/core/spec/style/specs.kt"
   )

   /** The spec [KtClassOrObject] at line 6 — the element [runSpec] receives from the tree node. */
   private fun specClass(): KtClassOrObject {
      val psiFiles = loadFixture()
      return psiFiles[0].elementAtLine(6)?.enclosingKtClass()
         ?: error("Could not find spec class at line 6")
   }

   /** `specClass().nameIdentifier` — the anchor [runSpec] actually passes to the producer. */
   private fun specNameIdentifier(): PsiElement {
      return specClass().nameIdentifier
         ?: error("Spec class has no nameIdentifier")
   }

   /** The PSI element at line 22 (`test("a nested test")`) — the anchor [runTest] passes. */
   private fun testElement(): PsiElement {
      val psiFiles = loadFixture()
      return psiFiles[0].elementAtLine(22)
         ?: error("Could not find test element at line 22")
   }

   // ── Producer invocation ────────────────────────────────────────────────────

   /**
    * Calls the protected [GradleMultiplatformJvmTestTaskRunProducer.doSetupConfigurationFromContext]
    * via reflection for the given [psiElement].
    */
   private fun callDoSetupForGradleTestTask(psiElement: PsiElement): Boolean {
      val producer = GradleMultiplatformJvmTestTaskRunProducer()
      val configuration = producer.configurationFactory.createTemplateConfiguration(project) as GradleRunConfiguration
      val context = ConfigurationContext.createEmptyContextForLocation(
         PsiLocation(project, myFixture.module, psiElement)
      )
      val ref = Ref.create<PsiElement?>(psiElement)

      val method = GradleMultiplatformJvmTestTaskRunProducer::class.java.getDeclaredMethod(
         "doSetupConfigurationFromContext",
         GradleRunConfiguration::class.java,
         ConfigurationContext::class.java,
         Ref::class.java
      )
      method.isAccessible = true
      return method.invoke(producer, configuration, context, ref) as Boolean
   }

   // ── Setup / teardown ───────────────────────────────────────────────────────

   /**
    * Removes project-level Gradle settings and versioned engine libraries added during a test
    * so they don't bleed into subsequent tests. [BasePlatformTestCase] reuses the same project
    * instance across all tests in a class, so project-level state must be cleaned up explicitly.
    */
   override fun tearDown() {
      try {
         WriteAction.runAndWait<Exception> {
            val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
            listOf("io.kotest:kotest-framework-engine-jvm:6.1.3").forEach { name ->
               libraryTable.getLibraryByName(name)?.let { libraryTable.removeLibrary(it) }
            }
         }
         val projectPath = project.basePath
         if (projectPath != null) {
            val gradleSettings = GradleSettings.getInstance(project)
            if (gradleSettings.getLinkedProjectSettings(projectPath) != null) {
               gradleSettings.unlinkExternalProject(projectPath)
            }
         }
      } finally {
         super.tearDown()
      }
   }

   /**
    * Configures the fixture so that [io.kotest.plugin.intellij.run.RunnerModes.mode] returns
    * [io.kotest.plugin.intellij.run.RunnerMode.GRADLE_TEST_TASK]:
    * - Module library `kotest-framework-engine-jvm` → [hasKotestEngine][io.kotest.plugin.intellij.dependencies.ModuleDependencies.hasKotestEngine] = true
    * - Project library `io.kotest:kotest-framework-engine-jvm:6.1.3` → [isKotest61OrAbove][io.kotest.plugin.intellij.gradle.GradleUtils.isKotest61OrAbove] = true
    * - Linked Gradle project with [TestRunner.GRADLE] → [isGradleTestRunner][io.kotest.plugin.intellij.gradle.GradleUtils.isGradleTestRunner] = true
    */
   private fun setupGradleTestTaskMode() {
      addKotestEngineModuleLibrary()
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

   /**
    * Adds the engine library so [hasKotestEngine][io.kotest.plugin.intellij.dependencies.ModuleDependencies.hasKotestEngine]
    * returns true, but does not link a Gradle project — leaving the runner mode as
    * [LEGACY][io.kotest.plugin.intellij.run.RunnerMode.LEGACY].
    */
   private fun setupLegacyRunnerMode() {
      addKotestEngineModuleLibrary()
   }

   private fun addKotestEngineModuleLibrary() {
      ModuleRootModificationUtil.updateModel(myFixture.module) { model ->
         if (model.moduleLibraryTable.getLibraryByName("kotest-framework-engine-jvm") == null) {
            model.moduleLibraryTable.createLibrary("kotest-framework-engine-jvm")
         }
      }
   }
}
