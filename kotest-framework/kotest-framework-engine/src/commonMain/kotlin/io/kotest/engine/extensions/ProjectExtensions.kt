package io.kotest.engine.extensions

import io.kotest.core.Logger
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.engine.config.ProjectConfigResolver

internal class ProjectExtensions(private val projectConfigResolver: ProjectConfigResolver) {

   private val logger = Logger(ProjectExtensions::class)

   suspend fun beforeProject(): List<ExtensionException.BeforeProjectException> {
      val extensions = projectConfigResolver.extensions().filterIsInstance<BeforeProjectListener>()
      logger.log { Pair(null, "Invoking ${extensions.size} BeforeProjectListeners") }
      return extensions.mapNotNull { ext ->
         try {
            ext.beforeProject()
            null
         } catch (t: Throwable) {
            ExtensionException.BeforeProjectException(t, ext)
         }
      }
   }

   suspend fun afterProject(): List<ExtensionException.AfterProjectException> {
      val extensions = projectConfigResolver.extensions().filterIsInstance<AfterProjectListener>()
      logger.log { Pair(null, "Invoking ${extensions.size} AfterProjectListeners") }
      return extensions.mapNotNull { ext ->
         try {
            ext.afterProject()
            null
         } catch (t: Throwable) {
            ExtensionException.AfterProjectException(t, ext)
         }
      }
   }
}
