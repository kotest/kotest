@file:Suppress("DEPRECATION")

package io.kotest.plugin.intellij.run.idea

import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.actions.ConfigurationFromContext
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.psi.PsiElement
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.kotest.matchers.shouldBe
import java.nio.file.Paths

/**
 * Tests that [PackageRunConfigurationProducer.shouldReplace] and
 * [PackageRunConfigurationProducer.isPreferredConfiguration] correctly prefer the package-level
 * configuration over spec/test-level [KotestRunConfiguration]s.
 *
 * When running tests from a package element, both [PackageRunConfigurationProducer] and
 * [SpecPlatformRunConfigurationProducer] (or [TestPlatformRunConfigurationProducer]) can
 * occasionally produce a [KotestRunConfiguration]. Because both produce the same configuration
 * type, the default "neither replaces the other" tie-breaking previously caused the spec producer
 * to win (it is registered first in plugin.xml). The fix is to make [PackageRunConfigurationProducer]
 * explicitly prefer itself over spec/test-level Kotest configurations.
 */
class PackageRunConfigurationProducerTest : BasePlatformTestCase() {

   override fun getTestDataPath(): String =
      Paths.get("./src/test/resources/").toAbsolutePath().toString()

   // ---------------------------------------------------------------------------
   // Helpers
   // ---------------------------------------------------------------------------

   /** Creates a fresh [KotestRunConfiguration] with the given properties. */
   private fun makeConfig(
      packageName: String? = null,
      specsName: String? = null,
   ): KotestRunConfiguration {
      val factory = KotestConfigurationFactory(KotestConfigurationType())
      return (factory.createTemplateConfiguration(project) as KotestRunConfiguration).also {
         if (packageName != null) it.setPackageName(packageName)
         if (specsName != null) it.setSpecsName(specsName)
      }
   }

   /**
    * Wraps a [KotestRunConfiguration] in a minimal [ConfigurationFromContext].
    *
    * [ConfigurationFromContext.getConfiguration] is overridden directly so that the test does not
    * depend on [RunnerAndConfigurationSettings] plumbing: [shouldReplace] and
    * [isPreferredConfiguration] only read [other.configuration], so this is sufficient.
    */
   private fun configFromContext(config: KotestRunConfiguration, element: PsiElement) =
      object : ConfigurationFromContext() {
         override fun getConfiguration(): RunConfiguration = config
         override fun getConfigurationSettings(): RunnerAndConfigurationSettings =
            error("getConfigurationSettings should not be called in this test")
         override fun setConfigurationSettings(s: RunnerAndConfigurationSettings) = Unit
         override fun getSourceElement(): PsiElement = element
      }

   // ---------------------------------------------------------------------------
   // shouldReplace
   // ---------------------------------------------------------------------------

   /**
    * A package-level [KotestRunConfiguration] should replace a spec-level one so that running
    * from a package element always produces a package-scoped run, not a single-spec run.
    */
   fun testPackageShouldReplaceSpecLevelKotestConfig() {
      val psiFiles = myFixture.configureByFiles("/funspec.kt", "/io/kotest/core/spec/style/specs.kt")
      val element = psiFiles[0]

      val specFromCtx = configFromContext(makeConfig(specsName = "io.kotest.samples.FunSpecTest"), element)
      val pkgFromCtx = configFromContext(makeConfig(packageName = "io.kotest.samples"), element)

      PackageRunConfigurationProducer().shouldReplace(pkgFromCtx, specFromCtx) shouldBe true
   }

   /**
    * A package-level config must NOT replace another package-level config — the producers should
    * coexist without stomping on each other.
    */
   fun testPackageShouldNotReplaceAnotherPackageLevelKotestConfig() {
      val psiFiles = myFixture.configureByFiles("/funspec.kt", "/io/kotest/core/spec/style/specs.kt")
      val element = psiFiles[0]

      val pkgFromCtx1 = configFromContext(makeConfig(packageName = "io.kotest.samples"), element)
      val pkgFromCtx2 = configFromContext(makeConfig(packageName = "io.kotest.samples.sub"), element)

      PackageRunConfigurationProducer().shouldReplace(pkgFromCtx1, pkgFromCtx2) shouldBe false
   }

   /**
    * The spec producer must NOT replace a package-level config — the package config is broader and
    * should always take precedence when the context originates from a package element.
    */
   fun testSpecShouldNotReplacePackageLevelKotestConfig() {
      val psiFiles = myFixture.configureByFiles("/funspec.kt", "/io/kotest/core/spec/style/specs.kt")
      val element = psiFiles[0]

      val specFromCtx = configFromContext(makeConfig(specsName = "io.kotest.samples.FunSpecTest"), element)
      val pkgFromCtx = configFromContext(makeConfig(packageName = "io.kotest.samples"), element)

      SpecPlatformRunConfigurationProducer().shouldReplace(specFromCtx, pkgFromCtx) shouldBe false
   }

   // ---------------------------------------------------------------------------
   // isPreferredConfiguration
   // ---------------------------------------------------------------------------

   /**
    * The package config should report itself as preferred over a spec-level config when both are
    * candidates for the same context.
    */
   fun testPackageIsPreferredOverSpecLevelKotestConfig() {
      val psiFiles = myFixture.configureByFiles("/funspec.kt", "/io/kotest/core/spec/style/specs.kt")
      val element = psiFiles[0]

      val specFromCtx = configFromContext(makeConfig(specsName = "io.kotest.samples.FunSpecTest"), element)
      val pkgFromCtx = configFromContext(makeConfig(packageName = "io.kotest.samples"), element)

      PackageRunConfigurationProducer().isPreferredConfiguration(pkgFromCtx, specFromCtx) shouldBe true
   }

   /**
    * The package config should NOT claim preference over another package-level config.
    */
   fun testPackageIsNotPreferredOverAnotherPackageLevelKotestConfig() {
      val psiFiles = myFixture.configureByFiles("/funspec.kt", "/io/kotest/core/spec/style/specs.kt")
      val element = psiFiles[0]

      val pkgFromCtx1 = configFromContext(makeConfig(packageName = "io.kotest.samples"), element)
      val pkgFromCtx2 = configFromContext(makeConfig(packageName = "io.kotest.samples.sub"), element)

      PackageRunConfigurationProducer().isPreferredConfiguration(pkgFromCtx1, pkgFromCtx2) shouldBe false
   }

   /**
    * The spec producer must NOT claim preference over a package-level config.
    */
   fun testSpecIsNotPreferredOverPackageLevelKotestConfig() {
      val psiFiles = myFixture.configureByFiles("/funspec.kt", "/io/kotest/core/spec/style/specs.kt")
      val element = psiFiles[0]

      val specFromCtx = configFromContext(makeConfig(specsName = "io.kotest.samples.FunSpecTest"), element)
      val pkgFromCtx = configFromContext(makeConfig(packageName = "io.kotest.samples"), element)

      SpecPlatformRunConfigurationProducer().isPreferredConfiguration(specFromCtx, pkgFromCtx) shouldBe false
   }
}
