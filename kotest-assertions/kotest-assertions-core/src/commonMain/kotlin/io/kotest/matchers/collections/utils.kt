package io.kotest.matchers.collections

/**
 * Returns a more specific underlying type if this is one of the basic collections/iterables.
 *
 * "List" for lists, "Set" for sets, "Collection" for collections etc.
 */
internal fun Iterable<*>.containerName(): String {
   return when (this) {
      is List<*> -> "List"
      is Set<*> -> "Set"
      is Collection<*> -> "Collection"
      else -> "Iterable"
   }
}

