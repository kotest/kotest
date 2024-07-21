package io.kotest.core.listeners

/**
 * Brings together [BeforeProjectListener] and [AfterProjectListener]. Exists for historical reasons.
 * Users can choose to extend this, or the constituent interfaces.
 */
interface ProjectListener : BeforeProjectListener, AfterProjectListener

@Suppress("DEPRECATION") // Remove when removing Listener
interface BeforeProjectListener : Listener {
   /**
    * Callback which is invoked before the first test of the project.
    */
   suspend fun beforeProject() {}
}

@Suppress("DEPRECATION") // Remove when removing Listener
interface AfterProjectListener : Listener {
   /**
    * Callback which is invoked after the last test of the project.
    */
   suspend fun afterProject() {}
}
