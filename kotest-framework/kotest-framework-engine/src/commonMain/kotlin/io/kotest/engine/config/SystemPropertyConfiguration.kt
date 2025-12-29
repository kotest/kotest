package io.kotest.engine.config

import io.kotest.core.config.LogLevel
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.engine.concurrency.ConcurrencyOrder
import kotlin.time.Duration

/**
 * Returns a [SystemPropertyConfiguration] which reads configuration from system properties.
 *
 * Note: This function will return null on non-JVM targets.
 */
internal expect fun loadSystemPropertyConfiguration(): SystemPropertyConfiguration?

interface SystemPropertyConfiguration {
   fun isolationMode(): IsolationMode?
   fun assertionMode(): AssertionMode?
   fun timeout(): Duration?
   fun invocationTimeout(): Duration?
   fun projectTimeout(): Duration?
   fun allowMultilineTestName(): Boolean?
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
   fun concurrencyOrder(): ConcurrencyOrder?
   fun specExecutionOrder(): SpecExecutionOrder?
}
