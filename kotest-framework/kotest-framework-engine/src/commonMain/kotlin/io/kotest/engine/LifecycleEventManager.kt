package io.kotest.engine

import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener

expect class LifecycleEventManager() {
   suspend fun beforeProject(listeners: List<BeforeProjectListener>)
   suspend fun afterProject(listeners: List<AfterProjectListener>)
}
