package io.kotlintest

object Project {

  private val ProjectConfigFQN = "io.kotlintest.provided.ProjectConfig"

  private fun discoverProjectConfig(): ProjectConfig? {
    return try {
      Class.forName(ProjectConfigFQN).newInstance() as ProjectConfig
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