package io.kotest.equals.types.utils

import kotlin.math.max

fun printValues(collection: Collection<*>, maxElements: Int = 10): String {
   val extra = collection.size - maxElements
   val actualMax = max(1, maxElements)
   return collection.joinToString(
      prefix = "[",
      separator = ", ",
      postfix = "${if (extra > 0) "... and $extra more" else ""}]"
   )
}
