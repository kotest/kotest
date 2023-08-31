package com.sksamuel.kotest.engine.coroutines

/**
 * Provoke the dispatcher to switch coroutine threads by keeping the coroutine alive for a sufficient time period.
 *
 * This function is not suspending, but intended to be invoked in a coroutine exclusively.
 */
internal fun provokeThreadSwitch() {
   Thread.sleep(50)
}
