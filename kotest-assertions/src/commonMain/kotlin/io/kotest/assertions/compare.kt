package io.kotest.assertions

fun compare(a: Any?, b: Any?): Boolean {
  return when (a) {
    is Int -> when (b) {
      is Long -> a.toLong() == b
      is Double -> a.toDouble() == b
      else -> a == b
    }
    is Float -> when (b) {
      is Double -> a.toDouble() == b
      else -> a == b
    }
    is Double -> when (b) {
      is Float -> a == b.toDouble()
      else -> a == b
    }
    is Long -> when (b) {
      is Int -> a == b.toLong()
      else -> a == b
    }
    else -> makeComparable(a) == makeComparable(b)
  }
}

private fun makeComparable(any: Any?): Any? {
  return when (any) {
    is BooleanArray -> any.asList()
    is IntArray -> any.asList()
    is ShortArray -> any.asList()
    is FloatArray -> any.asList()
    is DoubleArray -> any.asList()
    is LongArray -> any.asList()
    is ByteArray -> any.asList()
    is CharArray -> any.asList()
    is Array<*> -> any.asList()
    else -> any
  }
}
