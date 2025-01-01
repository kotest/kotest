package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.log
import io.kotest.engine.KotestEngineProperties
import io.kotest.engine.instantiateOrObject
import org.jetbrains.annotations.ApiStatus.Internal
import kotlin.reflect.KClass

internal actual fun loadProjectConfigsFromClassname(): List<AbstractProjectConfig> = loadProjectConfigsJVM()

private const val defaultConfigFqn = "io.kotest.provided.ProjectConfig"

@Suppress("UNCHECKED_CAST")
@Internal
fun loadProjectConfigsJVM(): List<AbstractProjectConfig> {
   val fqns = fqns()
   log { "Loading project configs from fqn(s): $fqns" }
   return fqns.split(";")
      .mapNotNull { runCatching { Class.forName(it).kotlin }.getOrNull() }
      .map { instantiateOrObject(it as KClass<out AbstractProjectConfig>).getOrThrow() }
}

private fun fqns(): String {
   val fqns = System.getProperty(KotestEngineProperties.configurationClassNames)
   return if (fqns == null) {
      log { "No project config class name provided, checking for default at $defaultConfigFqn" }
      defaultConfigFqn
   } else fqns
}
