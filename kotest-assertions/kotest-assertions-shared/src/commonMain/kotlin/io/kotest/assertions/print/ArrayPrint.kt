package io.kotest.assertions.print

object ArrayPrint : Print<Any> {

   @Deprecated(PRINT_DEPRECATION_MSG)
   override fun print(a: Any): Printed = print(a, 0)

   @Suppress("UNCHECKED_CAST")
   override fun print(a: Any, level: Int): Printed = when (a) {
      is LongArray -> ListPrint<Long>().print(a.asList(), level)
      is IntArray -> ListPrint<Int>().print(a.asList(), level)
      is ShortArray -> ListPrint<Short>().print(a.asList(), level)
      is ByteArray -> ListPrint<Byte>().print(a.asList(), level)
      is DoubleArray -> ListPrint<Double>().print(a.asList(), level)
      is FloatArray -> ListPrint<Float>().print(a.asList(), level)
      is BooleanArray -> ListPrint<Boolean>().print(a.asList(), level)
      is CharArray -> ListPrint<Char>().print(a.asList(), level)
      is Array<*> -> ListPrint<Any>().print(a.asList() as List<Any>, level)
      else -> throw UnsupportedOperationException()
   }
}
