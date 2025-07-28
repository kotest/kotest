package io.kotest.assertions.eq

actual fun isOrderedSet(item: Iterable<*>): Boolean =
   item is LinkedHashSet || (item is Set && item.size == 1)
