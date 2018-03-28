package io.kotlintest

import io.kotlintest.extensions.DiscoveryExtension
import io.kotlintest.extensions.Extension
import io.kotlintest.extensions.ProjectExtension
import io.kotlintest.extensions.SpecExtension
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

  private val _extensions = mutableListOf<Extension>()
  private val _listeners = mutableListOf<TestListener>()
  private var parallelism: Int = 1

  fun discoveryExtensions(): List<DiscoveryExtension> = _extensions.filterIsInstance<DiscoveryExtension>()
  fun projectExtensions(): List<ProjectExtension> = _extensions.filterIsInstance<ProjectExtension>()
  fun specExtensions(): List<SpecExtension> = _extensions.filterIsInstance<SpecExtension>()
  fun testCaseExtensions(): List<TestCaseExtension> = _extensions.filterIsInstance<TestCaseExtension>()
  fun listeners(): List<TestListener> = _listeners

  fun parallelism() = parallelism

  private var projectConfig: AbstractProjectConfig? = discoverProjectConfig()?.apply {
    _extensions.addAll(this.extensions())
    _listeners.addAll(this.listeners())
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

  fun registerListener(listener: TestListener) {
    _listeners.add(listener)
  }

  fun registerListeners(vararg listeners: TestListener) {
    _listeners.addAll(listeners)
  }

  fun registerExtension(extension: Extension) {
    _extensions.add(extension)
  }

  fun registerExtensions(vararg extensions: Extension) {
    _extensions.addAll(extensions)
  }
}