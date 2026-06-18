package io.kotest.matchers.collections

/**
 * Returns a more specific underlying type if this is one of the basic collections/iterables.
 *
 * "List" for lists, "Set" for sets, "Range" for ranges etc.
 */
internal fun Iterable<*>.containerName(): String {
   return when (this) {
      is List -> "List"
      is Set -> "Set"
      is Map<*, *> -> "Map"
      is ClosedRange<*>, is OpenEndRange<*> -> "Range"
      is Collection -> "Collection"
      else -> "Iterable"
   }
}
