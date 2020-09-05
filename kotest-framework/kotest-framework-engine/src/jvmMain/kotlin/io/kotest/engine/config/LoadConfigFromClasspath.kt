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
internal fun loadConfigFromAbstractProjectConfig(scanResult: ScanResult): DetectedProjectConfig {
   val configs = scanResult
      .getSubclasses(AbstractProjectConfig::class.java.name)
      .map { Class.forName(it.name) as Class<out AbstractProjectConfig> }
      .mapNotNull { instantiate(it).getOrNull() }
      .map { it.toDetectedConfig() }
   return if (configs.isEmpty()) DetectedProjectConfig() else configs.reduce { a, b -> a.merge(b) }
}

@OptIn(ExperimentalTime::class)
private fun AbstractProjectConfig.toDetectedConfig(): DetectedProjectConfig {

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
      testCaseOrder = testCaseOrder.toOption().orElse(testCaseOrder().toOption()),
      specExecutionOrder = specExecutionOrder.toOption().orElse(specExecutionOrder().toOption()),
      failOnIgnoredTests = failOnIgnoredTests.toOption(),
      globalAssertSoftly = globalAssertSoftly.toOption(),
      autoScanEnabled = autoScanEnabled.toOption(),
      autoScanIgnoredClasses = autoScanIgnoredClasses,
      writeSpecFailureFile = writeSpecFailureFile.toOption().orElse(writeSpecFailureFile().toOption()),
      parallelism = parallelism.toOption().orElse(parallelism().toOption()),
      timeout = timeout.toOption().map { it.toLongMilliseconds() },
      invocationTimeout = invocationTimeout.toOption(),
      testCaseConfig = defaultTestCaseConfig.toOption(),
      includeTestScopeAffixes = includeTestScopePrefixes.toOption(),
      testNameCase = testNameCase.toOption()
   )
}
