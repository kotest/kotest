@file:Suppress("MemberVisibilityCanBePrivate")

package io.kotest.core.config

import io.kotest.core.Tags
import io.kotest.core.extensions.*
import io.kotest.core.filters.Filter
import io.kotest.core.filters.TestCaseFilter
import io.kotest.core.listeners.Listener
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.LexicographicSpecExecutionOrder
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.*
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

/**
 * A central store of project wide configuration. This configuration contains defaults for kotest, and is
 * supplemented by user configuration (if present) as loaded by [detectConfig].
 *
 * Additionally config can be programatically added to this class by using the mutator methods such
 * as [registerExtension] or [setFailOnIgnoredTests].
 */
@UseExperimental(ExperimentalTime::class)
object Project {

   private val userconf = detectConfig()
   private val defaultTimeout = 600.seconds
   private var extensions = userconf.extensions + listOf(SystemPropertyTagExtension, RuntimeTagExtension)
   private var listeners = userconf.listeners
   private var filters = userconf.filters
   private var timeout = userconf.timeout ?: defaultTimeout
   private var failOnIgnoredTests = userconf.failOnIgnoredTests ?: false
   private var specExecutionOrder = userconf.specExecutionOrder ?: LexicographicSpecExecutionOrder
   private var writeSpecFailureFile = userconf.writeSpecFailureFile ?: false
   private var specFailureFilePath = userconf.specFailureFilePath ?: "./.kotest/spec_failures"
   private var globalAssertSoftly = userconf.globalAssertSoftly ?: false
   private var parallelism = userconf.parallelism ?: 1
   private var autoScanIgnoredClasses: List<KClass<*>> = emptyList()
   private var testCaseOrder: TestCaseOrder = userconf.testCaseOrder ?: TestCaseOrder.Sequential
   private var isolationMode: IsolationMode = userconf.isolationMode ?: IsolationMode.SingleInstance

   fun testCaseConfig() = userconf.testCaseConfig ?: TestCaseConfig()

   fun registerExtensions(vararg extensions: Extension) = extensions.forEach { registerExtension(it) }

   fun registerExtension(extension: Extension) {
      extensions = extensions + extension
   }

   fun deregisterExtension(extension: Extension) {
      extensions = extensions - extension
   }

   fun registerFilters(filters: Collection<Filter>) =
      filters.forEach { registerFilter(it) }

   fun registerFilter(filter: Filter) {
      filters = filters + filter
   }

   fun registerFilters(vararg filters: Filter) {
      registerFilters(filters.asList())
   }

   fun registerListeners(vararg listeners: Listener) = listeners.forEach { registerListener(it) }

   fun registerListener(listener: Listener) {
      listeners = listeners + listener
   }

   fun extensions() = extensions
      .filterNot { autoScanIgnoredClasses().contains(it::class) }

   fun listeners() = listeners
      .filterNot { autoScanIgnoredClasses().contains(it::class) }

   @Deprecated("Use registerListener(Listener)")
   fun registerProjectListener(listener: Listener) {
      registerListener(listener)
   }

   fun specFailureFilePath(): String = specFailureFilePath

   /**
    * Uses the registerd [TagExtension]s to evaluate the currently included/excluded [Tag]s.
    */
   fun tags(): Tags {
      val tags = tagExtensions().map { it.tags() }
      return if (tags.isEmpty()) Tags.Empty else tags.reduce { a, b -> a.combine(b) }
   }

   /**
    * Returns all registered [TestCaseFilter].
    */
   fun testCaseFilters(): List<TestCaseFilter> = filters
      .filterIsInstance<TestCaseFilter>()
      .filterNot { autoScanIgnoredClasses().contains(it::class) }

   fun specExtensions(): List<SpecExtension> = extensions
      .filterIsInstance<SpecExtension>()
      .filterNot { autoScanIgnoredClasses().contains(it::class) }

   /**
    * Returns the [SpecExecutionOrder] set by the user or defaults to [LexicographicSpecExecutionOrder].
    * Note: This has no effect on non-JVM targets.
    */
   fun specExecutionOrder(): SpecExecutionOrder {
      return specExecutionOrder
   }

   /**
    * Returns the default timeout for tests as specified in user config.
    * If not specified then defaults to [defaultTimeout]
    */
   fun timeout(): Duration = timeout

   fun setTimeout(duration: Duration) {
      this.timeout = duration
   }

   fun tagExtensions(): List<TagExtension> = extensions
      .filterIsInstance<TagExtension>()
      .filterNot { autoScanIgnoredClasses().contains(it::class) }

   fun constructorExtensions(): List<ConstructorExtension> = extensions
      .filterIsInstance<ConstructorExtension>()
      .filterNot { autoScanIgnoredClasses().contains(it::class) }

   fun discoveryExtensions(): List<DiscoveryExtension> = extensions
      .filterIsInstance<DiscoveryExtension>()
      .filterNot { autoScanIgnoredClasses().contains(it::class) }

   fun testCaseExtensions(): List<TestCaseExtension> = extensions
      .filterIsInstance<TestCaseExtension>()
      .filterNot { autoScanIgnoredClasses().contains(it::class) }

   fun testListeners(): List<TestListener> = listeners
      .filterIsInstance<TestListener>()
      .filterNot { autoScanIgnoredClasses().contains(it::class) }

   fun projectListeners(): List<ProjectListener> = listeners
      .filterIsInstance<ProjectListener>()
      .filterNot { autoScanIgnoredClasses().contains(it::class) }

   fun isolationMode() = isolationMode
   fun testCaseOrder() = testCaseOrder

   /**
    * Returns the number of concurrent specs that can be executed.
    * Defaults to 1.
    */
   fun parallelism(): Int = parallelism

   fun globalAssertSoftly(): Boolean = globalAssertSoftly

   fun writeSpecFailureFile(): Boolean = writeSpecFailureFile

   fun failOnIgnoredTests(): Boolean = failOnIgnoredTests

   fun setFailOnIgnoredTests(fail: Boolean) {
      failOnIgnoredTests = fail
   }

   fun autoScanIgnoredClasses() = autoScanIgnoredClasses

   fun setAutoScanIgnoredClasses(classes: List<KClass<*>>) {
      autoScanIgnoredClasses = classes
   }

   fun setGlobalAssertSoftly(g: Boolean) {
      globalAssertSoftly = g
   }
}

/**
 * Contains all the configuration details that can be set by a user supplied config object.
 */
@UseExperimental(ExperimentalTime::class)
data class ProjectConf constructor(
   val extensions: List<Extension> = emptyList(),
   val listeners: List<Listener> = emptyList(),
   val filters: List<Filter> = emptyList(),
   val isolationMode: IsolationMode? = null,
   val assertionMode: AssertionMode? = null,
   val testCaseOrder: TestCaseOrder? = null,
   val specExecutionOrder: SpecExecutionOrder? = null,
   val failOnIgnoredTests: Boolean? = null,
   val globalAssertSoftly: Boolean? = null,
   val autoScanEnabled: Boolean = true,
   val autoScanIgnoredClasses: List<KClass<*>> = emptyList(),
   val writeSpecFailureFile: Boolean? = null,
   val specFailureFilePath: String? = null,
   val parallelism: Int? = null,
   val timeout: Duration? = null,
   val testCaseConfig: TestCaseConfig? = null
)

/**
 * Loads a config object from the underlying target.
 * For example, on the JVM it may scan the classpath.
 */
expect fun detectConfig(): ProjectConf
