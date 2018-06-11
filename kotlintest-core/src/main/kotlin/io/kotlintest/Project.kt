package io.kotlintest

import io.kotlintest.extensions.DiscoveryExtension
import io.kotlintest.extensions.ProjectExtension
import io.kotlintest.extensions.ProjectLevelExtension
import io.kotlintest.extensions.SpecExtension
import io.kotlintest.extensions.SystemPropertyTagExtension
import io.kotlintest.extensions.TagExtension
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.extensions.TestListener

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

  private const val projectConfigFullyQualifiedName = "io.kotlintest.provided.ProjectConfig"

  private fun discoverProjectConfig(): AbstractProjectConfig? {
    return try {
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

  private val _extensions = mutableListOf<ProjectLevelExtension>().apply { add(SystemPropertyTagExtension) }
  private val _listeners = mutableListOf<TestListener>()
  private val _filters = mutableListOf<ProjectLevelFilter>()
  private var parallelism: Int = 1

  fun discoveryExtensions(): List<DiscoveryExtension> = _extensions.filterIsInstance<DiscoveryExtension>()
  private fun projectExtensions(): List<ProjectExtension> = _extensions.filterIsInstance<ProjectExtension>()
  fun specExtensions(): List<SpecExtension> = _extensions.filterIsInstance<SpecExtension>()
  fun testCaseExtensions(): List<TestCaseExtension> = _extensions.filterIsInstance<TestCaseExtension>()
  fun tagExtensions(): List<TagExtension> = _extensions.filterIsInstance<TagExtension>()

  fun listeners(): List<TestListener> = _listeners
  fun testCaseFilters(): List<TestCaseFilter> = _filters.filterIsInstance<TestCaseFilter>()

  fun parallelism() = parallelism

  fun tags(): Tags {
    val tags = tagExtensions().map { it.tags() }
    return if (tags.isEmpty()) Tags.Empty else tags.reduce { a, b -> a.combine(b) }
  }

  private var projectConfig: AbstractProjectConfig? = discoverProjectConfig()?.apply {
    _extensions.addAll(this.extensions())
    _listeners.addAll(this.listeners())
    _filters.addAll(this.filters())
    parallelism = System.getProperty("kotlintest.parallelism")?.toInt() ?: this.parallelism()
  }

  fun beforeAll() {
    projectExtensions().forEach { extension -> extension.beforeAll() }
    projectConfig?.beforeAll()
    listeners().forEach { it.beforeProject() }
  }

  fun afterAll() {
    listeners().forEach { it.afterProject() }
    projectConfig?.afterAll()
    projectExtensions().reversed().forEach { extension -> extension.afterAll() }
  }

  fun registerListeners(vararg listeners: TestListener) = listeners.forEach { registerListener(it) }
  private fun registerListener(listener: TestListener) {
    _listeners.add(listener)
  }

  fun registerExtensions(vararg extensions: ProjectLevelExtension) = extensions.forEach { registerExtension(it) }
  fun registerExtension(extension: ProjectLevelExtension) {
    _extensions.add(extension)
  }

  fun testCaseOrder(): TestCaseOrder = projectConfig?.testCaseOrder() ?: TestCaseOrder.Sequential
}