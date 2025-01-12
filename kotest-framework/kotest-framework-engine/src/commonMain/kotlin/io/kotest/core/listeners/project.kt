package io.kotest.core.listeners

import io.kotest.core.extensions.Extension

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
