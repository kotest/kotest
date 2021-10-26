package io.kotest.engine.project

import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.engine.extensions.ExtensionException
import io.kotest.mpp.log

class ProjectExtensions(private val registry: ExtensionRegistry) {

   suspend fun beforeProject(): List<ExtensionException.BeforeProjectException> {
      val ext = registry.all().filterIsInstance<BeforeProjectListener>()
      log { "ProjectExtensions: Invoking ${ext.size} BeforeProjectListeners" }
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
      log { "ProjectExtensions: Invoking ${ext.size} AfterProjectListeners" }
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
