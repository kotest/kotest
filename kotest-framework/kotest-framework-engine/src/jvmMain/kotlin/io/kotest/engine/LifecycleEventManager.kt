package io.kotest.engine

import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener

actual class LifecycleEventManager {

   actual suspend fun beforeProject(listeners: List<BeforeProjectListener>) {
      listeners.forEach { it.beforeProject() }
   }

   actual suspend fun afterProject(listeners: List<AfterProjectListener>) {
      listeners.forEach { it.afterProject() }
   }
}
