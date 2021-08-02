package io.kotest.engine

import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.core.spec.Spec

actual class SpecRunner {
   /**
    * Execute the given [spec] and invoke the [onComplete] callback once finished.
    */
   actual fun execute(spec: Spec, onComplete: suspend () -> Unit) {
   }
}

actual class LifecycleEventManager {
   actual fun beforeProject(listeners: List<BeforeProjectListener>) {
   }

   actual fun afterProject(listeners: List<AfterProjectListener>) {
   }
}
