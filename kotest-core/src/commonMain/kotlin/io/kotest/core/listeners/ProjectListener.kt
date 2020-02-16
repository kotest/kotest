package io.kotest.core.listeners

/**
 * Reusable extension to be registered with project config or auto scanned via @autoscan.
 */
interface ProjectListener : Listener {

   /**
    * Executed before the first test of the project.
    */
   fun beforeProject() {}

   /**
    * Executed after the last test of the project.
    */
   fun afterProject() {}
}
