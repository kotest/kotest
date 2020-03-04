package io.kotest.assertions

private object StringConstructionConstants {
   const val maxSnippetSize = 20
}

/** Return a string representation of [obj] that is less ambiguous than `toString` */
fun stringRepr(obj: Any?): String = when (obj) {
   is Float -> "${obj}f"
   is Long -> "${obj}L"
   is Char -> "'$obj'"
   is String -> "\"$obj\""
   is Array<*> -> stringRepr(obj.toList())
   is BooleanArray -> stringRepr(obj.toList())
   is IntArray -> stringRepr(obj.toList())
   is ShortArray -> stringRepr(obj.toList())
   is FloatArray -> stringRepr(obj.toList())
   is DoubleArray -> stringRepr(obj.toList())
   is LongArray -> stringRepr(obj.toList())
   is ByteArray -> stringRepr(obj.toList())
   is CharArray -> stringRepr(obj.toList())
   is List<*> -> obj.getCollectionSnippet()
   is Iterable<*> -> stringRepr(obj.toList())
   is Map<*, *> -> stringRepr(obj.map { (k, v) -> recursiveRepr(obj, k) to recursiveRepr(obj, v) })
   else -> obj.toString()
}

private fun List<*>.getCollectionSnippet(): String {
   val remainingItems = size - StringConstructionConstants.maxSnippetSize

   val suffix = when {
      remainingItems <= 0 -> "]"
      else -> "] and $remainingItems more"
   }

   return joinToString(
      separator = ", ",
      prefix = "[",
      postfix = suffix,
      limit = StringConstructionConstants.maxSnippetSize
   ) {
      recursiveRepr(this, it)
   }
}

private fun recursiveRepr(root: Any, node: Any?): String {
   return if (root == node) "(this ${root::class.simpleName})" else stringRepr(node)
}
