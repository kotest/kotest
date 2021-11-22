package io.kotest.core.listeners

interface BeforeProjectListener : Listener {
   /**
    * Callback which is invoked before the first test of the project.
    */
   suspend fun beforeProject() {}
}
