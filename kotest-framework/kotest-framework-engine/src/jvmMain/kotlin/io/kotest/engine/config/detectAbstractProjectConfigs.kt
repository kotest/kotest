package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.log
import io.kotest.engine.instantiateOrObject
import org.jetbrains.annotations.ApiStatus.Internal

internal actual fun detectAbstractProjectConfigs(): List<AbstractProjectConfig> = detectAbstractProjectConfigsJVM()

@Internal
@Deprecated("Will be removed entirely in a future release. Deprecated since 6.0")
fun detectAbstractProjectConfigsJVM(): List<AbstractProjectConfig> {

   // this property is used to enabled class path scanning for configurations
   if (System.getProperty(KotestEngineProperties.enableConfigurationClassPathScanning) == "true") {
      log { "Detecting abstract project configs by class path scanning" }
      return classgraph().scan().use { result ->
         result.getSubclasses(AbstractProjectConfig::class.java.name)
            .map { Class.forName(it.name) as Class<out AbstractProjectConfig> }
            .mapNotNull { instantiateOrObject(it).getOrNull() }
      }
   } else {
      return emptyList()
   }
}
