package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.mpp.instantiateOrObject

actual fun detectAbstractProjectConfigs(): List<AbstractProjectConfig> {
   return classgraph().scan()
      .getSubclasses(AbstractProjectConfig::class.java.name)
      .map { Class.forName(it.name) as Class<out AbstractProjectConfig> }
      .mapNotNull { instantiateOrObject(it).getOrNull() }
}
