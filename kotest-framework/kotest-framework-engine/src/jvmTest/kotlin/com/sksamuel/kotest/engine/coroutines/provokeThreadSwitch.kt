package com.sksamuel.kotest.engine.coroutines

/**
 * Provoke the dispatcher to switch coroutine threads by keeping the coroutine alive for a sufficient time period.
 */
@Suppress("RedundantSuspendModifier")
suspend fun provokeThreadSwitch() {
   @Suppress("BlockingMethodInNonBlockingContext")
   Thread.sleep(50)
}
