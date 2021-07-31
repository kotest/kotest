package io.kotest.engine.events

import io.kotest.core.config.configuration
import io.kotest.core.config.testListeners
import io.kotest.core.spec.resolvedTestListeners
import io.kotest.core.test.TestCase

internal suspend fun TestCase.invokeBeforeInvocation(k: Int) {
   val listeners = spec.resolvedTestListeners() + configuration.testListeners()
   listeners.forEach {
      it.beforeInvocation(this, k)
   }
}

internal suspend fun TestCase.invokeAfterInvocation(k: Int) {
   val listeners = spec.resolvedTestListeners() + configuration.testListeners()
   listeners.forEach {
      it.afterInvocation(this, k)
   }
}
