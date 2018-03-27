package io.kotlintest

import io.kotlintest.extensions.ProjectExtension
import io.kotlintest.extensions.SpecInterceptor
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.extensions.TestListener

/**
 * Internal class used to hold project wide configuration.
 *
 * This class will attempt to locate a user implementation of
 * [AbstractProjectConfig] located at the package io.kotlintest.provided.ProjectConfig.
 *
 * If such an object exists, it will be instantiated and then
 * any extensions, such as [ProjectExtension], [SpecInterceptor] or
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

  internal val projectExtensions = mutableListOf<ProjectExtension>()
  internal val specInterceptors = mutableListOf<SpecInterceptor>()
  internal val testCaseInterceptors = mutableListOf<TestCaseExtension>()
  internal val listeners = mutableListOf<TestListener>()
  internal var parallelism: Int = 1

  fun projectInterceptors() = projectExtensions.toList()
  fun specInterceptors() = specInterceptors.toList()
  fun testCaseInterceptors() = testCaseInterceptors.toList()
  fun listeners() = listeners.toList()
  fun parallelism() = parallelism

  private var projectConfig: AbstractProjectConfig? = discoverProjectConfig()?.apply {
    projectExtensions.addAll(this.extensions())
    specInterceptors.addAll(this.specExtensions())
    listeners.addAll(this.listeners())
    parallelism = System.getProperty("kotlintest.parallelism")?.toInt() ?: this.parallelism()
  }

  fun beforeAll() {
    projectExtensions.forEach { extension -> extension.beforeAll() }
    projectConfig?.beforeAll()
  }

  fun afterAll() {
    projectConfig?.afterAll()
    projectExtensions.reversed().forEach { extension -> extension.afterAll() }
  }

  fun registerListener(listener: TestListener) {
    this.listeners.add(listener)
  }

  fun registerExtension(projectExtension: ProjectExtension) {
    this.projectExtensions.add(projectExtension)
  }

  fun registerExtension(testCaseExtension: TestCaseExtension) {
    this.testCaseInterceptors.add(testCaseExtension)
  }

  fun registerExtension(specExtensions: SpecInterceptor) {
    this.specInterceptors.add(specExtensions)
  }

  fun registerExtensions(vararg projectExtensions: ProjectExtension) {
    this.projectExtensions.addAll(projectExtensions)
  }

  fun registerExtensions(vararg testCaseExtensions: TestCaseExtension) {
    this.testCaseInterceptors.addAll(testCaseExtensions)
  }

  fun registerExtensions(vararg specExtensions: SpecInterceptor) {
    this.specInterceptors.addAll(specExtensions)
  }
}