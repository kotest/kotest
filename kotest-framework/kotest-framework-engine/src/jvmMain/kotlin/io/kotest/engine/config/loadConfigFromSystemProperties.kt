package io.kotest.engine.config

import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.AssertionMode
import io.kotest.engine.KotestEngineSystemProperties
import io.kotest.fp.Option
import io.kotest.fp.toOption
import io.kotest.mpp.sysprop

fun isolationMode(): Option<IsolationMode> =
   sysprop(KotestEngineSystemProperties.isolationMode).toOption().map { IsolationMode.valueOf(it) }

fun assertionMode(): Option<AssertionMode> =
   sysprop(KotestEngineSystemProperties.assertionMode).toOption().map { AssertionMode.valueOf(it) }

fun parallelism(): Option<Int> = sysprop(KotestEngineSystemProperties.parallelism).toOption().map { it.toInt() }

fun timeout(): Option<Long> = sysprop(KotestEngineSystemProperties.timeout).toOption().map { it.toLong() }

fun invocationTimeout(): Option<Long> =
   sysprop(KotestEngineSystemProperties.invocationTimeout).toOption().map { it.toLong() }

/**
 * Returns a [DetectedProjectConfig] which is built from system properties where specified.
 */
fun loadConfigFromSystemProperties(): DetectedProjectConfig {
   return DetectedProjectConfig(
      extensions = emptyList(),
      listeners = emptyList(),
      filters = emptyList(),
      isolationMode = isolationMode(),
      assertionMode = assertionMode(),
      testCaseOrder = null,
      specExecutionOrder = null,
      failOnIgnoredTests = null,
      globalAssertSoftly = null,
      autoScanEnabled = true,
      autoScanIgnoredClasses = emptyList(),
      writeSpecFailureFile = null,
      specFailureFilePath = null,
      parallelism = parallelism(),
      timeout = timeout(),
      invocationTimeout = invocationTimeout(),
      testCaseConfig = null,
      includeTestScopeAffixes = null,
      testNameCase = null
   )
}
