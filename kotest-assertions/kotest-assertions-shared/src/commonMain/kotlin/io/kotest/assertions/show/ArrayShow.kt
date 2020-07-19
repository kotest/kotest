package io.kotest.assertions.show

object ArrayShow : Show<Any> {
   @Suppress("UNCHECKED_CAST")
   override fun show(a: Any): Printed = when (a) {
      is LongArray -> ListShow<Long>().show(a.asList())
      is IntArray -> ListShow<Int>().show(a.asList())
      is ShortArray -> ListShow<Short>().show(a.asList())
      is ByteArray -> ListShow<Byte>().show(a.asList())
      is DoubleArray -> ListShow<Double>().show(a.asList())
      is FloatArray -> ListShow<Float>().show(a.asList())
      is BooleanArray -> ListShow<Boolean>().show(a.asList())
      is CharArray -> ListShow<Char>().show(a.asList())
      is Array<*> -> ListShow<Any>().show(a.asList() as List<Any>)
      else -> throw UnsupportedOperationException()
   }
}
