package io.kotest.assertions.print

object ArrayPrint : Print<Any> {
   @Suppress("UNCHECKED_CAST")
   override fun print(a: Any): Printed = when (a) {
      is LongArray -> ListPrint<Long>().print(a.asList())
      is IntArray -> ListPrint<Int>().print(a.asList())
      is ShortArray -> ListPrint<Short>().print(a.asList())
      is ByteArray -> ListPrint<Byte>().print(a.asList())
      is DoubleArray -> ListPrint<Double>().print(a.asList())
      is FloatArray -> ListPrint<Float>().print(a.asList())
      is BooleanArray -> ListPrint<Boolean>().print(a.asList())
      is CharArray -> ListPrint<Char>().print(a.asList())
      is Array<*> -> ListPrint<Any>().print(a.asList() as List<Any>)
      else -> throw UnsupportedOperationException()
   }
}
