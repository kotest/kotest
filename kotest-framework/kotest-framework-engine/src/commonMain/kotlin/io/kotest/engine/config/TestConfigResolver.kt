package io.kotest.engine.config

import io.kotest.core.Tag
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.Spec
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.Enabled
import io.kotest.core.test.EnabledOrReasonIf
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.config.TestConfig
import io.kotest.engine.tags.tags
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * A [TestConfigResolver] is responsible for returning the runtime value to use for a given
 * configuration setting based on the various sources of configuration at the test case level.
 *
 * This class handles settings that can be set at the test level, such as retry count, or timeouts.
 *
 * Order of precedence for each possible setting from highest priority to lowest:
 *
 * - individual test level settings set on the test definition itself
 * - spec level individual test settings
 * - spec level defaults from setting [io.kotest.core.TestConfiguration.defaultTestConfig]
 * - package level defaults from [io.kotest.core.config.AbstractPackageConfig]
 * - project level defaults from [io.kotest.core.config.AbstractProjectConfig]
 * - system property overrides
 * - kotest defaults
 */
internal class TestConfigResolver(
   private val projectConfig: AbstractProjectConfig,
   private val systemPropertyConfiguration: SystemPropertyConfiguration,
) {

   private val disabledByEnabledIf = Enabled.Companion.disabled("Disabled by enabledIf flag in config")
   private val disabledByEnabled = Enabled.Companion.disabled("Disabled by enabled flag in config")
   private val disabledByXMethod = Enabled.Companion.disabled("Disabled by xmethod")

   fun retries(testCase: TestCase): Int? {
      return testConfigs(testCase).firstNotNullOfOrNull { it.retries }
         ?: testCase.spec.retries
         ?: testCase.spec.defaultTestConfig?.retries
         ?: projectConfig.retries
         ?: Defaults.defaultRetries
   }

   fun retryDelay(testCase: TestCase): Duration? {
      return testConfigs(testCase).firstNotNullOfOrNull { it.retryDelay }
         ?: testCase.spec.retryDelay
         ?: testCase.spec.defaultTestConfig?.retryDelay
         ?: projectConfig.retryDelay
   }

   private fun failfast(testConfig: TestConfig?, parent: TestCase?, spec: Spec): Boolean {
      return testConfig?.failfast
         ?: parent?.config?.failfast
         ?: spec.failfast
         ?: spec.defaultTestConfig?.failfast
         ?: projectConfig.projectWideFailFast
         ?: Defaults.FAILFAST
   }

   fun assertSoftly(testConfig: TestConfig?, parent: TestCase?, spec: Spec): Boolean {
      return testConfig?.assertSoftly ?: parent?.config?.assertSoftly ?: spec.assertSoftly
      ?: projectConfig.globalAssertSoftly
   }

   fun severity(testCase: TestCase): TestCaseSeverityLevel {
      return testConfigs(testCase).firstNotNullOfOrNull { it.severity }
         ?: testCase.spec.severity
         ?: testCase.spec.defaultTestConfig?.severity
         ?: projectConfig.severity
         ?: Defaults.TEST_CASE_SEVERITY_LEVEL
   }

   private fun assertionMode(testConfig: TestConfig?, parent: TestCase?, spec: Spec): AssertionMode {
      return testConfig?.assertionMode
         ?: parent?.config?.assertionMode
         ?: spec.assertions
         ?: spec.assertionMode()
         ?: projectConfig.assertionMode
   }

   private fun coroutineDebugProbes(testConfig: TestConfig?, parent: TestCase?, spec: Spec): Boolean {
      return testConfig?.coroutineDebugProbes
         ?: parent?.config?.coroutineDebugProbes
         ?: spec.coroutineDebugProbes
         ?: spec.defaultTestConfig?.coroutineDebugProbes
         ?: projectConfig.coroutineDebugProbes
         ?: Defaults.COROUTINE_DEBUG_PROBES
   }

   fun coroutineTestScope(testConfig: TestConfig?, parent: TestCase?, spec: Spec): Boolean {
      return testConfig?.coroutineTestScope
         ?: parent?.config?.coroutineTestScope
         ?: spec.coroutineTestScope
         ?: spec.defaultTestConfig?.coroutineTestScope
         ?: projectConfig.coroutineTestScope
   }

   fun blockingTest(testConfig: TestConfig?, parent: TestCase?, spec: Spec): Boolean {
      return testConfig?.blockingTest
         ?: parent?.config?.blockingTest
         ?: spec.blockingTest
         ?: spec.defaultTestConfig?.blockingTest
         ?: projectConfig.blockingTest
   }

   fun extensions(testConfig: TestConfig?, parent: TestCase?): List<Extension> {
      return (testConfig?.extensions ?: emptyList()) +
         (parent?.config?.extensions ?: emptyList())
   }

   fun timeout(testCase: TestCase): Duration {
      return testConfigs(testCase).firstNotNullOfOrNull { it.timeout }
         ?: testCase.spec.timeout?.milliseconds
         ?: testCase.spec.timeout()?.milliseconds
         ?: testCase.spec.defaultTestConfig?.timeout
         ?: projectConfig.timeout
         ?: Defaults.DEFAULT_TIMEOUT_MILLIS.milliseconds
   }

   fun invocationTimeout(testCase: TestCase): Duration {
      return testConfigs(testCase).firstNotNullOfOrNull { it.invocationTimeout }
         ?: testCase.spec.invocationTimeout?.milliseconds
         ?: testCase.spec.invocationTimeout()?.milliseconds
         ?: testCase.spec.defaultTestConfig?.invocationTimeout
         ?: projectConfig.invocationTimeout
         ?: Defaults.DEFAULT_INVOCATION_TIMEOUT_MILLIS.milliseconds
   }

   fun invocations(testCase: TestCase): Int {
      return testConfigs(testCase).firstNotNullOfOrNull { it.invocations }
         ?: testCase.spec.defaultTestConfig?.invocations
         ?: projectConfig.invocations
   }

   fun tags(testConfig: TestConfig?, parent: TestCase?, spec: Spec): Set<Tag> {
      return (testConfig?.tags ?: emptySet()) +
         (parent?.config?.tags ?: emptySet()) +
         spec.tags() +
         spec.appliedTags() +
         (spec.defaultTestConfig?.tags ?: emptySet()) +
         spec::class.tags(projectConfig.tagInheritance)
   }

   private fun enabledIf(xdisabled: Boolean, testConfig: TestConfig?, spec: Spec): EnabledOrReasonIf {
      val testEnabledIf = testConfig?.enabledIf
      val testEnabledOrReasonIf = testConfig?.enabledOrReasonIf
      val specEnabledIf = spec.defaultTestConfig?.enabledIf
      val specEnabledOrReasonIf = spec.defaultTestConfig?.enabledOrReasonIf
      val projectEnabledIf = projectConfig.enabledIf
      val projectEnabledOrReasonIf = projectConfig.enabledOrReasonIf
      return { testCase ->
         when {
            // if xdisabled we always override any other enabled/disabled flags
            xdisabled -> disabledByXMethod
            testConfig?.enabled == false -> disabledByEnabled
            testEnabledIf != null -> if (testEnabledIf(testCase)) Enabled.Companion.enabled else disabledByEnabledIf
            testEnabledOrReasonIf != null -> testEnabledOrReasonIf.invoke(testCase)
            specEnabledIf != null -> if (specEnabledIf(testCase)) Enabled.Companion.enabled else disabledByEnabledIf
            specEnabledOrReasonIf != null -> specEnabledOrReasonIf.invoke(testCase)
            projectEnabledIf != null -> if (projectEnabledIf(testCase)) Enabled.Companion.enabled else disabledByEnabledIf
            projectEnabledOrReasonIf != null -> projectEnabledOrReasonIf.invoke(testCase)
            else -> Enabled.Companion.enabled
         }
      }
   }

   /**
    * Returns the [TestConfig]s for each test in the hierarchy, with most specific first,
    * including this test itself.
    */
   private fun testConfigs(testCase: TestCase): List<TestConfig> {
      val parent = testCase.parent
      val config = listOfNotNull(testCase.config)
      return if (parent == null) config else config + testConfigs(parent)
   }
}
