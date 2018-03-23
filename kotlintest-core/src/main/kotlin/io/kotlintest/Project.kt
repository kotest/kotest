package io.kotlintest

import io.kotlintest.extensions.ProjectExtension
import io.kotlintest.extensions.SpecExtension
import io.kotlintest.extensions.TestCaseExtension

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
  internal val specExtensions = mutableListOf<SpecExtension>()
  internal val testCaseExtensions = mutableListOf<TestCaseExtension>()
  internal var parallelism: Int = 1

  fun projectExtensions() = projectExtensions.toList()
  fun specExtensions() = specExtensions.toList()
  fun testCaseExtensions() = testCaseExtensions.toList()
  fun parallelism() = parallelism

  private var projectConfig: AbstractProjectConfig? = discoverProjectConfig()?.apply {
    projectExtensions.addAll(this.extensions())
    specExtensions.addAll(this.specExtensions())
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

  fun registerExtension(projectExtension: ProjectExtension) {
    this.projectExtensions.add(projectExtension)
  }

  fun registerExtension(testCaseExtension: TestCaseExtension) {
    this.testCaseExtensions.add(testCaseExtension)
  }

  fun registerExtension(specExtensions: SpecExtension) {
    this.specExtensions.add(specExtensions)
  }

  fun registerExtensions(vararg projectExtensions: ProjectExtension) {
    this.projectExtensions.addAll(projectExtensions)
  }

  fun registerExtensions(vararg testCaseExtensions: TestCaseExtension) {
    this.testCaseExtensions.addAll(testCaseExtensions)
  }

  fun registerExtensions(vararg specExtensions: SpecExtension) {
    this.specExtensions.addAll(specExtensions)
  }
}