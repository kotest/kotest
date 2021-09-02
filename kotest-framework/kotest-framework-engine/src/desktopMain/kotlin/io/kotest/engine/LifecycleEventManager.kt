package io.kotest.engine

import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener
import kotlinx.coroutines.runBlocking

actual class LifecycleEventManager {
   actual fun beforeProject(listeners: List<BeforeProjectListener>) {
      runBlocking {
         listeners.forEach { it.beforeProject() }
      }
   }

   actual fun afterProject(listeners: List<AfterProjectListener>) {
      runBlocking {
         listeners.forEach { it.afterProject() }
      }
   }
}
