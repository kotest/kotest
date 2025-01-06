package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCaseOrder

/**
 * The [SpecConfigResolver] is responsible for returning the runtime value to use for a given
 * configuration setting based on the various sources of configuration.
 *
 * This class handles settings that should only be configured at the spec level,
 * such as test execution order, or test ordering.
 *
 * For project level settings, see [io.kotest.engine.config.ProjectConfigResolver].
 *
 *  Order of precedence for each setting from highest priority to lowest:
 *
 * - spec level individual test settings
 * - spec level defaults from setting [io.kotest.core.TestConfiguration.defaultTestConfig]
 * - package level defaults from [io.kotest.core.config.AbstractPackageConfig]
 * - project level defaults from [io.kotest.core.config.AbstractProjectConfig]
 * - system property overrides
 * - kotest defaults
 */
class SpecConfigResolver(
   private val projectConfig: AbstractProjectConfig,
) {

   private val systemPropertyConfiguration = loadSystemPropertyConfiguration()

   /**
    * Returns the [TestCaseOrder] applicable for the root tests in this spec.
    */
   fun testCaseOrder(spec: Spec): TestCaseOrder {
      return spec.testOrder
         ?: spec.testCaseOrder()
         ?: spec.defaultTestConfig?.testOrder
         ?: PackageConfigLoader.configs(spec).firstNotNullOfOrNull { it.testCaseOrder }
         ?: projectConfig.testCaseOrder
         ?: Defaults.TEST_CASE_ORDER
   }

   /**
    * Returns the [DuplicateTestNameMode] applicable for the tests in this spec.
    */
   fun duplicateTestNameMode(spec: Spec): DuplicateTestNameMode {
      return spec.duplicateTestNameMode
         ?: spec.duplicateTestNameMode()
         ?: spec.defaultTestConfig?.duplicateTestNameMode
         ?: PackageConfigLoader.configs(spec).firstNotNullOfOrNull { it.duplicateTestNameMode }
         ?: projectConfig.duplicateTestNameMode
         ?: systemPropertyConfiguration.duplicateTestNameMode()
         ?: Defaults.DUPLICATE_TEST_NAME_MODE
   }
}
