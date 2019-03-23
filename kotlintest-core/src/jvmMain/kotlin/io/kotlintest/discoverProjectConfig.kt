package io.kotlintest

import io.kotlintest.internal.systemProperty

const val defaultProjectConfigFullyQualifiedName = "io.kotlintest.provided.ProjectConfig"

actual fun discoverProjectConfig(): AbstractProjectConfig? {
  return try {
    val projectConfigFullyQualifiedName = systemProperty("kotlintest.project.config")
        ?: defaultProjectConfigFullyQualifiedName
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