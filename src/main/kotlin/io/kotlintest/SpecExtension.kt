package io.kotlintest

import org.reflections.Reflections

interface SpecExtension {
  fun beforeAll(context: TestBase) {}
  fun beforeEach(context: TestCaseContext) {}
  fun afterEach(context: TestCaseContext) {}
  fun afterAll(context: TestBase) {}
}


data class TestCaseContext(
    val spec: TestBase,
    val testCase: TestCase,
    val failure: Throwable? = null)


interface ProjectExtension {
  fun beforeAll() {}
  fun afterAll() {}
}


abstract class ProjectConfig {
  open val extensions: List<ProjectExtension> = listOf()
}


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
        executedBefore = true
      }
    }

  }

  fun afterAll() {
    synchronized(executedAfter) {
      if (!executedAfter) {
        projectConfig?.extensions?.forEach { extension -> extension.afterAll() }
        executedAfter = false
      }
    }
  }
}


class InvalidConfigException(message: String) : Exception(message)