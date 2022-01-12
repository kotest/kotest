package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.mpp.instantiateOrObject

internal actual fun detectAbstractProjectConfigs(): List<AbstractProjectConfig> {

   // this property is used to disable class path scanning for configurations
   if (System.getProperty(KotestEngineProperties.disableConfigurationClassPathScanning) == "true")
      return emptyList()

   return classgraph().scan().use { result ->
      result.getSubclasses(AbstractProjectConfig::class.java.name)
         .map { Class.forName(it.name) as Class<out AbstractProjectConfig> }
         .mapNotNull { instantiateOrObject(it).getOrNull() }
   }
}
