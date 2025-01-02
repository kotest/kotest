package io.kotest.engine.test

import io.kotest.core.Tag
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.Spec
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.Enabled
import io.kotest.core.test.EnabledOrReasonIf
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.config.DefaultTestConfig
import io.kotest.core.test.config.ResolvedTestConfig
import io.kotest.core.test.config.TestConfig
import io.kotest.engine.tags.tags
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Merges the possible combinations of test configs to create the final definitive [ResolvedTestConfig]
 * which is used by a [TestCase] at runtime.
 *
 * Order of precedence for each possible test setting from highest priority to lowest:
 *
 * - individual test level settings set on the test definition itself
 * - spec level individual test settings
 * - spec level defaults from setting [Spec.defaultTestConfig]
 * - project level defaults from individual settings in [ProjectConfiguration]
 * - kotest defaults
 */
internal class TestConfigResolver(private val projectConf: ProjectConfiguration) {

   private val disabledByEnabledIf = Enabled.disabled("Disabled by enabledIf flag in config")
   private val disabledByEnabled = Enabled.disabled("Disabled by enabled flag in config")
   private val disabledByXMethod = Enabled.disabled("Disabled by xmethod")

   fun resolve(
     testConfig: TestConfig?,
     xdisabled: Boolean?,
     parent: TestCase?,
     spec: Spec,
   ): ResolvedTestConfig {
      return ResolvedTestConfig(
         enabled = enabledIf(xdisabled == true, testConfig, spec),
         invocations = invocations(testConfig, spec.defaultTestConfig),
         timeout = timeout(testConfig, parent, spec),
         invocationTimeout = invocationTimeout(testConfig, parent, spec),
         tags = tags(testConfig, parent, spec),
         extensions = extensions(testConfig, parent),
         failfast = failfast(testConfig, parent, spec),
         severity = severity(testConfig, parent, spec),
         assertSoftly = assertSoftly(testConfig, parent, spec),
         assertionMode = assertionMode(testConfig, parent, spec),
         coroutineDebugProbes = coroutineDebugProbes(testConfig, parent, spec),
         coroutineTestScope = coroutineTestScope(testConfig, parent, spec),
         blockingTest = blockingTest(testConfig, parent, spec),
         retries = retries(testConfig, parent, spec),
         retryFn = retryFn(spec),
         retryDelay = retryDelay(testConfig, parent, spec),
         retryDelayFn = retryDelayFn(spec),
      )
   }

   private fun retries(testConfig: TestConfig?, parent: TestCase?, spec: Spec): Int? {
      return testConfig?.retries ?: parent?.config?.retries ?: spec.retries ?: spec.defaultTestConfig?.retries
      ?: projectConf.retries
   }

   private fun retryDelay(testConfig: TestConfig?, parent: TestCase?, spec: Spec): Duration? {
      return testConfig?.retryDelay ?: parent?.config?.retryDelay ?: spec.retryDelay ?: spec.defaultTestConfig?.retryDelay
      ?: projectConf.retryDelay
   }

   private fun retryFn(spec: Spec): ((TestCase) -> Int)? {
      return spec.defaultTestConfig?.retryFn ?: projectConf.retryFn
   }

   private fun retryDelayFn(spec: Spec): ((TestCase, Int) -> Duration)? {
      return spec.defaultTestConfig?.retryDelayFn ?: projectConf.retryDelayFn
   }

   private fun failfast(testConfig: TestConfig?, parent: TestCase?, spec: Spec): Boolean {
      return testConfig?.failfast ?: parent?.config?.failfast ?: spec.failfast ?: spec.defaultTestConfig?.failfast
      ?: projectConf.failfast
   }

   fun assertSoftly(testConfig: TestConfig?, parent: TestCase?, spec: Spec): Boolean {
      return testConfig?.assertSoftly ?: parent?.config?.assertSoftly ?: spec.assertSoftly
      ?: projectConf.globalAssertSoftly
   }

   private fun severity(testConfig: TestConfig?, parent: TestCase?, spec: Spec): TestCaseSeverityLevel {
      return testConfig?.severity ?: parent?.config?.severity ?: spec.severity ?: projectConf.severity
   }

   private fun assertionMode(testConfig: TestConfig?, parent: TestCase?, spec: Spec): AssertionMode {
      return testConfig?.assertionMode ?: parent?.config?.assertionMode ?: spec.assertions ?: spec.assertionMode()
      ?: projectConf.assertionMode
   }

   private fun coroutineDebugProbes(testConfig: TestConfig?, parent: TestCase?, spec: Spec): Boolean {
      return testConfig?.coroutineDebugProbes
         ?: parent?.config?.coroutineDebugProbes
         ?: spec.coroutineDebugProbes
         ?: spec.defaultTestConfig?.coroutineDebugProbes
         ?: projectConf.coroutineDebugProbes
   }

   fun coroutineTestScope(testConfig: TestConfig?, parent: TestCase?, spec: Spec): Boolean {
      return testConfig?.coroutineTestScope
         ?: parent?.config?.coroutineTestScope
         ?: spec.coroutineTestScope
         ?: spec.defaultTestConfig?.coroutineTestScope
         ?: projectConf.coroutineTestScope
   }

   fun blockingTest(testConfig: TestConfig?, parent: TestCase?, spec: Spec): Boolean {
      return testConfig?.blockingTest
         ?: parent?.config?.blockingTest
         ?: spec.blockingTest
         ?: spec.defaultTestConfig?.blockingTest
         ?: projectConf.blockingTest
   }

   fun extensions(testConfig: TestConfig?, parent: TestCase?): List<Extension> {
      return (testConfig?.extensions ?: emptyList()) +
         (parent?.config?.extensions ?: emptyList())
   }

   fun timeout(testConfig: TestConfig?, parent: TestCase?, spec: Spec): Duration {
      return testConfig?.timeout
         ?: parent?.config?.timeout
         ?: spec.timeout?.milliseconds
         ?: spec.timeout()?.milliseconds
         ?: spec.defaultTestConfig?.timeout
         ?: projectConf.timeout.milliseconds
   }

   fun invocationTimeout(testConfig: TestConfig?, parent: TestCase?, spec: Spec): Duration {
      return testConfig?.invocationTimeout
         ?: parent?.config?.invocationTimeout
         ?: spec.invocationTimeout?.milliseconds
         ?: spec.invocationTimeout()?.milliseconds
         ?: spec.defaultTestConfig?.invocationTimeout
         ?: projectConf.invocationTimeout.milliseconds
   }

   fun invocations(testConfig: TestConfig?, specTestConfig: DefaultTestConfig?): Int {
      return testConfig?.invocations
         ?: specTestConfig?.invocations
         ?: projectConf.invocations
   }

   fun tags(testConfig: TestConfig?, parent: TestCase?, spec: Spec): Set<Tag> {
      return (testConfig?.tags ?: emptySet()) +
         (parent?.config?.tags ?: emptySet()) +
         spec.tags() +
         spec.appliedTags() +
         (spec.defaultTestConfig?.tags ?: emptySet()) +
         spec::class.tags(projectConf.tagInheritance)
   }

   private fun enabledIf(xdisabled: Boolean, testConfig: TestConfig?, spec: Spec): EnabledOrReasonIf {
      val testEnabledIf = testConfig?.enabledIf
      val testEnabledOrReasonIf = testConfig?.enabledOrReasonIf
      val specEnabledIf = spec.defaultTestConfig?.enabledIf
      val specEnabledOrReasonIf = spec.defaultTestConfig?.enabledOrReasonIf
      val projectEnabledIf = projectConf.enabledIf
      val projectEnabledOrReasonIf = projectConf.enabledOrReasonIf
      return { testCase ->
         when {
            // if xdisabled we always override any other enabled/disabled flags
            xdisabled -> disabledByXMethod
            testConfig?.enabled == false -> disabledByEnabled
            testEnabledIf != null -> if (testEnabledIf(testCase)) Enabled.enabled else disabledByEnabledIf
            testEnabledOrReasonIf != null -> testEnabledOrReasonIf.invoke(testCase)
            specEnabledIf != null -> if (specEnabledIf(testCase)) Enabled.enabled else disabledByEnabledIf
            specEnabledOrReasonIf != null -> specEnabledOrReasonIf.invoke(testCase)
            projectEnabledIf != null -> if (projectEnabledIf(testCase)) Enabled.enabled else disabledByEnabledIf
            projectEnabledOrReasonIf != null -> projectEnabledOrReasonIf.invoke(testCase)
            else -> Enabled.enabled
         }
      }
   }
}
