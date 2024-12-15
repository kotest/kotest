package io.kotest.core.project

import io.kotest.core.TagExpression
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.test.TestScope
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

data class ProjectContext(val suite: TestSuite, val tags: TagExpression, val configuration: ProjectConfiguration) {
   constructor(configuration: ProjectConfiguration) : this(TestSuite.empty, TagExpression.Empty, configuration)
}

data class ProjectContextElement(val projectContext: ProjectContext) :
   AbstractCoroutineContextElement(ProjectContextElement) {
   companion object Key : CoroutineContext.Key<ProjectContextElement>
}

/**
 * Extracts the[ProjectContext] from the [TestScope]s [CoroutineContext].
 */
val TestScope.projectContext: ProjectContext
   get() = coroutineContext.projectContext

val CoroutineContext.projectContext: ProjectContext
   get() = get(ProjectContextElement)?.projectContext
      ?: error("projectContext is not injected into this CoroutineContext")
