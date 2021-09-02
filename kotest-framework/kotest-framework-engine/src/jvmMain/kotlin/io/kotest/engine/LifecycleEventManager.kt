package io.kotest.engine

import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener

actual class LifecycleEventManager {
   actual fun beforeProject(listeners: List<BeforeProjectListener>) {
   }

   actual fun afterProject(listeners: List<AfterProjectListener>) {
   }
}
