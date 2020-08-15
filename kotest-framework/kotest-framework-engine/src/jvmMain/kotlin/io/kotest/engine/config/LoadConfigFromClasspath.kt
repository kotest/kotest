package io.kotest.engine.config

import io.github.classgraph.ScanResult
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.ProjectListener
import io.kotest.fp.orElse
import io.kotest.fp.toOption
import io.kotest.mpp.instantiate
import kotlin.time.ExperimentalTime

/**
 * Returns a [DetectedProjectConfig] which is built scanning the classpath for
 * instances of [AbstractProjectConfig].
 *
 * If multiple instances are detected, they are merged together.
 */
fun loadConfigFromAbstractProjectConfig(scanResult: ScanResult): DetectedProjectConfig {
   return scanResult
      .getSubclasses(AbstractProjectConfig::class.java.name)
      .map { Class.forName(it.name) as Class<out AbstractProjectConfig> }
      .mapNotNull { instantiate(it).getOrNull() }
      .map { it.toDetectedConfig() }
      .reduce { a, b -> a.merge(b) }
}

@OptIn(ExperimentalTime::class)
fun AbstractProjectConfig.toDetectedConfig(): DetectedProjectConfig {

   val beforeAfterAllListener = object : ProjectListener {
      override suspend fun beforeProject() {
         this@toDetectedConfig.beforeAll()
      }

      override suspend fun afterProject() {
         this@toDetectedConfig.afterAll()
      }
   }

   return DetectedProjectConfig(
      extensions = extensions(),
      listeners = listeners() + listOf(beforeAfterAllListener),
      filters = filters(),
      isolationMode = isolationMode.toOption().orElse(isolationMode().toOption()),
      assertionMode = assertionMode.toOption(),
      testCaseOrder = testCaseOrder ?: testCaseOrder(),
      specExecutionOrder = specExecutionOrder ?: specExecutionOrder(),
      failOnIgnoredTests = failOnIgnoredTests,
      globalAssertSoftly = globalAssertSoftly,
      autoScanEnabled = autoScanEnabled ?: true,
      autoScanIgnoredClasses = autoScanIgnoredClasses,
      writeSpecFailureFile = writeSpecFailureFile ?: writeSpecFailureFile(),
      parallelism = parallelism.toOption().orElse(parallelism().toOption()),
      timeout = timeout.toOption().map { it.toLongMilliseconds() },
      invocationTimeout = invocationTimeout.toOption(),
      testCaseConfig = defaultTestCaseConfig,
      includeTestScopeAffixes = includeTestScopePrefixes,
      testNameCase = testNameCase
   )
}
