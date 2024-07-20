package io.kotest.matchers.equality

/**
 * This is currently unused but will form the basis of reflection based checks in 6.0
 */
internal fun <T> T.isBuiltInType() =
   this is Number
   || this is UInt
   || this is ULong
   || this is UByte
   || this is UShort
   || this is String
   || this is Boolean
   || this is Char
   || this is IntArray
   || this is CharArray
   || this is LongArray
   || this is ShortArray
   || this is ByteArray
   || this is DoubleArray
   || this is FloatArray
   || this is BooleanArray
   || this is UIntArray
   || this is ULongArray
   || this is UByteArray
   || this is UShortArray
   || this is Array<*>
   || this is List<*>
   || this is Set<*>
   || this is Map<*, *>
   || this is Sequence<*>
