package io.kotest.assertions.show

import kotlin.reflect.KClass

/**
 * A default implementation of [Show] that handles arrays, collections and primitives as well as nullable values.
 */
object DefaultShow : Show<Any?> {
   override fun show(a: Any?): Printed = when (a) {
      null -> "<null>".printed()
      is Boolean -> "$a".printed()
      is Float -> "${a}f".printed()
      is Long -> "${a}L".printed()
      is Char -> "'$a'".printed()
      is Int -> a.toString().printed()
      is Short -> a.toString().printed()
      is Byte -> a.toString().printed()
      is String -> "\"$a\"".printed()
      is Array<*> -> show(a.toList())
      is BooleanArray -> show(a.toList())
      is IntArray -> show(a.toList())
      is ShortArray -> show(a.toList())
      is FloatArray -> show(a.toList())
      is DoubleArray -> show(a.toList())
      is LongArray -> show(a.toList())
      is ByteArray -> show(a.toList())
      is CharArray -> show(a.toList())
      is List<*> -> a.getCollectionSnippet()
      is Iterable<*> -> show(handleRecursiveIterable(a, a::class))
      else -> a.toString().printed()
   }
}

private const val MaxCollectionSnippetSize = 20

/**
 * Returns the values in a Collection, up to a max size
 */
private fun Collection<*>.getCollectionSnippet(): Printed {
   val remainingItems = size - MaxCollectionSnippetSize

   val suffix = when {
      remainingItems <= 0 -> "]"
      else -> "] and $remainingItems more"
   }

   return joinToString(
      separator = ", ",
      prefix = "[",
      postfix = suffix,
      limit = MaxCollectionSnippetSize
   ) {
      recursiveRepr(this, it).value
   }.printed()
}

internal fun recursiveRepr(root: Any, node: Any?): Printed {
   return if (root == node) "(this ${root::class.simpleName})".printed() else node.show()
}

private fun handleRecursiveIterable(i: Iterable<*>, kClass: KClass<out Any>) : Any {
   return if (i.iterator().hasNext() && kClass.isInstance(i.iterator().next())) i.toString() else i.toList()
}
