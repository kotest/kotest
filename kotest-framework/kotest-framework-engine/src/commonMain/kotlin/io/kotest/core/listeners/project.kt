package io.kotest.core.listeners

import io.kotest.core.extensions.Extension
import io.kotest.core.spec.AfterProject

/**
 * Union of [BeforeProjectListener] and [AfterProjectListener].
 * Users can choose to extend this or the constituent interfaces.
 */
interface ProjectListener : BeforeProjectListener, AfterProjectListener

interface BeforeProjectListener : Extension {
   /**
    * Callback which is invoked before the test suite is started.
    *
    * Note, for more control over the engine context, see [io.kotest.core.extensions.ProjectExtension].
    */
   suspend fun beforeProject() {}
}

interface AfterProjectListener : Extension {
   /**
    * Callback which is invoked once the test suite has completed.
    *
    * Note, for more control over the engine context, see [io.kotest.core.extensions.ProjectExtension].
    */
   suspend fun afterProject() {}
}

internal class ContextAwareAfterProjectListener(
   val context: String?,
   private val f: AfterProject
) : AfterProjectListener {
   override suspend fun afterProject() {
      f()
   }
}
