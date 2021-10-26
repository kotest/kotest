package io.kotest.core.config

import io.kotest.core.test.TestContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

data class ConfigurationContextElement(val configuration: Configuration) :
   AbstractCoroutineContextElement(ConfigurationContextElement) {
   companion object Key : CoroutineContext.Key<ConfigurationContextElement>
}

val TestContext.configuration: Configuration
   get() = coroutineContext[ConfigurationContextElement]?.configuration
      ?: error("Configuration is not injected into this TestContext")
