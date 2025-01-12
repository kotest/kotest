package io.kotest.engine.config

import io.kotest.core.config.LogLevel
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCaseSeverityLevel
import kotlin.time.Duration

/**
 * Returns a [SystemPropertyConfiguration] which reads configuration from system properties.
 *
 * Note: This function will return a no-op on non-JVM targets.
 */
internal expect fun loadSystemPropertyConfiguration(): SystemPropertyConfiguration

interface SystemPropertyConfiguration {
   fun isolationMode(): IsolationMode?
   fun assertionMode(): AssertionMode?
   fun timeout(): Duration?
   fun invocationTimeout(): Duration?
   fun projectTimeout(): Duration?
   fun allowMultilineTestName(): Boolean?
   fun discoveryClasspathScanningEnabled(): Boolean?
   fun disableTestNestedJarScanning(): Boolean?
   fun globalAssertSoftly(): Boolean?
   fun testNameAppendTags(): Boolean?
   fun tagInheritance(): Boolean?
   fun duplicateTestNameMode(): DuplicateTestNameMode?
   fun minimumRuntimeTestCaseSeverityLevel(): TestCaseSeverityLevel?
   fun coroutineDebugProbes(): Boolean?
   fun ignorePrivateClasses(): Boolean?
   fun displayFullTestPath(): Boolean?
   fun logLevel(): LogLevel?
   fun dumpConfig(): Boolean?
}

object NoopSystemPropertyConfiguration : SystemPropertyConfiguration {
   override fun isolationMode(): IsolationMode? = null
   override fun assertionMode(): AssertionMode? = null
   override fun timeout(): Duration? = null
   override fun invocationTimeout(): Duration? = null
   override fun projectTimeout(): Duration? = null
   override fun allowMultilineTestName(): Boolean? = null
   override fun discoveryClasspathScanningEnabled(): Boolean? = null
   override fun disableTestNestedJarScanning(): Boolean? = null
   override fun globalAssertSoftly(): Boolean? = null
   override fun testNameAppendTags(): Boolean? = null
   override fun tagInheritance(): Boolean? = null
   override fun duplicateTestNameMode(): DuplicateTestNameMode? = null
   override fun minimumRuntimeTestCaseSeverityLevel(): TestCaseSeverityLevel? = null
   override fun coroutineDebugProbes(): Boolean? = null
   override fun ignorePrivateClasses(): Boolean? = null
   override fun displayFullTestPath(): Boolean? = null
   override fun logLevel(): LogLevel? = null
   override fun dumpConfig(): Boolean? = null
}
