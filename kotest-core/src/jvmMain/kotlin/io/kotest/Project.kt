@file:Suppress("ObjectPropertyName", "MemberVisibilityCanBePrivate")

package io.kotest

import io.kotest.core.TestCaseFilter
import io.kotest.extensions.ConstructorExtension
import io.kotest.extensions.DiscoveryExtension
import io.kotest.extensions.ProjectLevelExtension
import io.kotest.extensions.ProjectLevelFilter
import io.kotest.extensions.ProjectListener
import io.kotest.extensions.RuntimeTagExtension
import io.kotest.extensions.SpecExtension
import io.kotest.extensions.SystemPropertyTagExtension
import io.kotest.extensions.TagExtension
import io.kotest.extensions.TestCaseExtension
import io.kotest.extensions.TestListener
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

/**
 * Internal class used to hold project wide configuration.
 *
 * This class will attempt to locate a user implementation of
 * [AbstractProjectConfig] located at the package io.kotest.provided.ProjectConfig.
 *
 * If such an object exists, it will be instantiated and then
 * any extensions, such as [SpecExtension] or [TestCaseExtension]
 * will be registered with this class.
 *
 * In addition, extensions can be programatically added to this class
 * by invoking the `registerExtension` functions.
 */
object Project {

  init {
    System.getProperty("includeTags")?.apply {
      println("[WARN] The system property 'includeTags' has been detected. This no longer has any effect in Kotest. If you are setting this property for another library then you can ignore this message. Otherwise change the property to be kotest.tags.include")
    }
    System.getProperty("excludeTags")?.apply {
      println("[WARN] The system property 'excludeTags' has been detected. This no longer has any effect in Kotest. If you are setting this property for another library then you can ignore this message. Otherwise change the property to be kotest.tags.exclude")
    }
  }

   private const val defaultProjectConfigFullyQualifiedName = "io.kotest.provided.ProjectConfig"
   @UseExperimental(ExperimentalTime::class)
   private val defaultTimeout = 600.seconds
   private val defaultAssertionMode = AssertionMode.None

   private fun discoverProjectConfig(): AbstractProjectConfig? {
      return try {
         val projectConfigFullyQualifiedName = System.getProperty("kotest.project.config")
            ?: defaultProjectConfigFullyQualifiedName
         val clas = Class.forName(projectConfigFullyQualifiedName)
         when (val field = clas.declaredFields.find { it.name == "INSTANCE" }) {
            // if the static field for an object cannot be found, then instantiate
            null -> clas.newInstance() as AbstractProjectConfig
            // if the static field can be found then use it
            else -> field.get(null) as AbstractProjectConfig
         }
      } catch (cnf: ClassNotFoundException) {
         null
      }
  }

   private val _extensions: MutableList<ProjectLevelExtension> =
      mutableListOf(SystemPropertyTagExtension, RuntimeTagExtension)
   private val _listeners = mutableListOf<TestListener>()
   private val _projectlisteners = mutableListOf<ProjectListener>()
   private val _filters = mutableListOf<ProjectLevelFilter>()
   private var _specExecutionOrder: SpecExecutionOrder = LexicographicSpecExecutionOrder
   private var writeSpecFailureFile: Boolean = false
   private var _globalAssertSoftly: Boolean = false
   private var parallelism: Int = 1
   @UseExperimental(ExperimentalTime::class)
   private var _timeout: Duration? = null
   var failOnIgnoredTests: Boolean = false
   private var _assertionMode: AssertionMode? = null

   fun discoveryExtensions(): List<DiscoveryExtension> = _extensions.filterIsInstance<DiscoveryExtension>()
   fun constructorExtensions(): List<ConstructorExtension> = _extensions.filterIsInstance<ConstructorExtension>()
   private fun projectListeners(): List<ProjectListener> = _projectlisteners
   fun specExtensions(): List<SpecExtension> = _extensions.filterIsInstance<SpecExtension>()
   fun testCaseExtensions(): List<TestCaseExtension> = _extensions.filterIsInstance<TestCaseExtension>()
   fun tagExtensions(): List<TagExtension> = _extensions.filterIsInstance<TagExtension>()

   fun listeners(): List<TestListener> = _listeners
   fun testCaseFilters(): List<TestCaseFilter> = _filters.filterIsInstance<TestCaseFilter>()

   fun globalAssertSoftly(): Boolean = _globalAssertSoftly
   fun parallelism() = parallelism

   @UseExperimental(ExperimentalTime::class)
   fun timeout(): Duration = _timeout ?: defaultTimeout

   fun tags(): Tags {
      val tags = tagExtensions().map { it.tags() }
      return if (tags.isEmpty()) Tags.Empty else tags.reduce { a, b -> a.combine(b) }
   }

   private var projectConfig: AbstractProjectConfig? = discoverProjectConfig()?.also {
      _extensions.addAll(it.extensions())
      _listeners.addAll(it.listeners())
      _projectlisteners.addAll(it.projectListeners())
      _filters.addAll(it.filters())
      _specExecutionOrder = it.specExecutionOrder()
      _globalAssertSoftly = System.getProperty("kotest.assertions.global-assert-softly") == "true" || it.globalAssertSoftly
      _timeout = it.timeout
      parallelism = System.getProperty("kotest.parallelism")?.toInt() ?: it.parallelism()
      writeSpecFailureFile = System.getProperty("kotest.write.specfailures") == "true" || it.writeSpecFailureFile()
      failOnIgnoredTests = System.getProperty("kotest.build.fail-on-ignore") == "true" || it.failOnIgnoredTests
      _assertionMode = it.assertionMode
   }

   fun writeSpecFailureFile(): Boolean = writeSpecFailureFile
   fun specExecutionOrder(): SpecExecutionOrder = _specExecutionOrder

  fun beforeAll() {
    printConfigs()
    projectListeners().forEach { it.beforeProject() }
    projectConfig?.beforeAll()
    listeners().forEach { it.beforeProject() }
  }

  fun afterAll() {
    listeners().forEach { it.afterProject() }
    projectConfig?.afterAll()
    projectListeners().forEach { it.afterProject() }
  }

  fun registerTestCaseFilter(filters: List<TestCaseFilter>) = _filters.addAll(filters)

  fun registerListeners(vararg listeners: TestListener) = listeners.forEach { registerListener(it) }
  private fun registerListener(listener: TestListener) {
    _listeners.add(listener)
  }

  fun registerExtensions(vararg extensions: ProjectLevelExtension) = extensions.forEach { registerExtension(it) }
  fun registerExtension(extension: ProjectLevelExtension) {
    _extensions.add(extension)
  }

  fun deregisterExtension(extension: ProjectLevelExtension) {
    _extensions.remove(extension)
  }

   fun assertionMode(): AssertionMode = _assertionMode ?: defaultAssertionMode
   fun testCaseOrder(): TestCaseOrder = projectConfig?.testCaseOrder() ?: TestCaseOrder.Sequential
   fun isolationMode(): IsolationMode? = projectConfig?.isolationMode()

  private fun printConfigs() {
    println("~~~ Project Configuration ~~~")
    buildOutput("Parallelism", parallelism.plurals("%d thread", "%d threads"))
    buildOutput("Test order", _specExecutionOrder::class.java.simpleName)
    buildOutput("Soft assertations", _globalAssertSoftly.toString().capitalize())
    buildOutput("Write spec failure file", writeSpecFailureFile.toString().capitalize())
    buildOutput("Fail on ignored tests", failOnIgnoredTests.toString().capitalize())

    if (_extensions.isNotEmpty()) {
      buildOutput("Extensions")
      _extensions.map(::mapClassName).forEach {
        buildOutput(it, indentation = 1)
      }
    }

    if (_listeners.isNotEmpty()) {
      buildOutput("Listeners")
      _listeners.map(::mapClassName).forEach {
        buildOutput(it, indentation = 1)
      }
    }

    if (_filters.isNotEmpty()) {
      buildOutput("Filters")
      _filters.map(::mapClassName).forEach {
        buildOutput(it, indentation = 1)
      }
    }
  }

  private fun buildOutput(key: String, value: String? = null, indentation: Int = 0) {
    StringBuilder().apply {
      if (indentation == 0) {
        append("-> ")
      } else {
        for (i in 0 until indentation) {
          append("  ")
        }
        append("- ")
      }
      append(key)
      value?.let { append(": $it") }
    }.also { println(it.toString()) }
  }

  private fun Int.plurals(singular: String, plural: String, zero: String = plural) = when {
    this == 0 -> zero.format(this)
    this in listOf(-1, 1) -> singular.format(this)
    else -> plural.format(this)
  }

  private fun mapClassName(any: Any) =
      any::class.java.canonicalName ?: any::class.java.name

}
