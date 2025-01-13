package io.kotest.core.listeners

import io.kotest.core.extensions.Extension
import io.kotest.core.spec.AfterProject

/**
 * Union of [BeforeProjectListener] and [AfterProjectListener].
 * Users can choose to extend this, or the constituent interfaces.
 */
interface ProjectListener : BeforeProjectListener, AfterProjectListener

interface BeforeProjectListener : Extension {
   /**
    * Callback which is invoked before the first test of the project.
    */
   suspend fun beforeProject() {}
}

interface AfterProjectListener : Extension {
   /**
    * Callback which is invoked after the last test of the project.
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
