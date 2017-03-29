package io.kotlintest

import org.reflections.Reflections
import java.util.concurrent.atomic.AtomicBoolean

object Project {

  private var projectConfig: ProjectConfig? = null
  private val executedBefore = AtomicBoolean(false)
  private val executedAfter = AtomicBoolean(false)

  init {
    val configClasses = Reflections().getSubTypesOf(ProjectConfig::class.java)
    if (configClasses.size > 1)  {
      val configClassNames = configClasses.map { config -> config.simpleName }
      throw InvalidConfigException("Multiple ProjectConfigs found: $configClassNames")
    }
    projectConfig = configClasses.firstOrNull()?.kotlin?.objectInstance
  }

  internal fun beforeAll() {
    synchronized(executedBefore) {
      if (executedBefore.compareAndSet(false, true)) {
        projectConfig?.extensions?.forEach { extension -> extension.beforeAll() }
        projectConfig?.beforeAll()

      }
    }
  }

  internal fun afterAll() {
    synchronized(executedAfter) {
      if (executedAfter.compareAndSet(false, true)) {
        projectConfig?.afterAll()
        projectConfig?.extensions?.reversed()?.forEach { extension -> extension.afterAll() }
      }
    }
  }
}