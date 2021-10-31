package io.kotest.core

import io.kotest.core.config.Configuration
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

data class ProjectContext(val tags: TagExpression, val specs: List<SpecRef>, val configuration: Configuration) {
   constructor(configuration: Configuration) : this(TagExpression.Empty, emptyList(), configuration)
}

data class ProjectContextElement(val projectContext: ProjectContext) :
   AbstractCoroutineContextElement(ProjectContextElement) {
   companion object Key : CoroutineContext.Key<ProjectContextElement>
}

val TestContext.projectContext: ProjectContext
   get() = coroutineContext.projectContext

val CoroutineContext.projectContext: ProjectContext
   get() = get(ProjectContextElement)?.projectContext
      ?: error("projectContext is not injected into this CoroutineContext")
