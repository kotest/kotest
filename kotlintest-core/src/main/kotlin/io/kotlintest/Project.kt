package io.kotlintest

/**
 * Internal class used to execute project wide before and after functions.
 *
 * This class will attempt to locate a user implementation of
 * [AbstractProjectConfig] located at the package io.kotlintest.provided.ProjectConfig.
 *
 * If such a class exists, it will be instantiated and then
 * any [ProjectExtension]s registered will be executed by this class.
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

  private var projectConfig = discoverProjectConfig()

  fun beforeAll() {
    projectConfig?.extensions?.forEach { extension -> extension.beforeAll() }
    projectConfig?.beforeAll()
  }

  fun afterAll() {
    projectConfig?.afterAll()
    projectConfig?.extensions?.reversed()?.forEach { extension -> extension.afterAll() }
  }
}