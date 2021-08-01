package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.Configuration
import io.kotest.core.listeners.ProjectListener
import io.kotest.fp.foreach

/**
 * Applies settings from a [AbstractProjectConfig] instance to the given [Configuration].
 */
internal fun applyConfigFromProjectConfig(config: AbstractProjectConfig, configuration: Configuration) {

   // assertions
   config.assertionMode.foreach { configuration.assertionMode = it }
   config.globalAssertSoftly.foreach { configuration.globalAssertSoftly = it }

   // project run options
   config.failOnIgnoredTests.foreach { configuration.failOnIgnoredTests = it }
   config.failOnEmptyTestSuite.foreach { configuration.failOnEmptyTestSuite = it }
   config.testCaseOrder.foreach { configuration.testCaseOrder = it }
   config.specExecutionOrder.foreach { configuration.specExecutionOrder = it }
   config.writeSpecFailureFile.foreach { configuration.writeSpecFailureFile = it }

   // concurrency
   config.parallelism.foreach { configuration.parallelism = it }
   config.concurrentTests.foreach { configuration.concurrentTests = it }
   config.concurrentSpecs.foreach { configuration.concurrentSpecs = it }
   config.isolationMode.foreach { configuration.isolationMode = it }

   // timeouts
   config.timeout.foreach { configuration.timeout = it.inWholeMilliseconds }
   config.invocationTimeout.foreach { configuration.invocationTimeout = it }
   config.projectTimeout.foreach { configuration.projectTimeout = it }

   // test names
   config.includeTestScopePrefixes.foreach { configuration.includeTestScopeAffixes = it }
   config.testNameRemoveWhitespace.foreach { configuration.removeTestNameWhitespace = it }
   config.testNameAppendTags.foreach { configuration.testNameAppendTags = it }
   config.duplicateTestNameMode.foreach { configuration.duplicateTestNameMode = it }
   config.testNameCase.foreach { configuration.testNameCase = it }

   config.defaultTestCaseConfig.foreach { configuration.defaultTestConfig = it }

   // the project config object allows us to define project event methods, which we
   // wrap into a project listener and register as normal
   val projectListener = object : ProjectListener {
      override suspend fun beforeProject() {
         config.beforeProject()
         config.beforeAll()
      }

      override suspend fun afterProject() {
         config.afterProject()
         config.afterAll()
      }
   }

   configuration.registerListeners(config.listeners() + listOf(projectListener))
   configuration.registerExtensions(config.extensions())
   configuration.registerFilters(config.filters())
}
