package io.kotest.assertions.eq

import java.util.TreeSet

/**
 * JVM-specific: Allows TreeSet to be considered ordered for iterable comparisons
 */
actual fun isOrderedSet(item: Iterable<*>) =
   item is LinkedHashSet ||
      item is TreeSet ||
      (item is Set && item.size == 1)
