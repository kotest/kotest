package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.LogLevel
import io.kotest.core.config.asProjectExtension
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.SpecExecutionOrderExtension
import io.kotest.core.names.TestNameCase
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.engine.concurrency.ConcurrencyOrder
import io.kotest.engine.concurrency.SpecExecutionMode
import io.kotest.engine.extensions.EmptyExtensionRegistry
import io.kotest.engine.extensions.ExtensionRegistry
import kotlin.time.Duration

/**
 * The [ProjectConfigResolver] is responsible for returning the runtime value to use for a given
 * configuration setting based on the various sources of configuration.
 *
 * This class handles settings that should only be configured at the project level,
 * such as spec execution mode, or minimum test severity level.
 *
 * For spec level equivalent, see [SpecConfigResolver].
 *
 *  Order of precedence for each setting from highest priority to lowest:
 *
 * - project level defaults from [io.kotest.core.config.AbstractProjectConfig]
 * - system property overrides
 * - kotest defaults
 */
class ProjectConfigResolver(
   private val config: AbstractProjectConfig?,
   private val registry: ExtensionRegistry,
) {

   constructor() : this(null)
   constructor(config: AbstractProjectConfig?) : this(config, EmptyExtensionRegistry)

   private val systemPropertyConfiguration = loadSystemPropertyConfiguration()

   /**
    * Returns the minimum severity level for tests to be executed.
    */
   fun minimumRuntimeTestSeverityLevel(): TestCaseSeverityLevel? {
      return config?.minimumRuntimeTestCaseSeverityLevel
         ?: systemPropertyConfiguration?.minimumRuntimeTestCaseSeverityLevel()
   }

   fun logLevel(): LogLevel {
      return config?.logLevel
         ?: systemPropertyConfiguration?.logLevel()
         ?: Defaults.LOG_LEVEL
   }

   fun randomOrderSeed(): Long? {
      return config?.randomOrderSeed
   }

   fun tagInheritance(): Boolean {
      return config?.tagInheritance
         ?: systemPropertyConfiguration?.tagInheritance()
         ?: Defaults.TAG_INHERITANCE
   }

   /**
    * If returns false then private spec classes will be ignored by the test engine.
    * Defaults to false.
    */
   fun ignorePrivateClasses(): Boolean {
      return config?.ignorePrivateClasses
         ?: systemPropertyConfiguration?.ignorePrivateClasses()
         ?: Defaults.IGNORE_PRIVATE_CLASSES
   }

   /**
    * Returns true if the test style affixes should be included in the test name.
    * For example, some spec styles add prefixes or suffixes to the test name, and this
    * setting specifies if those should be included in the displayed test name.
    */
   fun includeTestScopeAffixes(testCase: TestCase): Boolean {
      val include = config?.includeTestScopeAffixes ?: Defaults.DEFAULT_INCLUDE_TEST_SCOPE_AFFIXES
      return when (include) {
         IncludeTestScopeAffixes.STYLE_DEFAULT -> testCase.name.defaultAffixes
         IncludeTestScopeAffixes.NEVER -> false
         IncludeTestScopeAffixes.ALWAYS -> true
      }
   }

   fun specExecutionMode(): SpecExecutionMode {
      return config?.specExecutionMode ?: Defaults.SPEC_EXECUTION_MODE
   }

   /**
    * Returns the [TestNameCase] to use when outputing test names.
    * This setting is only settable at the global level.
    */
   fun testNameCase(): TestNameCase {
      return config?.testNameCase ?: Defaults.TEST_NAME_CASE
   }

   /**
    * Set this to true to skip all remaining tests if any test fails.
    */
   fun projectWideFailFast(): Boolean {
      return config?.projectWideFailFast ?: Defaults.PROJECT_WIDE_FAIL_FAST
   }

   fun failOnEmptyTestSuite(): Boolean {
      return config?.failOnEmptyTestSuite ?: Defaults.FAIL_ON_EMPTY_TEST_SUITE
   }

   /**
    * If true, then the test execution will fail if any test is set to ignore.
    * If false, then ignored tests are outputted as normal.
    */
   fun failOnIgnoredTests(): Boolean {
      return config?.failOnIgnoredTests ?: Defaults.FAIL_ON_IGNORED_TESTS
   }

   /**
    * Returns true if tags specified on a test should be included in the test name output.
    */
   fun testNameAppendTags(): Boolean {
      return config?.testNameAppendTags ?: Defaults.TEST_NAME_APPEND_TAGS
   }

   /**
    * Returns the [SpecExecutionOrder] to use, unless overridden by registered [SpecExecutionOrderExtension]s.
    */
   fun specExecutionOrder(): SpecExecutionOrder {
      return config?.specExecutionOrder ?:
      systemPropertyConfiguration?.specExecutionOrder() ?:
      Defaults.SPEC_EXECUTION_ORDER
   }

   /**
    * Returns the [ConcurrencyOrder] to use.
    */
   fun concurrencyOrder(): ConcurrencyOrder {
      return config?.concurrencyOrder ?:
      systemPropertyConfiguration?.concurrencyOrder() ?:
      Defaults.CONCURRENCY_MODE_ORDER
   }

   fun specFailureFilePath(): String {
      return config?.specFailureFilePath ?: Defaults.SPEC_FAILURE_FILE_PATH
   }

   fun writeSpecFailureFile(): Boolean {
      return config?.writeSpecFailureFile ?: Defaults.WRITE_SPEC_FAILURE_FILE
   }

   /**
    * Returns true if the test name should be the full name including parent names.
    */
   fun displayFullTestPath(): Boolean {
      return config?.displayFullTestPath ?: Defaults.DISPLAY_FULL_TEST_PATH
   }

   fun allowOutOfOrderCallbacks(): Boolean {
      return config?.allowOutOfOrderCallbacks ?: Defaults.ALLOW_OUT_OF_ORDER_CALLBACKS
   }

   /**
    * Returns any extensions defined at the project level.
    *
    * That is extensions defined using the [AbstractProjectConfig.beforeProject] and
    * [AbstractProjectConfig.afterProject] functions as well as any extensions defined
    * by overriding the [AbstractProjectConfig.extensions] method.
    *
    * It also includes any extensions registered globally in the [ExtensionRegistry].
    */
   fun extensions(): List<Extension> {
      return (config?.extensions ?: emptyList()) +
         listOfNotNull(config?.asProjectExtension()) +
         registry.all()
   }

   inline fun <reified T : Extension> extensionsOf(): List<T> {
      return extensions().filterIsInstance<T>()
   }

   fun projectTimeout(): Duration? {
      return config?.projectTimeout
         ?: systemPropertyConfiguration?.projectTimeout()
   }

   fun dumpConfig(): Boolean {
      return config?.dumpConfig
         ?: systemPropertyConfiguration?.dumpConfig()
         ?: Defaults.DUMP_CONFIG
   }
}
