package io.kotest.engine.events

import io.kotest.core.config.configuration
import io.kotest.core.config.testListeners
import io.kotest.core.spec.Spec
import io.kotest.core.spec.resolvedTestListeners
import io.kotest.fp.Try
import io.kotest.mpp.log

/**
 * Notifies the user listeners that a [Spec] is starting.
 * This will be invoked for every instance of a spec.
 */
internal suspend fun Spec.invokeBeforeSpec(): Try<Spec> = Try {
   log { "invokeBeforeSpec $this" }
   val listeners = resolvedTestListeners() + configuration.testListeners()
   listeners.forEach {
      it.beforeSpec(this)
   }
   this
}

/**
 * Notifies the user listeners that a [Spec] has finished.
 * This will be invoked for every instance of a spec.
 */
internal suspend fun Spec.invokeAfterSpec(): Try<Spec> = Try {
   log { "invokeAfterSpec $this" }

   registeredAutoCloseables().let { closeables ->
      log { "Closing ${closeables.size} autocloseables [$closeables]" }
      closeables.forEach { it.value.close() }
   }

   val listeners = resolvedTestListeners() + configuration.testListeners()
   listeners.forEach { it.afterSpec(this) }
   this
}
