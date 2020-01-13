@file:Suppress("MemberVisibilityCanBePrivate")

package io.kotest.core.config

import io.kotest.core.Tags
import io.kotest.core.extensions.*
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.LexicographicSpecExecutionOrder
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCaseFilter
import io.kotest.core.test.TestCaseOrder
import io.kotest.extensions.*
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
   private var extensions = userconf.extensions + listOf(SystemPropertyTagExtension,
      RuntimeTagExtension
   )
   private var projectListeners = userconf.projectListeners
   private var testListeners = userconf.testListeners
   private var filters = userconf.filters
   private var timeout = userconf.timeout ?: defaultTimeout
   private var failOnIgnoredTests = userconf.failOnIgnoredTests ?: false
   private var specExecutionOrder = userconf.specExecutionOrder ?: LexicographicSpecExecutionOrder
   private var writeSpecFailureFile = userconf.writeSpecFailureFile ?: false
   private var globalAssertSoftly = userconf.globalAssertSoftly ?: false
   private var parallelism = userconf.parallelism ?: 1

   fun registerExtensions(vararg extensions: Extension) = extensions.forEach { registerExtension(it) }

   fun registerExtension(extension: Extension) {
      extensions = extensions + extension
   }

   fun deregisterExtension(extension: Extension) {
      extensions = extensions - extension
   }

   fun registerFilters(filters: Collection<ProjectLevelFilter>) = filters.forEach { registerFilter(it) }
   fun registerFilters(vararg filters: ProjectLevelFilter) = filters.forEach { registerFilter(it) }

   fun registerFilter(filter: ProjectLevelFilter) {
      filters = filters + filter
   }

   fun registerListeners(vararg listeners: TestListener) = listeners.forEach { registerListener(it) }

   fun registerListener(listener: TestListener) {
      testListeners = testListeners + listener
   }

   fun registerProjectListener(listener: ProjectListener) {
      projectListeners = projectListeners + listener
   }

   /**
    * Returns the registered [TagExtension]s.
    */
   fun tags(): Tags {
      val tags = extensions.filterIsInstance<TagExtension>().map { it.tags() }
      return if (tags.isEmpty()) Tags.Empty else tags.reduce { a, b -> a.combine(b) }
   }

   /**
    * Returns the registered [TestCaseFilter].
    */
   fun testCaseFilters(): List<TestCaseFilter> {
      return filters.filterIsInstance<TestCaseFilter>()
   }

   fun testCaseExtensions(): List<TestCaseExtension> = extensions.filterIsInstance<TestCaseExtension>()
   fun specExtensions(): List<SpecExtension> = extensions.filterIsInstance<SpecExtension>()

   /**
    * Returns the [SpecExecutionOrder] set by the user or defaults to [LexicographicSpecExecutionOrder].
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

   fun constructorExtensions(): List<ConstructorExtension> = extensions.filterIsInstance<ConstructorExtension>()
   fun discoveryExtensions(): List<DiscoveryExtension> = extensions.filterIsInstance<DiscoveryExtension>()

   fun extensions(): List<Extension> = extensions

   fun filters(): List<ProjectLevelFilter> = filters

   fun testListeners(): List<TestListener> = testListeners

   fun projectListeners(): List<ProjectListener> = projectListeners

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

   fun setGlobalAssertSoftly(g: Boolean) {
      globalAssertSoftly = g
   }

   fun beforeAll() {
      projectListeners().forEach { it.beforeProject() }
   }

   fun afterAll() {
      projectListeners().forEach { it.afterProject() }
   }
}

/**
 * Contains all the configuration details that can be set by a user supplied config object.
 */
@UseExperimental(ExperimentalTime::class)
data class ProjectConf constructor(
   val extensions: List<Extension> = emptyList(),
   val projectListeners: List<ProjectListener> = emptyList(),
   val testListeners: List<TestListener> = emptyList(),
   val filters: List<ProjectLevelFilter> = emptyList(),
   val isolationMode: IsolationMode? = null,
   val assertionMode: AssertionMode? = null,
   val testCaseOrder: TestCaseOrder? = null,
   val specExecutionOrder: SpecExecutionOrder? = null,
   val failOnIgnoredTests: Boolean? = null,
   val globalAssertSoftly: Boolean? = null,
   val writeSpecFailureFile: Boolean? = null,
   val parallelism: Int? = null,
   val timeout: Duration? = null
)

/**
 * Loads a config object from the underlying target.
 * For example, on the JVM it may scan the classpath.
 */
expect fun detectConfig(): ProjectConf
