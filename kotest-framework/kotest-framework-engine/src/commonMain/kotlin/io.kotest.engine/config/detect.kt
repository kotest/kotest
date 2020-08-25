package io.kotest.engine.config

import io.kotest.core.Tags
import io.kotest.core.config.Configuration
import io.kotest.core.extensions.Extension
import io.kotest.core.filter.Filter
import io.kotest.core.listeners.Listener
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestNameCase
import io.kotest.fp.Option
import io.kotest.fp.orElse
import kotlin.reflect.KClass

/**
 * Loads a config object from the underlying platform.
 * For example, on the JVM it may scan the classpath and/or look for system properties.
 */
expect fun detectConfig(): DetectedProjectConfig

/**
 * Contains all the configuration details that can be set by a user supplied config object.
 */
data class DetectedProjectConfig(
   val extensions: List<Extension> = emptyList(),
   val listeners: List<Listener> = emptyList(),
   val filters: List<Filter> = emptyList(),
   val tags: Option<Tags> = Option.None,
   val isolationMode: Option<IsolationMode> = Option.None,
   val assertionMode: Option<AssertionMode> = Option.None,
   val testCaseOrder: Option<TestCaseOrder> = Option.None,
   val specExecutionOrder: Option<SpecExecutionOrder> = Option.None,
   val failOnIgnoredTests: Option<Boolean> = Option.None,
   val globalAssertSoftly: Option<Boolean> = Option.None,
   val autoScanEnabled: Option<Boolean> = Option.None,
   val autoScanIgnoredClasses: List<KClass<*>> = emptyList(),
   val writeSpecFailureFile: Option<Boolean> = Option.None,
   val specFailureFilePath: Option<String> = Option.None,
   val parallelism: Option<Int> = Option.None,
   val timeout: Option<Long> = Option.None,
   val invocationTimeout: Option<Long> = Option.None,
   val testCaseConfig: Option<TestCaseConfig> = Option.None,
   val includeTestScopeAffixes: Option<Boolean> = Option.None,
   val testNameCase: Option<TestNameCase> = Option.None
)

fun DetectedProjectConfig.merge(other: DetectedProjectConfig): DetectedProjectConfig {
   return DetectedProjectConfig(
      extensions = this.extensions + other.extensions,
      listeners = this.listeners + other.listeners,
      filters = this.filters + other.filters,
      isolationMode = this.isolationMode.orElse(other.isolationMode),
      assertionMode = this.assertionMode.orElse(other.assertionMode),
      testCaseOrder = this.testCaseOrder.orElse(other.testCaseOrder),
      specExecutionOrder = this.specExecutionOrder.orElse(other.specExecutionOrder),
      failOnIgnoredTests = this.failOnIgnoredTests.orElse(other.failOnIgnoredTests),
      globalAssertSoftly = this.globalAssertSoftly.orElse(other.globalAssertSoftly),
      autoScanEnabled = this.autoScanEnabled.orElse(other.autoScanEnabled),
      autoScanIgnoredClasses = this.autoScanIgnoredClasses + other.autoScanIgnoredClasses,
      writeSpecFailureFile = this.writeSpecFailureFile.orElse(other.writeSpecFailureFile),
      specFailureFilePath = this.specFailureFilePath.orElse(other.specFailureFilePath),
      parallelism = this.parallelism.orElse(other.parallelism),
      timeout = this.timeout.orElse(other.timeout),
      invocationTimeout = this.invocationTimeout.orElse(other.invocationTimeout),
      testCaseConfig = this.testCaseConfig.orElse(other.testCaseConfig),
      includeTestScopeAffixes = this.includeTestScopeAffixes.orElse(other.includeTestScopeAffixes),
      testNameCase = this.testNameCase.orElse(other.testNameCase),
   )
}

/**
 * Applies this config to the given [Configuration] instance.
 */
fun DetectedProjectConfig.apply(configuration: Configuration) {

   configuration.registerListeners(listeners)
   configuration.registerExtensions(extensions)
   configuration.registerFilters(filters)

   assertionMode.forEach { configuration.assertionMode = it }
   testCaseOrder.forEach { configuration.testCaseOrder = it }
   specExecutionOrder.forEach { configuration.specExecutionOrder = it }
   isolationMode.forEach { configuration.isolationMode = it }
   failOnIgnoredTests.forEach { configuration.failOnIgnoredTests = it }
   globalAssertSoftly.forEach { configuration.globalAssertSoftly = it }
   writeSpecFailureFile.forEach { configuration.writeSpecFailureFile = it }
   specFailureFilePath.forEach { configuration.specFailureFilePath = it }
   parallelism.forEach { configuration.parallelism = it }
   timeout.forEach { configuration.timeout = it }
   invocationTimeout.forEach { configuration.invocationTimeout = it }
   testCaseConfig.forEach { configuration.defaultTestConfig = it }
   includeTestScopeAffixes.forEach { configuration.includeTestScopeAffixes = it }
   testNameCase.forEach { configuration.testNameCase = it }
}
