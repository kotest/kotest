package io.kotest.engine.config

import io.kotest.core.Tags
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
   val testCaseOrder: TestCaseOrder? = null,
   val specExecutionOrder: SpecExecutionOrder? = null,
   val failOnIgnoredTests: Boolean? = null,
   val globalAssertSoftly: Boolean? = null,
   val autoScanEnabled: Boolean? = null,
   val autoScanIgnoredClasses: List<KClass<*>> = emptyList(),
   val writeSpecFailureFile: Boolean? = null,
   val specFailureFilePath: String? = null,
   val parallelism: Option<Int> = Option.None,
   val timeout: Option<Long> = Option.None,
   val invocationTimeout: Option<Long> = Option.None,
   val testCaseConfig: TestCaseConfig? = null,
   val includeTestScopeAffixes: Boolean? = null,
   val testNameCase: TestNameCase? = null
)

fun DetectedProjectConfig.merge(other: DetectedProjectConfig): DetectedProjectConfig {
   return DetectedProjectConfig(
      extensions = this.extensions + other.extensions,
      listeners = this.listeners + other.listeners,
      filters = this.filters + other.filters,
      isolationMode = this.isolationMode.orElse(other.isolationMode),
      assertionMode = this.assertionMode.orElse(other.assertionMode),
      testCaseOrder = this.testCaseOrder ?: other.testCaseOrder,
      specExecutionOrder = this.specExecutionOrder ?: other.specExecutionOrder,
      failOnIgnoredTests = this.failOnIgnoredTests ?: other.failOnIgnoredTests,
      globalAssertSoftly = this.globalAssertSoftly ?: other.globalAssertSoftly,
      autoScanEnabled = this.autoScanEnabled ?: other.autoScanEnabled,
      autoScanIgnoredClasses = this.autoScanIgnoredClasses + other.autoScanIgnoredClasses,
      writeSpecFailureFile = this.writeSpecFailureFile ?: other.writeSpecFailureFile,
      specFailureFilePath = this.specFailureFilePath ?: other.specFailureFilePath,
      parallelism = this.parallelism.orElse(other.parallelism),
      timeout = this.timeout.orElse(other.timeout),
      invocationTimeout = this.invocationTimeout.orElse(other.invocationTimeout),
      testCaseConfig = this.testCaseConfig ?: other.testCaseConfig,
      includeTestScopeAffixes = this.includeTestScopeAffixes ?: other.includeTestScopeAffixes,
      testNameCase = this.testNameCase ?: other.testNameCase,
   )
}
