package io.kotest.assertions.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.kotest.assertions.Spectator
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
class LiveDataSpectator<T> @PublishedApi internal constructor() : Observer<T>, Spectator<T> {
   private val _history = mutableListOf<T>()

   override val history: List<T>
      get() = _history

   private val valueChannel: Channel<T> = Channel()

   override fun onChanged(value: T) {
      _history.add(value)
      valueChannel.offer(value)
   }

   override val lastValue: T?
      get() = _history.lastOrNull()

   override suspend fun awaitNextValue(timeout: Duration): T = withTimeout(timeout) {
      valueChannel.receive()
   }

   override suspend fun awaitValue(timeout: Duration): T =
      lastValue ?: awaitNextValue(timeout)
}

/**
 * Returns a [Spectator] which allows to test the receiver [LiveData].
 */
@OptIn(ExperimentalTime::class)
inline fun <T> LiveData<T>.spectate(block: Spectator<T>.() -> Unit = {}): Spectator<T> =
   LiveDataSpectator<T>().also { observeForever(it) }.apply(block)
