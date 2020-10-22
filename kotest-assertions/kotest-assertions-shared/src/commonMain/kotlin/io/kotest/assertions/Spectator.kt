package io.kotest.assertions

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * An interface observing `Observer`-like classes and modeling a common way of testing
 * different implementations of that pattern.
 *
 * Possible examples of use of this might be Kotlin Flows, Android LiveData or
 * RxJava Observable.
 */
@OptIn(ExperimentalTime::class)
interface Spectator<T> {
   /**
    * The latest received value, or `null`.
    */
   val lastValue: T?

   /**
    * Contains all the values received by the [Spectator].
    */
   val history: List<T>

   /**
    * Awaits a new value for the amount of time specified in [timeout], otherwise throws an exception.
    *
    * @throws [kotlinx.coroutines.TimeoutCancellationException] when no value is received in the amount of time.
    */
   suspend fun awaitNextValue(timeout: Duration): T

   /**
    * If a value has been sent, it returns immediately the content of [lastValue], otherwise awaits for the
    * duration [timeout].
    *
    * @throws [kotlinx.coroutines.TimeoutCancellationException] when no value is received in the amount of time.
    */
   suspend fun awaitValue(timeout: Duration): T
}
