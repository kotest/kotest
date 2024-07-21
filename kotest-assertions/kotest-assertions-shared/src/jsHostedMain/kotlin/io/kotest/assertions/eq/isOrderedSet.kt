package io.kotest.assertions.eq

actual fun isOrderedSet(item: Iterable<*>) =
   item is LinkedHashSet || (item is Set && item.size == 1)
