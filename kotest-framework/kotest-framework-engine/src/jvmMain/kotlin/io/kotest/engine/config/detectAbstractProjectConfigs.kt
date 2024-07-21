package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.engine.instantiateOrObject
import io.kotest.core.log
import org.jetbrains.annotations.ApiStatus.Internal

internal actual fun detectAbstractProjectConfigs(): List<AbstractProjectConfig> = detectAbstractProjectConfigsJVM()

@Internal
fun detectAbstractProjectConfigsJVM(): List<AbstractProjectConfig> {
   // this property is used to disable class path scanning for configurations
   if (System.getProperty(KotestEngineProperties.disableConfigurationClassPathScanning) == "true") {
      log { "Detecting abstract project configs is DISABLED by sysprop" }
      return emptyList()
   }

   log { "Detecting abstract project configs JVM" }
   return classgraph().scan().use { result ->
      result.getSubclasses(AbstractProjectConfig::class.java.name)
         .map { Class.forName(it.name) as Class<out AbstractProjectConfig> }
         .mapNotNull { instantiateOrObject(it).getOrNull() }
   }
}
