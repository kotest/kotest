package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.mpp.instantiateOrObject
import org.jetbrains.annotations.ApiStatus.Internal

internal actual fun loadProjectConfigFromClassname(): AbstractProjectConfig? = loadProjectConfigFromClassnameJVM()

@Internal
fun loadProjectConfigFromClassnameJVM(): AbstractProjectConfig? {
   val fqn = System.getProperty(KotestEngineProperties.configurationClassName) ?: return null
   return instantiateOrObject(Class.forName(fqn)).getOrThrow() as AbstractProjectConfig
}
