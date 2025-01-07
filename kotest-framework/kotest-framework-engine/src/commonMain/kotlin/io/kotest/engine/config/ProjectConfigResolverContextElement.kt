package io.kotest.engine.config

import io.kotest.core.test.TestScope
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

data class ProjectConfigResolverContextElement(val projectConfigResolver: ProjectConfigResolver) :
   AbstractCoroutineContextElement(ProjectConfigResolverContextElement) {
   companion object Key : CoroutineContext.Key<ProjectConfigResolverContextElement>
}

val TestScope.projectConfigResolver: ProjectConfigResolver
   get() = coroutineContext.projectConfigResolver

val CoroutineContext.projectConfigResolver: ProjectConfigResolver
   get() = get(ProjectConfigResolverContextElement)?.projectConfigResolver
      ?: error("Configuration is not injected into this CoroutineContext")
