package io.kotest.core.listeners

/**
 * Reusable extension to be registered with project config or auto scanned via @autoscan.
 */
interface ProjectListener : Listener {

   override val name: String
      get() = "defaultProjectListener"

   /**
    * Executed before the first test of the project.
    */
   suspend fun beforeProject() {}

   /**
    * Executed after the last test of the project.
    */
   suspend fun afterProject() {}
}
