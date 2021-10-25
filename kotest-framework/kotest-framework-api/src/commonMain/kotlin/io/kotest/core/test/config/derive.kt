package io.kotest.core.test.config

import io.kotest.core.config.Configuration
import io.kotest.core.internal.tags.tags
import io.kotest.core.spec.Spec
import io.kotest.core.test.Enabled
import io.kotest.core.test.EnabledOrReasonIf
import kotlin.time.milliseconds

/**
 * Accepts an [UnresolvedTestConfig] and returns a resolved [ResolvedTestConfig] by completing
 * the unresolved with defaults from the [spec] or [Configuration].
 */
fun resolveConfig(
   config: UnresolvedTestConfig?,
   xdisabled: Boolean?,
   spec: Spec,
   configuration: Configuration
): ResolvedTestConfig {

   val defaultTestConfig = spec.defaultTestConfig ?: spec.defaultTestCaseConfig() ?: configuration.defaultTestConfig

   val enabled: EnabledOrReasonIf = { testCase ->
      when {
         xdisabled == false -> Enabled.disabled
         config?.enabled == false -> Enabled.disabled
         config?.enabledIf != null -> Enabled(config.enabledIf.invoke(testCase))
         config?.enabledOrReasonIf != null -> config.enabledOrReasonIf.invoke(testCase)
         !defaultTestConfig.enabled -> Enabled.disabled
         !defaultTestConfig.enabledIf.invoke(testCase) -> Enabled.disabled
         else -> defaultTestConfig.enabledOrReasonIf.invoke(testCase)
      }
   }

   val timeout = config?.timeout
      ?: spec.timeout?.milliseconds
      ?: spec.timeout()?.milliseconds
      ?: defaultTestConfig.timeout
      ?: configuration.timeout.milliseconds

   val threads = config?.threads
      ?: spec.threads
      ?: spec.threads()
      ?: defaultTestConfig.threads

   val invocations = config?.invocations
      ?: defaultTestConfig.invocations

   val invocationTimeout = config?.timeout
      ?: spec.invocationTimeout?.milliseconds
      ?: spec.invocationTimeout()?.milliseconds
      ?: defaultTestConfig.invocationTimeout
      ?: configuration.invocationTimeout.milliseconds

   val extensions = (config?.listeners ?: emptyList()) +
      (config?.extensions ?: emptyList()) +
      spec.extensions() +
      spec.registeredExtensions() +
      defaultTestConfig.extensions +
      defaultTestConfig.listeners

   return ResolvedTestConfig(
      enabled = enabled,
      threads = threads,
      invocations = invocations,
      timeout = timeout,
      invocationTimeout = invocationTimeout,
      tags = (config?.tags ?: emptySet()) + (defaultTestConfig.tags) + spec.tags() + spec._tags + spec::class.tags(),
      extensions = extensions,
      failfast = config?.failfast ?: spec.failfast ?: configuration.failfast,
      severity = config?.severity ?: spec.severity ?: configuration.severity,
      assertSoftly = config?.assertSoftly ?: spec.assertSoftly ?: configuration.globalAssertSoftly,
      assertionMode = config?.assertionMode ?: spec.assertions ?: spec.assertionMode() ?: configuration.assertionMode,
      coroutineDebugProbes = config?.coroutineDebugProbes ?: spec.coroutineDebugProbes ?: configuration.coroutineDebugProbes,
      testCoroutineDispatcher = config?.testCoroutineDispatcher ?: spec.testCoroutineDispatcher ?: configuration.testCoroutineDispatcher,
      blockingTest = config?.blockingTest ?: spec.blockingTest ?: configuration.blockingTest,
   )
}
