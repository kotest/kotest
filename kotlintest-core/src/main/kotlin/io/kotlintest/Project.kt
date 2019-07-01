@file:Suppress("ObjectPropertyName", "MemberVisibilityCanBePrivate")

package io.kotlintest

import io.kotlintest.extensions.*
import java.lang.StringBuilder

/**
 * Internal class used to hold project wide configuration.
 *
 * This class will attempt to locate a user implementation of
 * [AbstractProjectConfig] located at the package io.kotlintest.provided.ProjectConfig.
 *
 * If such an object exists, it will be instantiated and then
 * any extensions, such as [ProjectExtension], [SpecExtension] or
 * [TestCaseExtension]s will be registered with this class.
 *
 * In addition, extensions can be programatically added to this class
 * by invoking the `registerExtension` functions.
 */
object Project {

  init {
    System.getProperty("includeTags")?.apply {
      println("[WARN] The system property 'includeTags' has been detected. This no longer has any effect in KotlinTest. If you are setting this property for another library then you can ignore this message. Otherwise change the property to be kotlintest.tags.include")
    }
    System.getProperty("excludeTags")?.apply {
      println("[WARN] The system property 'excludeTags' has been detected. This no longer has any effect in KotlinTest. If you are setting this property for another library then you can ignore this message. Otherwise change the property to be kotlintest.tags.exclude")
    }
  }

  private const val defaultProjectConfigFullyQualifiedName = "io.kotlintest.provided.ProjectConfig"

  private fun discoverProjectConfig(): AbstractProjectConfig? {
    return try {
      val projectConfigFullyQualifiedName = System.getProperty("kotlintest.project.config")
          ?: defaultProjectConfigFullyQualifiedName
      val clas = Class.forName(projectConfigFullyQualifiedName)
      val field = clas.declaredFields.find { it.name == "INSTANCE" }
      when (field) {
        // if the static field for an object cannot be found, then instantiate
        null -> clas.newInstance() as AbstractProjectConfig
        // if the static field can be found then use it
        else -> field.get(null) as AbstractProjectConfig
      }
    } catch (cnf: ClassNotFoundException) {
      null
    }
  }

  private val _extensions: MutableList<ProjectLevelExtension> = mutableListOf(SystemPropertyTagExtension, RuntimeTagExtension)
  private val _listeners = mutableListOf<TestListener>()
  private val _filters = mutableListOf<ProjectLevelFilter>()
  private var _specExecutionOrder: SpecExecutionOrder = LexicographicSpecExecutionOrder
  private var writeSpecFailureFile: Boolean = true
  private var _globalAssertSoftly: Boolean = false
  private var parallelism: Int = 1

  fun discoveryExtensions(): List<DiscoveryExtension> = _extensions.filterIsInstance<DiscoveryExtension>()
  fun constructorExtensions(): List<ConstructorExtension> = _extensions.filterIsInstance<ConstructorExtension>()
  private fun projectExtensions(): List<ProjectExtension> = _extensions.filterIsInstance<ProjectExtension>()
  fun specExtensions(): List<SpecExtension> = _extensions.filterIsInstance<SpecExtension>()
  fun testCaseExtensions(): List<TestCaseExtension> = _extensions.filterIsInstance<TestCaseExtension>()
  fun tagExtensions(): List<TagExtension> = _extensions.filterIsInstance<TagExtension>()

  fun listeners(): List<TestListener> = _listeners
  fun testCaseFilters(): List<TestCaseFilter> = _filters.filterIsInstance<TestCaseFilter>()

  fun globalAssertSoftly(): Boolean = _globalAssertSoftly
  fun parallelism() = parallelism

  var failOnIgnoredTests: Boolean = false

  fun tags(): Tags {
    val tags = tagExtensions().map { it.tags() }
    return if (tags.isEmpty()) Tags.Empty else tags.reduce { a, b -> a.combine(b) }
  }

  private var projectConfig: AbstractProjectConfig? = discoverProjectConfig()?.also {
    _extensions.addAll(it.extensions())
    _listeners.addAll(it.listeners())
    _filters.addAll(it.filters())
    _specExecutionOrder = it.specExecutionOrder()
    _globalAssertSoftly = System.getProperty("kotlintest.assertions.global-assert-softly") == "true" || it.globalAssertSoftly
    parallelism = System.getProperty("kotlintest.parallelism")?.toInt() ?: it.parallelism()
    writeSpecFailureFile = System.getProperty("kotlintest.write.specfailures") == "true" || it.writeSpecFailureFile()
    failOnIgnoredTests = System.getProperty("kotlintest.build.fail-on-ignore") == "true" || it.failOnIgnoredTests
  }

  fun writeSpecFailureFile(): Boolean = writeSpecFailureFile
  fun specExecutionOrder(): SpecExecutionOrder = _specExecutionOrder

  fun beforeAll() {
    printConfigs()
    projectExtensions().forEach { extension -> extension.beforeAll() }
    projectConfig?.beforeAll()
    listeners().forEach { it.beforeProject() }
  }

  fun afterAll() {
    listeners().forEach { it.afterProject() }
    projectConfig?.afterAll()
    projectExtensions().reversed().forEach { extension -> extension.afterAll() }
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

  fun testCaseOrder(): TestCaseOrder = projectConfig?.testCaseOrder() ?: TestCaseOrder.Sequential
  fun isolationMode(): IsolationMode? = projectConfig?.isolationMode()

  private fun printConfigs() {
    println("~~~ Discovered this project configurations ~~~")
    buildOutput("Parallelism", parallelism.plurals("%d thread", "%d threads"))
    buildOutput("Test order", _specExecutionOrder::class.java.simpleName)
    buildOutput("Soft assertations", _globalAssertSoftly.toString().capitalize())
    buildOutput("Write spec failure file", writeSpecFailureFile.toString().capitalize())
    buildOutput("Fail on ignored tests", failOnIgnoredTests.toString().capitalize())

    buildOutput("Extensions")
    _extensions.map(::mapClassName).forEach {
      buildOutput(it, indentation = 1)
    }

    buildOutput("Listeners")
    _listeners.map(::mapClassName).forEach {
      buildOutput(it, indentation = 1)
    }

    buildOutput("Filters")
    _filters.map(::mapClassName).forEach {
      buildOutput(it, indentation = 1)
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

  private fun Int.plurals(singular: String, plural: String, zero: String = plural) = if (this == 0)
    zero.format(this)
  else if (this in listOf(-1, 1))
    singular.format(this)
  else
    plural.format(this)

  private fun mapClassName(any: Any) =
      any::class.java.canonicalName ?: any::class.java.name

}
