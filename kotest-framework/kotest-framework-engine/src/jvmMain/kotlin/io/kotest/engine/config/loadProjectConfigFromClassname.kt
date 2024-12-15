package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.log
import io.kotest.engine.KotestEngineProperties
import io.kotest.engine.instantiateOrObject
import org.jetbrains.annotations.ApiStatus.Internal

internal actual fun loadProjectConfigsFromClassname(): List<AbstractProjectConfig> = loadProjectConfigsJVM()

private const val defaultConfigFqn = "io.kotest.provided.ProjectConfig"

@Internal
fun loadProjectConfigsJVM(): List<AbstractProjectConfig> {
   return when (val fqns = System.getProperty(KotestEngineProperties.configurationClassNames)) {

      null -> {
         log { "No project config class name provided, checking for default at $defaultConfigFqn" }
         val clazz = runCatching { Class.forName(defaultConfigFqn) }.getOrNull()
         if (clazz == null) emptyList() else listOf(instantiateOrObject(clazz).getOrThrow() as AbstractProjectConfig)
      }

      else -> {
         log { "Loading project configs from fqn(s): $fqns" }
         fqns.split(";").map {
            instantiateOrObject(Class.forName(it)).getOrThrow() as AbstractProjectConfig
         }
      }
   }
}
