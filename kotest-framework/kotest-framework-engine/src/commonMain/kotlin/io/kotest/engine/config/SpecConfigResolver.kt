package io.kotest.engine.config

import io.kotest.core.config.AbstractPackageConfig
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.functionOverrideCallbacks
import io.kotest.core.test.TestCaseOrder
import io.kotest.engine.concurrency.TestExecutionMode
import io.kotest.engine.coroutines.CoroutineDispatcherFactory
import io.kotest.engine.extensions.EmptyExtensionRegistry
import io.kotest.engine.extensions.ExtensionRegistry

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
   private val config: AbstractProjectConfig?,
   private val registry: ExtensionRegistry,
) {

   constructor() : this(null)
   constructor(config: AbstractProjectConfig?) : this(config, EmptyExtensionRegistry)

   private val systemPropertyConfiguration = loadSystemPropertyConfiguration()

   /**
    * Resolves the [TestExecutionMode] for the given spec, first checking spec level config,
    * before using project level default.
    */
   fun testExecutionMode(spec: Spec): TestExecutionMode {
      return spec.testExecutionMode
         ?: spec.testExecutionMode()
         ?: config?.testExecutionMode
         ?: Defaults.TEST_EXECUTION_MODE
   }

   /**
    * Resolves the [IsolationMode] for the given spec.
    */
   fun isolationMode(spec: Spec): IsolationMode {
      return spec.isolationMode()
         ?: spec.isolationMode
         ?: config?.isolationMode
         ?: systemPropertyConfiguration.isolationMode()
         ?: Defaults.ISOLATION_MODE
   }

   /**
    * Returns the [TestCaseOrder] applicable for the root tests in this spec.
    */
   fun testCaseOrder(spec: Spec): TestCaseOrder {
      return spec.testOrder
         ?: spec.testCaseOrder()
         ?: spec.defaultTestConfig?.testOrder
         ?: packageConfigs(spec).firstNotNullOfOrNull { it.testCaseOrder }
         ?: config?.testCaseOrder
         ?: Defaults.TEST_CASE_ORDER
   }

   /**
    * Returns the [DuplicateTestNameMode] applicable for the tests in this spec.
    */
   fun duplicateTestNameMode(spec: Spec): DuplicateTestNameMode {
      return spec.duplicateTestNameMode
         ?: spec.duplicateTestNameMode()
         ?: spec.defaultTestConfig?.duplicateTestNameMode
         ?: packageConfigs(spec).firstNotNullOfOrNull { it.duplicateTestNameMode }
         ?: config?.duplicateTestNameMode
         ?: systemPropertyConfiguration.duplicateTestNameMode()
         ?: Defaults.DUPLICATE_TEST_NAME_MODE
   }

   fun coroutineDispatcherFactory(spec: Spec): CoroutineDispatcherFactory? {
      return spec.coroutineDispatcherFactory
         ?: spec.coroutineDispatcherFactory()
         ?: config?.coroutineDispatcherFactory
   }

   /**
    * Returns all [Extension]s applicable to a [Spec]. This includes extensions via
    * function overrides, those registered explicitly in the spec as part of the DSL,
    * and project wide extensions from configuration.
    */
   fun extensions(spec: Spec): List<Extension> {
      return spec.extensions() + // overriding the extensions function in the spec
         spec.functionOverrideCallbacks() + // dsl
         spec.registeredExtensions() + // added to the spec via register
         (config?.extensions ?: emptyList()) + // extensions defined at the project level
         registry.all() // globals
   }

   private fun packageConfigs(spec: Spec): List<AbstractPackageConfig> {
      return PackageConfigLoader.configs(spec)
   }
}
