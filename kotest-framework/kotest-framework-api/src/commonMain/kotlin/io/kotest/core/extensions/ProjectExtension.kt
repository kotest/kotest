package io.kotest.core.extensions

/**
 * Extension point that allows intercepting execution of projects.
 */
interface ProjectExtension : Extension {

   /**
    * Implementations must invoke the [callback] callback if they wish
    * the project to be executed, otherwise not calling [callback] will skip
    * the entire project.
    *
    * Any errors returned from the callback should be returned from this method.
    */
   suspend fun aroundProject(callback: suspend () -> List<Throwable>): List<Throwable>
}
