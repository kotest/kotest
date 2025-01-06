package io.kotest.engine.config

import io.kotest.core.Tag
import io.kotest.core.config.AbstractPackageConfig
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.asProjectExtension
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

//internal class TestConfigResolverBuilder {
//
//   fun builder() {
//
//   }
//
//   fun build(): TestConfigResolver {
//      return TestConfigResolver(
//
//      )
//   }
//}

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
   private val projectConfig: AbstractProjectConfig?,
) {

   private val disabledByEnabledIf = Enabled.disabled("Disabled by enabledIf flag in config")
   private val disabledByTestConfig = Enabled.disabled("Disabled by enabled flag in config")
   private val disabledByXMethod = Enabled.disabled("Disabled by xmethod")

   private val systemPropertyConfiguration = loadSystemPropertyConfiguration()

   fun retries(testCase: TestCase): Int? {
      return testConfigs(testCase).firstNotNullOfOrNull { it.retries }
         ?: testCase.spec.retries
         ?: testCase.spec.defaultTestConfig?.retries
         ?: packageConfigs(testCase.spec).firstNotNullOfOrNull { it.retries }
         ?: projectConfig?.retries
         ?: Defaults.defaultRetries
   }

   fun retryDelay(testCase: TestCase): Duration? {
      return testConfigs(testCase).firstNotNullOfOrNull { it.retryDelay }
         ?: testCase.spec.retryDelay
         ?: testCase.spec.defaultTestConfig?.retryDelay
         ?: projectConfig?.retryDelay
   }

   fun failfast(testCase: TestCase): Boolean {
      return testConfigs(testCase).firstNotNullOfOrNull { it.failfast }
         ?: testCase.spec.failfast
         ?: testCase.spec.defaultTestConfig?.failfast
         ?: packageConfigs(testCase.spec).firstNotNullOfOrNull { it.failfast }
         ?: projectConfig?.projectWideFailFast
         ?: Defaults.FAILFAST
   }

   fun assertSoftly(testCase: TestCase): Boolean {
      return testConfigs(testCase).firstNotNullOfOrNull { it.assertSoftly }
         ?: testCase.spec.assertSoftly
         ?: testCase.spec.defaultTestConfig?.assertSoftly
         ?: packageConfigs(testCase.spec).firstNotNullOfOrNull { it.assertSoftly }
         ?: projectConfig?.globalAssertSoftly
         ?: systemPropertyConfiguration.globalAssertSoftly()
         ?: Defaults.GLOBAL_ASSERT_SOFTLY
   }

   fun severity(testCase: TestCase): TestCaseSeverityLevel {
      return testConfigs(testCase).firstNotNullOfOrNull { it.severity }
         ?: testCase.spec.severity
         ?: testCase.spec.defaultTestConfig?.severity
         ?: projectConfig?.severity
         ?: Defaults.TEST_CASE_SEVERITY_LEVEL
   }

   fun assertionMode(testCase: TestCase): AssertionMode {
      return testConfigs(testCase).firstNotNullOfOrNull { it.assertionMode }
         ?: testCase.spec.assertionMode()
         ?: testCase.spec.defaultTestConfig?.assertionMode
         ?: packageConfigs(testCase.spec).firstNotNullOfOrNull { it.assertionMode }
         ?: projectConfig?.assertionMode
         ?: systemPropertyConfiguration.assertionMode()
         ?: Defaults.ASSERTION_MODE
   }

   fun coroutineDebugProbes(testCase: TestCase): Boolean {
      return testConfigs(testCase).firstNotNullOfOrNull { it.coroutineDebugProbes }
         ?: testCase.spec.coroutineDebugProbes
         ?: testCase.spec.defaultTestConfig?.coroutineDebugProbes
         ?: packageConfigs(testCase.spec).firstNotNullOfOrNull { it.coroutineDebugProbes }
         ?: projectConfig?.coroutineDebugProbes
         ?: systemPropertyConfiguration.coroutineDebugProbes()
         ?: Defaults.COROUTINE_DEBUG_PROBES
   }

   fun coroutineTestScope(testCase: TestCase): Boolean {
      return testConfigs(testCase).firstNotNullOfOrNull { it.coroutineTestScope }
         ?: testCase.spec.coroutineTestScope
         ?: testCase.spec.defaultTestConfig?.coroutineTestScope
         ?: packageConfigs(testCase.spec).firstNotNullOfOrNull { it.coroutineTestScope }
         ?: projectConfig?.coroutineTestScope
         ?: Defaults.COROUTINE_TEST_SCOPE
   }

   fun blockingTest(testCase: TestCase): Boolean {
      return testConfigs(testCase).firstNotNullOfOrNull { it.blockingTest }
         ?: testCase.spec.blockingTest
         ?: testCase.spec.defaultTestConfig?.blockingTest
         ?: Defaults.BLOCKING_TEST
   }

   fun extensions(testCase: TestCase): List<Extension> {
      return testConfigs(testCase).flatMap { it.extensions ?: emptyList() } +
         testCase.spec.extensions() +
         (projectConfig?.extensions() ?: emptyList()) +
         listOfNotNull(projectConfig?.asProjectExtension())
   }

   fun timeout(testCase: TestCase): Duration {
      return testConfigs(testCase).firstNotNullOfOrNull { it.timeout }
         ?: testCase.spec.timeout?.milliseconds
         ?: testCase.spec.timeout()?.milliseconds
         ?: testCase.spec.defaultTestConfig?.timeout
         ?: packageConfigs(testCase.spec).firstNotNullOfOrNull { it.timeout }
         ?: projectConfig?.timeout
         ?: Defaults.DEFAULT_TIMEOUT_MILLIS.milliseconds
   }

   fun invocationTimeout(testCase: TestCase): Duration {
      return testConfigs(testCase).firstNotNullOfOrNull { it.invocationTimeout }
         ?: testCase.spec.invocationTimeout?.milliseconds
         ?: testCase.spec.invocationTimeout()?.milliseconds
         ?: testCase.spec.defaultTestConfig?.invocationTimeout
         ?: packageConfigs(testCase.spec).firstNotNullOfOrNull { it.invocationTimeout }
         ?: projectConfig?.invocationTimeout
         ?: Defaults.DEFAULT_INVOCATION_TIMEOUT_MILLIS.milliseconds
   }

   fun invocations(testCase: TestCase): Int {
      return testConfigs(testCase).firstNotNullOfOrNull { it.invocations }
         ?: testCase.spec.defaultTestConfig?.invocations
         ?: Defaults.INVOCATIONS
   }

   fun tags(testCase: TestCase): Set<Tag> {
      return testConfigs(testCase).flatMap { it.tags }.toSet() +
         testCase.spec.tags() +
         testCase.spec.appliedTags() +
         (testCase.spec.defaultTestConfig?.tags ?: emptySet()) +
         testCase.spec::class.tags(projectConfig?.tagInheritance == true)
   }

   fun enabled(testCase: TestCase): EnabledOrReasonIf {
      val disabledByTestConfig = testConfigs(testCase).any { it.enabled == false }
      val testEnabledIf = testConfigs(testCase).firstNotNullOfOrNull { it.enabledIf }
      val testEnabledOrReasonIf = testConfigs(testCase).firstNotNullOfOrNull { it.enabledOrReasonIf }
      val specEnabledIf = testCase.spec.defaultTestConfig?.enabledIf
      val specEnabledOrReasonIf = testCase.spec.defaultTestConfig?.enabledOrReasonIf
      val projectEnabledOrReasonIf = projectConfig?.enabledOrReasonIf
      return { testCase ->
         when {
            // if xdisabled we always override any other enabled/disabled flags
            xdisabled -> disabledByXMethod
            disabledByTestConfig == false -> this@TestConfigResolver.disabledByTestConfig
            testEnabledIf != null -> if (testEnabledIf(testCase)) Enabled.Companion.enabled else disabledByEnabledIf
            testEnabledOrReasonIf != null -> testEnabledOrReasonIf.invoke(testCase)
            specEnabledIf != null -> if (specEnabledIf(testCase)) Enabled.Companion.enabled else disabledByEnabledIf
            specEnabledOrReasonIf != null -> specEnabledOrReasonIf.invoke(testCase)
            projectEnabledOrReasonIf != null -> projectEnabledOrReasonIf.invoke(testCase)
            else -> Enabled.Companion.enabled
         }
      }
   }

   /**
    * Returns the [TestConfig]s for each [TestCase] in the hierarchy, with the most specific first,
    * including this test itself, with the root test last.
    */
   private fun testConfigs(testCase: TestCase): List<TestConfig> {
      val parent = testCase.parent
      val config = listOfNotNull(testCase.config)
      return if (parent == null) config else config + testConfigs(parent)
   }

   private fun packageConfigs(spec: Spec): List<AbstractPackageConfig> {
      return PackageConfigLoader.configs(spec)
   }
}
