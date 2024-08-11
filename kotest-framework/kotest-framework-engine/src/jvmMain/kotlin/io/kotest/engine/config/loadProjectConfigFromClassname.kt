package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.log
import io.kotest.engine.instantiateOrObject
import org.jetbrains.annotations.ApiStatus.Internal

internal actual fun loadProjectConfigFromClassname(): AbstractProjectConfig? = loadProjectConfigFromClassnameJVM()

private const val defaultConfigFqn = "io.kotest.provided.ProjectConfig"

@Internal
fun loadProjectConfigFromClassnameJVM(): AbstractProjectConfig? {
   return when (val fqn = System.getProperty(KotestEngineProperties.configurationClassName)) {

      null -> {
         log { "No project config class name provided, checking for default at $defaultConfigFqn" }
         val clazz = runCatching { Class.forName(defaultConfigFqn) }.getOrNull()
         if (clazz == null) null else instantiateOrObject(clazz).getOrThrow() as AbstractProjectConfig
      }

      else -> {
         log { "Loading project config from fqn: $fqn" }
         instantiateOrObject(Class.forName(fqn)).getOrThrow() as AbstractProjectConfig
      }
   }
}
