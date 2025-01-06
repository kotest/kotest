package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.log
import io.kotest.engine.instantiateOrObject
import org.jetbrains.annotations.ApiStatus.Internal
import kotlin.reflect.KClass

internal actual fun loadProjectConfigFromClassname(): AbstractProjectConfig? = loadProjectConfigsJVM()

private const val defaultConfigFqn = "io.kotest.provided.ProjectConfig"

@Suppress("UNCHECKED_CAST")
@Internal
fun loadProjectConfigsJVM(): AbstractProjectConfig? {
   val fqn = fqn()
   log { "Loading project configs from fqn: $fqn" }
   val kclass = runCatching { Class.forName(fqn).kotlin }.getOrNull() ?: return null
   return instantiateOrObject(kclass as KClass<out AbstractProjectConfig>).getOrThrow()
}

private fun fqn(): String {
   val fqn = System.getProperty(KotestEngineProperties.configurationClassNames)
   return if (fqn == null) {
      log { "No project config class name provided, checking for default at $defaultConfigFqn" }
      defaultConfigFqn
   } else fqn
}
