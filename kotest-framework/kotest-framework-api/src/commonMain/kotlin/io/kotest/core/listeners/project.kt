package io.kotest.core.listeners

/**
 * Brings together [BeforeProjectListener] and [AfterProjectListener].
 * Users can choose to extend this, or the component interfaces.
 */
interface ProjectListener : BeforeProjectListener, AfterProjectListener {

   override val name: String
      get() = "defaultProjectListener"

}

interface BeforeProjectListener : Listener {

   override val name: String
      get() = "defaultBeforeProjectListener"

   /**
    * Callback which is invoked before the first test of the project.
    */
   suspend fun beforeProject() {}
}

interface AfterProjectListener : Listener {

   override val name: String
      get() = "defaultAfterProjectListener"

   /**
    * Callback which is invoked after the last test of the project.
    */
   suspend fun afterProject() {}
}
