package io.kotest.engine.config

import io.kotest.core.config.LogLevel
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.AssertionMode
import io.kotest.engine.KotestEngineProperties
import io.kotest.mpp.sysprop
import io.kotest.mpp.syspropOrEnv
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Uses system properties to load configuration values onto the supplied [ProjectConfiguration] object.
 *
 * Note: This function will have no effect on non-JVM targets.
 */
internal actual fun applyConfigFromSystemProperties(configuration: ProjectConfiguration) {

   // before applying system props, we should detect the kotest.properties file and apply defaults from that
   KotestPropertiesLoader.loadAndApplySystemPropsFile()

   isolationMode()?.let { configuration.isolationMode = it }
   assertionMode()?.let { configuration.assertionMode = it }
   timeout()?.let { configuration.timeout = it }
   invocationTimeout()?.let { configuration.invocationTimeout = it }
   allowMultilineTestName()?.let { configuration.removeTestNameWhitespace = it }
   globalAssertSoftly()?.let { configuration.globalAssertSoftly = it }
   testNameAppendTags()?.let { configuration.testNameAppendTags = it }
   tagInheritance()?.let { configuration.tagInheritance = it }
   duplicateTestNameMode()?.let { configuration.duplicateTestNameMode = it }
   projectTimeout()?.let { configuration.projectTimeout = it }
   logLevel(configuration.logLevel).let { configuration.logLevel = it }
   discoveryClasspathScanningEnabled()?.let { configuration.discoveryClasspathFallbackEnabled = it }
   disableTestNestedJarScanning()?.let { configuration.disableTestNestedJarScanning = it }
}

internal fun isolationMode(): IsolationMode? =
   sysprop(KotestEngineProperties.isolationMode)?.let { IsolationMode.valueOf(it) }

internal fun assertionMode(): AssertionMode? =
   sysprop(KotestEngineProperties.assertionMode)?.let { AssertionMode.valueOf(it) }

internal fun timeout(): Long? =
   sysprop(KotestEngineProperties.TIMEOUT)?.toLong()

internal fun invocationTimeout(): Long? =
   sysprop(KotestEngineProperties.invocationTimeout)?.toLong()

internal fun allowMultilineTestName(): Boolean? =
   sysprop(KotestEngineProperties.allowMultilineTestName)?.let { it.uppercase() == "TRUE" }

internal fun discoveryClasspathScanningEnabled(): Boolean? =
   sysprop(KotestEngineProperties.discoveryClasspathFallbackEnabled)?.toBoolean()

internal fun disableTestNestedJarScanning(): Boolean? =
   sysprop(KotestEngineProperties.disableTestNestedJarScanning)?.toBoolean()

internal fun globalAssertSoftly(): Boolean? =
   sysprop(KotestEngineProperties.globalAssertSoftly)?.let { it.uppercase() == "TRUE" }

internal fun testNameAppendTags(): Boolean? =
   sysprop(KotestEngineProperties.testNameAppendTags)?.let { it.uppercase() == "TRUE" }

internal fun tagInheritance(): Boolean? =
   syspropOrEnv(KotestEngineProperties.tagInheritance)?.let { it.uppercase() == "TRUE" }

internal fun duplicateTestNameMode(): DuplicateTestNameMode? =
   sysprop(KotestEngineProperties.duplicateTestNameMode)?.let { DuplicateTestNameMode.valueOf(it) }

internal fun projectTimeout(): Duration? {
   val d = sysprop(KotestEngineProperties.projectTimeout)?.toLong() ?: return null
   return d.milliseconds
}


internal fun logLevel(fromConfiguration: LogLevel): LogLevel {
   val levelProp = syspropOrEnv(KotestEngineProperties.logLevel)?.let { LogLevel.from(it) }

   return levelProp ?: fromConfiguration
}
