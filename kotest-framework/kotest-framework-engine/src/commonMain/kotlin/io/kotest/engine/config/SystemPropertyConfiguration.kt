package io.kotest.engine.config

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
internal expect fun getSystemPropertyConfiguration(): SystemPropertyConfiguration

interface SystemPropertyConfiguration {
   fun isolationMode(): IsolationMode?
   fun assertionMode(): AssertionMode?
   fun timeout(): Long?
   fun invocationTimeout(): Long?
   fun allowMultilineTestName(): Boolean?
   fun discoveryClasspathScanningEnabled(): Boolean?
   fun disableTestNestedJarScanning(): Boolean?
   fun globalAssertSoftly(): Boolean?
   fun testNameAppendTags(): Boolean?
   fun tagInheritance(): Boolean?
   fun duplicateTestNameMode(): DuplicateTestNameMode?
   fun projectTimeout(): Duration?
   fun minimumRuntimeTestCaseSeverityLevel(): TestCaseSeverityLevel?
}

object NoopSystemPropertyConfiguration : SystemPropertyConfiguration {
   override fun isolationMode(): IsolationMode? = null
   override fun assertionMode(): AssertionMode? = null
   override fun timeout(): Long? = null
   override fun invocationTimeout(): Long? = null
   override fun allowMultilineTestName(): Boolean? = null
   override fun discoveryClasspathScanningEnabled(): Boolean? = null
   override fun disableTestNestedJarScanning(): Boolean? = null
   override fun globalAssertSoftly(): Boolean? = null
   override fun testNameAppendTags(): Boolean? = null
   override fun tagInheritance(): Boolean? = null
   override fun duplicateTestNameMode(): DuplicateTestNameMode? = null
   override fun projectTimeout(): Duration? = null
   override fun minimumRuntimeTestCaseSeverityLevel(): TestCaseSeverityLevel? = null
}
