package io.kotest.engine.config

import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.AssertionMode
import io.kotest.core.internal.KotestEngineSystemProperties
import io.kotest.fp.Option
import io.kotest.fp.toOption
import io.kotest.mpp.sysprop

internal fun isolationMode(): Option<IsolationMode> =
   sysprop(KotestEngineSystemProperties.isolationMode).toOption().map { IsolationMode.valueOf(it) }

internal fun assertionMode(): Option<AssertionMode> =
   sysprop(KotestEngineSystemProperties.assertionMode).toOption().map { AssertionMode.valueOf(it) }

internal fun parallelism(): Option<Int> =
   sysprop(KotestEngineSystemProperties.parallelism).toOption().map { it.toInt() }

internal fun timeout(): Option<Long> = sysprop(KotestEngineSystemProperties.timeout).toOption().map { it.toLong() }

internal fun invocationTimeout(): Option<Long> =
   sysprop(KotestEngineSystemProperties.invocationTimeout).toOption().map { it.toLong() }

internal fun allowMultilineTestName(): Option<Boolean> =
   sysprop(KotestEngineSystemProperties.allowMultilineTestName).toOption().map { it.toUpperCase() == "TRUE" }

internal fun concurrentSpecs(): Option<Int> =
   sysprop(KotestEngineSystemProperties.concurrentSpecs).toOption().map { it.toInt() }

internal fun concurrentTests(): Option<Int> =
   sysprop(KotestEngineSystemProperties.concurrentTests).toOption().map { it.toInt() }

internal fun globalAssertSoftly(): Option<Boolean> =
   sysprop(KotestEngineSystemProperties.globalAssertSoftly).toOption().map { it.toUpperCase() == "TRUE" }

internal fun testNameAppendTags(): Option<Boolean> =
   sysprop(KotestEngineSystemProperties.testNameAppendTags).toOption().map { it.toUpperCase() == "TRUE" }

/**
 * Returns a [DetectedProjectConfig] which is built from system properties where supported.
 */
internal fun loadConfigFromSystemProperties(): DetectedProjectConfig {
   return DetectedProjectConfig(
      isolationMode = isolationMode(),
      assertionMode = assertionMode(),
      parallelism = parallelism(),
      concurrentTests = concurrentTests(),
      concurrentSpecs = concurrentSpecs(),
      timeout = timeout(),
      invocationTimeout = invocationTimeout(),
      testNameRemoveWhitespace = allowMultilineTestName(),
      globalAssertSoftly = globalAssertSoftly(),
      testNameAppendTags = testNameAppendTags()
   )
}
