package io.kotest.assertions.eq

import java.util.SortedSet

/**
 * JVM-specific: Allows TreeSet to be considered ordered for iterable comparisons
 */
actual fun isOrderedSet(item: Iterable<*>): Boolean {
   return item is LinkedHashSet ||
      item is SortedSet ||
      (item is Set && item.size <= 1) ||
      isJavaSequencedSet(item::class.java)
}

private fun isJavaSequencedSet(clazz: Class<out Iterable<*>>): Boolean {
   return clazz.name == $$"java.util.Collections$UnmodifiableSequencedSet"
}
