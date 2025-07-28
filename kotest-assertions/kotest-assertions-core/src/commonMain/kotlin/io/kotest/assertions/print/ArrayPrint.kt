package io.kotest.assertions.print

object ArrayPrint : Print<Any> {

   @Suppress("UNCHECKED_CAST")
   override fun print(a: Any, level: Int): Printed = when (a) {
      is LongArray -> ListPrint<Long>().print(a.asList(), level).copy(type = LongArray::class)
      is IntArray -> ListPrint<Int>().print(a.asList(), level).copy(type = IntArray::class)
      is ShortArray -> ListPrint<Short>().print(a.asList(), level).copy(type = ShortArray::class)
      is ByteArray -> ListPrint<Byte>().print(a.asList(), level).copy(type = ByteArray::class)
      is DoubleArray -> ListPrint<Double>().print(a.asList(), level).copy(type = DoubleArray::class)
      is FloatArray -> ListPrint<Float>().print(a.asList(), level).copy(type = FloatArray::class)
      is BooleanArray -> ListPrint<Boolean>().print(a.asList(), level).copy(type = BooleanArray::class)
      is CharArray -> ListPrint<Char>().print(a.asList(), level).copy(type = CharArray::class)
      is Array<*> -> ListPrint<Any>().print(a.asList() as List<Any>, level).copy(type = Array::class)
      else -> throw UnsupportedOperationException()
   }
}
