@file:Suppress("MemberVisibilityCanBePrivate")

package io.kotest.engine.config

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.config.DefaultExtensionRegistry
import io.kotest.core.config.Defaults
import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.config.LogLevel
import io.kotest.core.listeners.Listener
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.names.TestNameCase
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.config.TestCaseConfig
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * This class defines project wide settings that are used when executing tests.
 *
 * Some settings at the project level are fallback values. That is, settings specified in a
 * spec or test will test precedence.
 */
class MutableConfiguration {

   companion object {
      @ExperimentalKotest
      const val Sequential = 1

      @ExperimentalKotest
      const val MaxConcurrency = Int.MAX_VALUE
   }

   private val registry: ExtensionRegistry = DefaultExtensionRegistry()

   var writeSpecFailureFile: Boolean = Defaults.writeSpecFailureFile
   var specFailureFilePath: String = Defaults.specFailureFilePath
   var globalAssertSoftly: Boolean = Defaults.globalAssertSoftly
   var testNameCase: TestNameCase = Defaults.defaultTestNameCase
   var failOnIgnoredTests: Boolean = Defaults.failOnIgnoredTests
   var assertionMode: AssertionMode = Defaults.assertionMode
   var parallelism: Int = Defaults.parallelism
   var dispatcherAffinity: Boolean = Defaults.dispatcherAffinity
   var concurrentSpecs: Int? = null

   @ExperimentalKotest
   var concurrentTests: Int = Defaults.concurrentTests
   var timeout: Long = Defaults.defaultTimeoutInMillis
   var invocationTimeout: Long = Defaults.defaultInvocationTimeoutInMillis
   var projectTimeout: Duration? = null
   var logLevel: LogLevel = LogLevel.Off
   var failfast: Boolean = Defaults.failfast
   var blockingTest: Boolean = Defaults.blockingTest
   var severity: TestCaseSeverityLevel = Defaults.severity

   @ExperimentalKotest
   var testCoroutineDispatcher: Boolean = Defaults.testCoroutineDispatcher

   @Deprecated("These settings can be specified individually to provide finer grain control. Deprecated since 5.0")
   var defaultTestConfig: TestCaseConfig = Defaults.testCaseConfig
   var failOnEmptyTestSuite: Boolean = Defaults.failOnEmptyTestSuite
   var coroutineDebugProbes: Boolean = Defaults.coroutineDebugProbes
   var includeTestScopeAffixes: Boolean? = Defaults.defaultIncludeTestScopeAffixes
   var displaySpecIfNoActiveTests: Boolean = Defaults.displaySpecIfNoActiveTests
   var isolationMode: IsolationMode = Defaults.isolationMode
   var testCaseOrder: TestCaseOrder = Defaults.testCaseOrder
   var specExecutionOrder: SpecExecutionOrder = Defaults.specExecutionOrder
   var removeTestNameWhitespace: Boolean = false
   var testNameAppendTags: Boolean = false
   var duplicateTestNameMode: DuplicateTestNameMode = Defaults.duplicateTestNameMode
   var displayFullTestPath: Boolean = Defaults.displayFullTestPath

   /**
    * Returns all globally registered [Listener]s.
    */
   @Deprecated("Listeners have been subsumed into extensions", level = DeprecationLevel.ERROR)
   fun listeners(): Nothing = throw UnsupportedOperationException()

   /**
    * Returns the [ExtensionRegistry] that contains all extensions registered through
    * this configuration instance.
    */
   fun registry(): ExtensionRegistry = registry

   @Deprecated(
      "Use extensions().add(). Deprecated since 5.0",
      ReplaceWith("listeners.forEach { registry().add(it) }")
   )
   fun registerListeners(listeners: List<Listener>) = listeners.forEach { registry().add(it) }
}

fun MutableConfiguration.toConfiguration(): ProjectConfiguration {
   return ProjectConfiguration(
      extensions = this.registry(),
      writeSpecFailureFile = this.writeSpecFailureFile,
      specFailureFilePath = this.specFailureFilePath,
      globalAssertSoftly = this.globalAssertSoftly,
      testNameCase = this.testNameCase,
      failOnIgnoredTests = this.failOnIgnoredTests,
      assertionMode = this.assertionMode,
      testCaseOrder = this.testCaseOrder,
      displaySpecIfNoActiveTests = this.displaySpecIfNoActiveTests,
      failOnEmptyTestSuite = this.failOnEmptyTestSuite,
      defaultTestConfig = this.defaultTestConfig,
      coroutineDebugProbes = this.coroutineDebugProbes,
      includeTestScopeAffixes = this.includeTestScopeAffixes,
      isolationMode = this.isolationMode,
      specExecutionOrder = this.specExecutionOrder,
      removeTestNameWhitespace = this.removeTestNameWhitespace,
      testNameAppendTags = this.testNameAppendTags,
      duplicateTestNameMode = this.duplicateTestNameMode,
      timeout = this.timeout.milliseconds,
      displayFullTestPath = this.displayFullTestPath,
      invocationTimeout = this.invocationTimeout.milliseconds,
      projectTimeout = this.projectTimeout,
      logLevel = this.logLevel,
      dispatcherAffinity = this.dispatcherAffinity,
      concurrentSpecs = this.concurrentSpecs,
      concurrentTests = this.concurrentTests,
      parallelism = this.parallelism,
      testCoroutineDispatcher = this.testCoroutineDispatcher,
      failfast = this.failfast,
      blockingTest = this.blockingTest,
      severity = this.severity,
   )
}

