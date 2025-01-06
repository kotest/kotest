package io.kotest.engine.config

import io.kotest.core.test.TestScope
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

data class ConfigurationContextElement(val projectConfigResolver: ProjectConfigResolver) :
   AbstractCoroutineContextElement(ConfigurationContextElement) {
   companion object Key : CoroutineContext.Key<ConfigurationContextElement>
}

val TestScope.projectConfigResolver: ProjectConfigResolver
   get() = coroutineContext.projectConfigResolver

val CoroutineContext.projectConfigResolver: ProjectConfigResolver
   get() = get(ConfigurationContextElement)?.projectConfigResolver
      ?: error("Configuration is not injected into this CoroutineContext")
