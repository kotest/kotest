package io.kotest.engine.spec

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.Spec
import io.kotest.core.test.Enabled
import io.kotest.core.test.EnabledOrReasonIf
import io.kotest.core.test.TestCase
import io.kotest.core.test.config.ResolvedTestConfig
import io.kotest.core.test.config.TestConfig
import io.kotest.engine.tags.tags
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Accepts an [TestConfig] and returns a [ResolvedTestConfig] by completing
 * any nulls in the unresolved config with defaults from the [spec] or [ProjectConfiguration].
 */
internal fun resolveConfig(
   config: TestConfig?,
   xdisabled: Boolean?,
   parent: TestCase?,
   spec:Spec,
   projectConfiguration: ProjectConfiguration
): ResolvedTestConfig {

   @Suppress("DEPRECATION")  // remove when removing TestCaseConfig
   val defaultTestConfig = spec.defaultTestConfig
      ?: spec.defaultTestCaseConfig()
      ?: projectConfiguration.defaultTestConfig

   val enabled: EnabledOrReasonIf = { testCase ->
      when {
         // if xdisabled we always override any other enabled/disabled flags
         xdisabled == true -> Enabled.disabled("Disabled by xmethod")
         config?.enabled == false -> Enabled.disabled("Disabled by enabled flag in config")
         config?.enabledIf != null -> if (config.enabledIf!!.invoke(testCase)) Enabled.enabled else Enabled.disabled("Disabled by enabledIf flag in config")
         config?.enabledOrReasonIf != null -> config.enabledOrReasonIf!!.invoke(testCase)
         !defaultTestConfig.enabled -> Enabled.disabled
         !defaultTestConfig.enabledIf.invoke(testCase) -> Enabled.disabled
         else -> defaultTestConfig.enabledOrReasonIf.invoke(testCase)
      }
   }

   val timeout: Duration? = config?.timeout
      ?: parent?.config?.timeout
      ?: spec.timeout?.toMillis()
      ?: spec.timeout()?.toMillis()
      ?: defaultTestConfig.timeout
      ?: projectConfiguration.timeout?.toMillis()

   val threads: Int = config?.threads
      ?: spec.threads
      ?: spec.threads()
      ?: projectConfiguration.threads

   val invocations = config?.invocations
      ?: projectConfiguration.invocations

   val invocationTimeout: Duration? = config?.invocationTimeout
      ?: parent?.config?.invocationTimeout
      ?: spec.invocationTimeout?.toMillis()
      ?: spec.invocationTimeout()?.toMillis()
      ?: defaultTestConfig.invocationTimeout
      ?: projectConfiguration.invocationTimeout?.toMillis()

   val extensions = (config?.listeners ?: emptyList()) +
      (config?.extensions ?: emptyList()) +
      defaultTestConfig.extensions +
      defaultTestConfig.listeners

   @Suppress("DEPRECATION") // Remove when removing testCoroutineDispatcher
   return ResolvedTestConfig(
      enabled = enabled,
      threads = threads,
      invocations = invocations,
      timeout = timeout,
      invocationTimeout = invocationTimeout,
      tags = (config?.tags ?: emptySet()) + (defaultTestConfig.tags) + (parent?.config?.tags ?: emptySet()) + spec.tags() + spec.appliedTags() + spec::class.tags(projectConfiguration.tagInheritance),
      extensions = extensions,
      failfast = config?.failfast ?: parent?.config?.failfast ?: spec.failfast ?: projectConfiguration.failfast,
      severity = config?.severity ?: parent?.config?.severity ?: spec.severity ?: projectConfiguration.severity,
      assertSoftly = config?.assertSoftly ?: parent?.config?.assertSoftly ?:spec.assertSoftly ?: projectConfiguration.globalAssertSoftly,
      assertionMode = config?.assertionMode ?: parent?.config?.assertionMode ?: spec.assertions ?: spec.assertionMode() ?: projectConfiguration.assertionMode,
      coroutineDebugProbes = config?.coroutineDebugProbes ?: parent?.config?.coroutineDebugProbes ?:spec.coroutineDebugProbes ?: projectConfiguration.coroutineDebugProbes,
      testCoroutineDispatcher = config?.testCoroutineDispatcher ?: parent?.config?.testCoroutineDispatcher ?: spec.testCoroutineDispatcher ?: projectConfiguration.testCoroutineDispatcher,
      coroutineTestScope = config?.coroutineTestScope ?: parent?.config?.coroutineTestScope ?: spec.coroutineTestScope ?: projectConfiguration.coroutineTestScope,
      blockingTest = config?.blockingTest ?: parent?.config?.blockingTest ?: spec.blockingTest ?: projectConfiguration.blockingTest,
   )
}

private fun Long.toMillis(): Duration = this.milliseconds
