package io.kotest.core

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestScope
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

data class ProjectContext(val tags: TagExpression, val specs: List<SpecRef>, val configuration: ProjectConfiguration) {
   constructor(configuration: ProjectConfiguration) : this(TagExpression.Empty, emptyList(), configuration)
}

data class ProjectContextElement(val projectContext: ProjectContext) :
   AbstractCoroutineContextElement(ProjectContextElement) {
   companion object Key : CoroutineContext.Key<ProjectContextElement>
}

val TestScope.projectContext: ProjectContext
   get() = coroutineContext.projectContext

val CoroutineContext.projectContext: ProjectContext
   get() = get(ProjectContextElement)?.projectContext
      ?: error("projectContext is not injected into this CoroutineContext")
