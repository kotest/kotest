package io.kotest.core.test.config

fun deriveTestCaseConfig(config: ConfigurableTestConfig?, defaultTestCaseConfig: TestCaseConfig): TestCaseConfig {
   if (config == null) return defaultTestCaseConfig
   return TestCaseConfig(
      enabled = config.enabled ?: defaultTestCaseConfig.enabled,
      enabledOrReasonIf = config.enabledOrReasonIf ?: defaultTestCaseConfig.enabledOrReasonIf,
      enabledIf = config.enabledIf ?: defaultTestCaseConfig.enabledIf,
      threads = config.threads ?: defaultTestCaseConfig.threads,
      invocations = config.invocations ?: defaultTestCaseConfig.invocations,
      timeout = config.timeout ?: defaultTestCaseConfig.timeout,
      invocationTimeout = config.invocationTimeout ?: defaultTestCaseConfig.invocationTimeout,
      tags = (config.tags ?: emptySet()) + defaultTestCaseConfig.tags,
      listeners = (config.listeners ?: emptyList()) + defaultTestCaseConfig.listeners,
      extensions = (config.extensions ?: emptyList()) + defaultTestCaseConfig.extensions,
      failfast = config.failfast ?: defaultTestCaseConfig.failfast,
      severity = config.severity ?: defaultTestCaseConfig.severity,
      assertionMode = config.assertionMode ?: defaultTestCaseConfig.assertionMode,
      coroutineDebugProbes = config.coroutineDebugProbes ?: defaultTestCaseConfig.coroutineDebugProbes,
      testCoroutineDispatcher = config.testCoroutineDispatcher ?: defaultTestCaseConfig.testCoroutineDispatcher,
      blockingTest = config.blockingTest ?: defaultTestCaseConfig.blockingTest,
   )
}
