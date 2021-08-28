package io.kotest.engine.config

import io.kotest.core.config.Configuration
import io.kotest.core.config.LogLevel
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.DuplicateTestNameMode
import io.kotest.fp.fmap
import io.kotest.fp.foreach
import io.kotest.mpp.sysprop

/**
 * Uses system properties to load configuration values onto the supplied [Configuration] object.
 *
 * Note: This function will have no effect on non-JVM targets.
 */
actual fun applyConfigFromSystemProperties(configuration: Configuration) {
   isolationMode().foreach { configuration.isolationMode = it }
   assertionMode().foreach { configuration.assertionMode = it }
   parallelism().foreach { configuration.parallelism = it }
   concurrentTests().foreach { configuration.concurrentTests = it }
   concurrentSpecs().foreach { configuration.concurrentSpecs = it }
   timeout().foreach { configuration.timeout = it }
   invocationTimeout().foreach { configuration.invocationTimeout = it }
   allowMultilineTestName().foreach { configuration.removeTestNameWhitespace = it }
   globalAssertSoftly().foreach { configuration.globalAssertSoftly = it }
   testNameAppendTags().foreach { configuration.testNameAppendTags = it }
   duplicateTestNameMode().foreach { configuration.duplicateTestNameMode = it }
   projectTimeout().foreach { configuration.projectTimeout = it }
   logLevel().foreach { configuration.logLevel = it }
}

internal fun isolationMode(): IsolationMode? =
   sysprop(KotestEngineProperties.isolationMode).fmap { IsolationMode.valueOf(it) }

internal fun assertionMode(): AssertionMode? =
   sysprop(KotestEngineProperties.assertionMode).fmap { AssertionMode.valueOf(it) }

internal fun parallelism(): Int? =
   sysprop(KotestEngineProperties.parallelism).fmap { it.toInt() }

internal fun timeout(): Long? =
   sysprop(KotestEngineProperties.timeout).fmap { it.toLong() }

internal fun invocationTimeout(): Long? =
   sysprop(KotestEngineProperties.invocationTimeout).fmap { it.toLong() }

internal fun allowMultilineTestName(): Boolean? =
   sysprop(KotestEngineProperties.allowMultilineTestName).fmap { it.uppercase() == "TRUE" }

internal fun concurrentSpecs(): Int? =
   sysprop(KotestEngineProperties.concurrentSpecs).fmap { it.toInt() }

internal fun concurrentTests(): Int? =
   sysprop(KotestEngineProperties.concurrentTests).fmap { it.toInt() }

internal fun globalAssertSoftly(): Boolean? =
   sysprop(KotestEngineProperties.globalAssertSoftly).fmap { it.uppercase() == "TRUE" }

internal fun testNameAppendTags(): Boolean? =
   sysprop(KotestEngineProperties.testNameAppendTags).fmap { it.uppercase() == "TRUE" }

internal fun duplicateTestNameMode(): DuplicateTestNameMode? =
   sysprop(KotestEngineProperties.duplicateTestNameMode).fmap { DuplicateTestNameMode.valueOf(it) }

internal fun projectTimeout(): Long? =
   sysprop(KotestEngineProperties.projectTimeout).fmap { it.toLong() }

internal fun logLevel(): LogLevel? =
   sysprop(KotestEngineProperties.logLevel).fmap { LogLevel.from(prop = it) }
