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
    * The callback will return any errors during execution of the test suite.
    * Note: These errors are not test failures, but unexpected errors.
    *
    * Errors from the callback should be returned in turn, unless the extension
    * wishes to override.
    */
   suspend fun aroundProject(callback: suspend () -> List<Throwable>): List<Throwable>
}
