package io.kotest.engine.extensions

import io.kotest.core.Logger
import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener

internal class ProjectExtensions(private val registry: ExtensionRegistry) {

   private val logger = Logger(ProjectExtensions::class)

   suspend fun beforeProject(): List<ExtensionException.BeforeProjectException> {
      val ext = registry.all().filterIsInstance<BeforeProjectListener>()
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
      val ext = registry.all().filterIsInstance<AfterProjectListener>()
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
