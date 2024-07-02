package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener

/**
 * Applies settings from a [AbstractProjectConfig] instance to the given [ProjectConfiguration].
 */
internal fun applyConfigFromProjectConfig(config: AbstractProjectConfig, configuration: ProjectConfiguration) {

   // assertions
   config.assertionMode?.let { configuration.assertionMode = it }
   config.globalAssertSoftly?.let { configuration.globalAssertSoftly = it }

   // outputs
   config.displaySpecIfNoActiveTests?.let { configuration.displaySpecIfNoActiveTests = it }

   // project run options
   config.failOnIgnoredTests?.let { configuration.failOnIgnoredTests = it }
   config.failOnEmptyTestSuite?.let { configuration.failOnEmptyTestSuite = it }
   config.testCaseOrder?.let { configuration.testCaseOrder = it }
   config.specExecutionOrder?.let { configuration.specExecutionOrder = it }
   config.writeSpecFailureFile?.let { configuration.writeSpecFailureFile = it }
   config.projectWideFailFast?.let { configuration.projectWideFailFast = it }
   config.allowOutOfOrderCallbacks?.let { configuration.allowOutOfOrderCallbacks = it }
   config.randomOrderSeed?.let { configuration.randomOrderSeed = it }

   // concurrency
   config.parallelism?.let { configuration.parallelism = it }
   config.concurrentTests?.let { configuration.concurrentTests = it }
   config.concurrentSpecs?.let { configuration.concurrentSpecs = it }
   config.isolationMode?.let { configuration.isolationMode = it }
   config.dispatcherAffinity?.let { configuration.dispatcherAffinity = it }

   // timeouts
   config.timeout?.let { configuration.timeout = it.inWholeMilliseconds }
   config.invocationTimeout?.let { configuration.invocationTimeout = it }
   config.projectTimeout?.let { configuration.projectTimeout = it }

   // discovery
   config.discoveryClasspathFallbackEnabled?.let { configuration.discoveryClasspathFallbackEnabled = it }
   config.disableTestNestedJarScanning?.let { configuration.disableTestNestedJarScanning = it }

   // test names
   config.includeTestScopePrefixes?.let { configuration.includeTestScopeAffixes = it }
   config.testNameRemoveWhitespace?.let { configuration.removeTestNameWhitespace = it }
   config.testNameAppendTags?.let { configuration.testNameAppendTags = it }
   config.duplicateTestNameMode?.let { configuration.duplicateTestNameMode = it }
   config.testNameCase?.let { configuration.testNameCase = it }
   config.displayFullTestPath?.let { configuration.displayFullTestPath = it }

   // config
   @Suppress("DEPRECATION") // Remove when removing TestCaseConfig
   config.defaultTestCaseConfig?.let { configuration.defaultTestConfig = it }
   config.logLevel?.let { configuration.logLevel = it }
   config.tagInheritance?.let { configuration.tagInheritance = it }


   // coroutines
   config.coroutineDebugProbes?.let { configuration.coroutineDebugProbes = it }
   @Suppress("DEPRECATION") // Remove when removing testCoroutineDispatcher
   config.testCoroutineDispatcher.let { configuration.testCoroutineDispatcher = it }
   config.coroutineTestScope?.let { configuration.coroutineTestScope = it }

   // the project config object allows us to define project event methods, which we
   // wrap into a project listener and register as normal
   val projectListener = object : BeforeProjectListener, AfterProjectListener {

      override suspend fun beforeProject() {
         config.beforeProject()
         @Suppress("DEPRECATION") // Remove when removing beforeAll
         config.beforeAll()
      }

      override suspend fun afterProject() {
         config.afterProject()
         @Suppress("DEPRECATION") // Remove when removing afterAll
         config.afterAll()
      }
   }

   @Suppress("DEPRECATION") // Remove when removing Listener
   val exts = config.listeners() + listOf(projectListener) + config.extensions() + config.filters()
   exts.forEach { configuration.registry.add(it) }
}
