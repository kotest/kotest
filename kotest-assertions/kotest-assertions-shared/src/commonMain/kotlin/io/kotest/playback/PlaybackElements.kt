package io.kotest.playback

class PlaybackElements<T>(
   source: Sequence<T>
) {
   val iterator = source.iterator()

   fun next(): T = iterator.next()
}

// syntactic  sugar
fun<T> Sequence<T>.toFunction(): PlaybackElements<T> = PlaybackElements(this)
