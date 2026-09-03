@file:Suppress("DEPRECATION")

package io.kotest.plugin.intellij.run.idea

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs

/**
 * Regression tests for https://github.com/kotest/kotest/issues/6214.
 *
 * The three legacy ("idea") run configuration producers used to build their [getKotestConfigurationType]
 * factory by instantiating `KotestConfigurationType()` directly, e.g.
 * `KotestConfigurationFactory(KotestConfigurationType())`. Since [KotestConfigurationType] has no
 * `equals`/`hashCode`, every such instance was only reference-equal to itself — distinct from the
 * singleton IntelliJ registers via the `<configurationType>` extension point in plugin.xml.
 * `RunManager`'s lookup of existing configurations for a given factory relies on that registered
 * singleton, so a fresh instance on every producer call meant existing configurations were never
 * found, and a new run configuration was created on every gutter run.
 */
class KotestConfigurationTypeTest : BasePlatformTestCase() {

   /** [getKotestConfigurationType] must always return the same, platform-registered instance. */
   fun testGetKotestConfigurationTypeReturnsSameSingletonAcrossCalls() {
      val first = getKotestConfigurationType()
      val second = getKotestConfigurationType()
      first shouldBe second
   }

   /** A directly-instantiated [KotestConfigurationType] must NOT be treated as the same type. */
   fun testDirectlyInstantiatedTypeIsNotTheRegisteredSingleton() {
      val rogue = KotestConfigurationType()
      val registered = getKotestConfigurationType()
      rogue shouldNotBeSameInstanceAs registered
   }

   /** All three legacy producers must share the exact same [getConfigurationFactory] instance. */
   fun testLegacyProducersShareTheSameConfigurationFactory() {
      val specFactory = SpecPlatformRunConfigurationProducer().configurationFactory
      val testFactory = TestPlatformRunConfigurationProducer().configurationFactory
      val packageFactory = PackageRunConfigurationProducer().configurationFactory

      specFactory shouldBe testFactory
      testFactory shouldBe packageFactory
      specFactory shouldBe getKotestConfigurationType().getFactory()
   }
}
