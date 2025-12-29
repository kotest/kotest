package io.kotest.engine.config

import io.kotest.common.sysprop
import io.kotest.common.syspropOrEnv
import io.kotest.core.config.LogLevel
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.engine.concurrency.ConcurrencyOrder
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal actual fun loadSystemPropertyConfiguration(): SystemPropertyConfiguration? = JvmSystemPropertyConfiguration

/**
 * Returns config values from system properties.
 */
internal object JvmSystemPropertyConfiguration : SystemPropertyConfiguration {

   override fun isolationMode(): IsolationMode? =
      sysprop(KotestEngineProperties.ISOLATION_MODE)?.let { IsolationMode.valueOf(it) }

   override fun assertionMode(): AssertionMode? =
      sysprop(KotestEngineProperties.ASSERTION_MODE)?.let { AssertionMode.valueOf(it) }

   override fun timeout(): Duration? =
      sysprop(KotestEngineProperties.TIMEOUT)?.toLong()?.milliseconds

   override fun invocationTimeout(): Duration? =
      sysprop(KotestEngineProperties.INVOCATION_TIMEOUT)?.toLong()?.milliseconds

   override fun allowMultilineTestName(): Boolean? =
      sysprop(KotestEngineProperties.ALLOW_MULTILINE_TEST_NAME)?.let { it.uppercase() == "TRUE" }

   override fun globalAssertSoftly(): Boolean? =
      sysprop(KotestEngineProperties.GLOBAL_ASSERT_SOFTLY)?.let { it.uppercase() == "TRUE" }

   override fun testNameAppendTags(): Boolean? =
      sysprop(KotestEngineProperties.TEST_NAME_APPEND_TAGS)?.let { it.uppercase() == "TRUE" }

   override fun tagInheritance(): Boolean? =
      syspropOrEnv(KotestEngineProperties.TAG_INHERITANCE)?.let { it.uppercase() == "TRUE" }

   override fun ignorePrivateClasses(): Boolean? =
      syspropOrEnv(KotestEngineProperties.IGNORE_PRIVATE_CLASSES)?.let { it.uppercase() == "TRUE" }

   override fun displayFullTestPath(): Boolean? =
      syspropOrEnv(KotestEngineProperties.DISPLAY_FULL_TEST_PATH)?.let { it.uppercase() == "TRUE" }

   override fun dumpConfig(): Boolean? =
      syspropOrEnv(KotestEngineProperties.DUMP_CONFIG)?.let { it.uppercase() == "TRUE" }

   override fun duplicateTestNameMode(): DuplicateTestNameMode? =
      sysprop(KotestEngineProperties.DUPLICATE_TEST_NAME_MODE)?.let { DuplicateTestNameMode.valueOf(it) }

   override fun concurrencyOrder(): ConcurrencyOrder? =
      sysprop(KotestEngineProperties.CONCURRENCY_ORDER)?.let { ConcurrencyOrder.valueOf(it) }

   override fun specExecutionOrder(): SpecExecutionOrder? =
      sysprop(KotestEngineProperties.SPEC_EXECUTION_ORDER)?.let { SpecExecutionOrder.valueOf(it) }

   override fun projectTimeout(): Duration? {
      val d = sysprop(KotestEngineProperties.PROJECT_TIMEOUT)?.toLong() ?: return null
      return d.milliseconds
   }

   override fun logLevel(): LogLevel? {
      return syspropOrEnv(KotestEngineProperties.LOG_LEVEL)?.let { LogLevel.from(it) }
   }

   override fun minimumRuntimeTestCaseSeverityLevel(): TestCaseSeverityLevel? {
      return sysprop(KotestEngineProperties.TEST_SEVERITY)
         ?.let { TestCaseSeverityLevel.valueOf(it) }
   }

   override fun coroutineDebugProbes(): Boolean? =
      syspropOrEnv(KotestEngineProperties.COROUTINE_DEBUG_PROBES)?.let { it.uppercase() == "TRUE" }
}
