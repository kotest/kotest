package io.kotest.core

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.test.TestScope
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * Runtime parameters for the project.
 *
 * Retrieve from a test or listener scope by [coroutineContext.projectContext].
 */
data class ProjectContext(val suite: TestSuite, val tags: TagExpression, val configuration: ProjectConfiguration) {
   constructor(configuration: ProjectConfiguration) : this(TestSuite(emptyList()), TagExpression.Empty, configuration)
}

fun ProjectContext.withTestSuite(suite: TestSuite): ProjectContext {
   return ProjectContext(suite, tags, configuration)
}

fun ProjectContext.withConfiguration(c: ProjectConfiguration): ProjectContext {
   return ProjectContext(suite, tags, c)
}

fun ProjectContext.withTags(tags: TagExpression): ProjectContext {
   return ProjectContext(suite, tags, configuration)
}

data class ProjectContextElement(val projectContext: ProjectContext) :
   AbstractCoroutineContextElement(ProjectContextElement) {
   companion object Key : CoroutineContext.Key<ProjectContextElement>
}

val TestScope.projectContext: ProjectContext
   get() = coroutineContext.projectContext

val CoroutineContext.projectContext: ProjectContext
   get() = get(ProjectContextElement)?.projectContext
      ?: error("ProjectContext is not injected into this CoroutineContext")

