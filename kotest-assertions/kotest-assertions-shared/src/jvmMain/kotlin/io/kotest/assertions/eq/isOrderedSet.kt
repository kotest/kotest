package io.kotest.assertions.eq

import java.util.*

/**
 * JVM-specific: Allows TreeSet to be considered ordered for iterable comparisons
 */
actual fun isOrderedSet(item: Iterable<*>) =
   item is LinkedHashSet ||
      item is SequencedSet ||
      (item is Set && item.size == 1)
