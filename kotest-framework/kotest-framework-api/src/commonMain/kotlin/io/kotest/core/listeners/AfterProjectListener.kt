package io.kotest.core.listeners

interface AfterProjectListener : Listener {
   /**
    * Callback which is invoked after the last test of the project.
    */
   suspend fun afterProject() {}
}
