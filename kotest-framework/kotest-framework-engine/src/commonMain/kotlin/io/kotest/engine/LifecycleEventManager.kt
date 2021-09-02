package io.kotest.engine

import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener

expect class LifecycleEventManager() {
   fun beforeProject(listeners: List<BeforeProjectListener>)
   fun afterProject(listeners: List<AfterProjectListener>)
}
