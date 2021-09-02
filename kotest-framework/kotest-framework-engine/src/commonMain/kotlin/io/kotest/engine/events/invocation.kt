package io.kotest.engine.events

import io.kotest.core.config.configuration
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.resolvedTestListeners
import io.kotest.core.test.TestCase

internal suspend fun TestCase.invokeBeforeInvocation(k: Int) {
   val listeners = spec.resolvedTestListeners() + configuration.extensions().filterIsInstance<TestListener>()
   listeners.forEach {
      it.beforeInvocation(this, k)
   }
}

internal suspend fun TestCase.invokeAfterInvocation(k: Int) {
   val listeners = spec.resolvedTestListeners() + configuration.extensions().filterIsInstance<TestListener>()
   listeners.forEach {
      it.afterInvocation(this, k)
   }
}
