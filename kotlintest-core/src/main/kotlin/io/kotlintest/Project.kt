package io.kotlintest

object Project {

  private val projectConfigFullyQualifiedName = "io.kotlintest.provided.ProjectConfig"

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

  internal fun beforeAll() {
    projectConfig?.extensions?.forEach { extension -> extension.beforeAll() }
    projectConfig?.beforeAll()
  }

  internal fun afterAll() {
    projectConfig?.afterAll()
    projectConfig?.extensions?.reversed()?.forEach { extension -> extension.afterAll() }
  }
}