package io.kotest.engine.extensions

import io.kotest.core.Logger
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener

internal class ProjectExtensions(private val extensions: List<Extension>) {

   private val logger = Logger(ProjectExtensions::class)

   suspend fun beforeProject(): List<ExtensionException.BeforeProjectException> {
      val ext = extensions.filterIsInstance<BeforeProjectListener>()
      logger.log { Pair(null, "Invoking ${ext.size} BeforeProjectListeners") }
      return ext.mapNotNull {
         try {
            it.beforeProject()
            null
         } catch (t: Throwable) {
            ExtensionException.BeforeProjectException(t)
         }
      }
   }

   suspend fun afterProject(): List<ExtensionException.AfterProjectException> {
      val ext = extensions.filterIsInstance<AfterProjectListener>()
      logger.log { Pair(null, "Invoking ${ext.size} AfterProjectListeners") }
      return ext.mapNotNull {
         try {
            it.afterProject()
            null
         } catch (t: Throwable) {
            ExtensionException.AfterProjectException(t)
         }
      }
   }
}
