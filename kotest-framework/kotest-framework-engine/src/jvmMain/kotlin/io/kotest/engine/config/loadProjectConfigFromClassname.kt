package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.internal.KotestEngineProperties

internal actual fun loadProjectConfigFromClassname(): AbstractProjectConfig? {
   val fqn = System.getProperty(KotestEngineProperties.configurationClassName) ?: return null
   return Class.forName(fqn).newInstance() as AbstractProjectConfig
}
