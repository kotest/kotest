package io.kotest.engine.config

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.test.TestScope
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

data class ConfigurationContextElement(val configuration: ProjectConfiguration) :
   AbstractCoroutineContextElement(ConfigurationContextElement) {
   companion object Key : CoroutineContext.Key<ConfigurationContextElement>
}

val TestScope.configuration: ProjectConfiguration
   get() = coroutineContext.configuration

val CoroutineContext.configuration: ProjectConfiguration
   get() = get(ConfigurationContextElement)?.configuration
      ?: error("Configuration is not injected into this CoroutineContext")
