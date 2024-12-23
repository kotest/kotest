package io.kotest.playback

class PlaybackResults<T>(
   source: Sequence<Result<T>>
) {
   val iterator = source.iterator()

   fun next(): T = iterator.next().getOrThrow()
}

// syntactic  sugar
fun<T> Sequence<Result<T>>.toFunction(): PlaybackResults<T> = PlaybackResults(this)
