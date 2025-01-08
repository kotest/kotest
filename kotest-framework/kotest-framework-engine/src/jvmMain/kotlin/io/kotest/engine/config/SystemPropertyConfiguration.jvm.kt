package io.kotest.engine.config

import io.kotest.core.config.LogLevel
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.mpp.sysprop
import io.kotest.mpp.syspropOrEnv
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal actual fun loadSystemPropertyConfiguration(): SystemPropertyConfiguration =
   JvmSystemPropertyConfiguration

/**
 * Returns config values from system properties.
 */
internal object JvmSystemPropertyConfiguration : SystemPropertyConfiguration {

   override fun isolationMode(): IsolationMode? =
      sysprop(KotestEngineProperties.isolationMode)?.let { IsolationMode.valueOf(it) }

   override fun assertionMode(): AssertionMode? =
      sysprop(KotestEngineProperties.assertionMode)?.let { AssertionMode.valueOf(it) }

   override fun timeout(): Long? =
      sysprop(KotestEngineProperties.TIMEOUT)?.toLong()

   override fun invocationTimeout(): Long? =
      sysprop(KotestEngineProperties.invocationTimeout)?.toLong()

   override fun allowMultilineTestName(): Boolean? =
      sysprop(KotestEngineProperties.allowMultilineTestName)?.let { it.uppercase() == "TRUE" }

   override fun discoveryClasspathScanningEnabled(): Boolean? =
      sysprop(KotestEngineProperties.discoveryClasspathFallbackEnabled)?.toBoolean()

   override fun disableTestNestedJarScanning(): Boolean? =
      sysprop(KotestEngineProperties.disableTestNestedJarScanning)?.toBoolean()

   override fun globalAssertSoftly(): Boolean? =
      sysprop(KotestEngineProperties.globalAssertSoftly)?.let { it.uppercase() == "TRUE" }

   override fun testNameAppendTags(): Boolean? =
      sysprop(KotestEngineProperties.testNameAppendTags)?.let { it.uppercase() == "TRUE" }

   override fun tagInheritance(): Boolean? =
      syspropOrEnv(KotestEngineProperties.tagInheritance)?.let { it.uppercase() == "TRUE" }

   override fun ignorePrivateClasses(): Boolean? =
      syspropOrEnv(KotestEngineProperties.ignorePrivateClasses)?.let { it.uppercase() == "TRUE" }

   override fun displayFullTestPath(): Boolean? =
      syspropOrEnv(KotestEngineProperties.displayFullTestPath)?.let { it.uppercase() == "TRUE" }

   override fun duplicateTestNameMode(): DuplicateTestNameMode? =
      sysprop(KotestEngineProperties.duplicateTestNameMode)?.let { DuplicateTestNameMode.valueOf(it) }

   override fun projectTimeout(): Duration? {
      val d = sysprop(KotestEngineProperties.projectTimeout)?.toLong() ?: return null
      return d.milliseconds
   }

   override fun logLevel(): LogLevel? {
      return sysprop(KotestEngineProperties.logLevel)?.let { LogLevel.from(it) }
   }

   override fun minimumRuntimeTestCaseSeverityLevel(): TestCaseSeverityLevel? {
      return sysprop(KotestEngineProperties.testSeverity)
         ?.let { TestCaseSeverityLevel.valueOf(it) }
   }

   override fun coroutineDebugProbes(): Boolean? =
      syspropOrEnv(KotestEngineProperties.coroutineDebugProbes)?.let { it.uppercase() == "TRUE" }
}
