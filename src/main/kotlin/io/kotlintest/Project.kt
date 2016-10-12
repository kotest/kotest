package io.kotlintest

import org.reflections.Reflections

object Project {

  private var projectConfig: ProjectConfig? = null
  private var executedBefore = false
  private var executedAfter = false

  init {
    val configClasses = Reflections("io.kotlintest").getSubTypesOf(ProjectConfig::class.java)
    if (configClasses.size > 1)  {
      val configClassNames = configClasses.map { config -> config.typeName }
      throw InvalidConfigException("Duplicate GlobalConfig found: $configClassNames")
    }
    projectConfig = configClasses.firstOrNull()?.kotlin?.objectInstance
  }

  fun beforeAll() {
    synchronized(executedBefore) {
      if (!executedBefore) {
        projectConfig?.extensions?.forEach { extension -> extension.beforeAll() }
        projectConfig?.beforeAll()
        executedBefore = true
      }
    }
  }

  fun afterAll() {
    synchronized(executedAfter) {
      if (!executedAfter) {
        projectConfig?.afterAll()
        projectConfig?.extensions?.reversed()?.forEach { extension -> extension.afterAll() }
        executedAfter = true
      }
    }
  }
}