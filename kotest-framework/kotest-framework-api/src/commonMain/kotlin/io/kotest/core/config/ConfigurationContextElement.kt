package io.kotest.core.config

import io.kotest.core.test.TestScope
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

data class ConfigurationContextElement(val configuration: Configuration) :
   AbstractCoroutineContextElement(ConfigurationContextElement) {
   companion object Key : CoroutineContext.Key<ConfigurationContextElement>
}

val TestScope.configuration: Configuration
   get() = coroutineContext.configuration

val CoroutineContext.configuration: Configuration
   get() = get(ConfigurationContextElement)?.configuration
      ?: error("Configuration is not injected into this CoroutineContext")
