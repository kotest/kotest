package io.kotest.assertions.print

import kotlin.reflect.KClass

/**
 * Global object that allows for registration of custom [Print] typeclasses.
 */
object Printers {

   private val shows = mutableMapOf<KClass<*>, Print<*>>().apply {
      put(String::class, StringPrint)
      put(Char::class, CharPrint)
      put(Long::class, LongPrint)
      put(Int::class, IntPrint)
      put(Short::class, ShortPrint)
      put(Byte::class, BytePrint)
      put(UByte::class, UBytePrint)
      put(UShort::class, UShortPrint)
      put(UInt::class, UIntPrint)
      put(ULong::class, ULongPrint)
      put(Double::class, DoublePrint)
      put(Float::class, FloatPrint)
      put(Boolean::class, BooleanPrint)
      put(Map::class, MapPrint)
      put(BooleanArray::class, ArrayPrint)
      put(IntArray::class, ArrayPrint)
      put(ShortArray::class, ArrayPrint)
      put(FloatArray::class, ArrayPrint)
      put(DoubleArray::class, ArrayPrint)
      put(LongArray::class, ArrayPrint)
      put(ByteArray::class, ArrayPrint)
      put(CharArray::class, ArrayPrint)
      put(LongRange::class, LongRangePrint)
      put(IntRange::class, IntRangePrint)
      put(UIntRange::class, UIntRangePrint)
      put(ULongRange::class, ULongRangePrint)
      put(CharRange::class, CharRangePrint)
      put(Array::class, ArrayPrint)
      put(List::class, ListPrint<Any>())
      put(Iterable::class, IterablePrint<Any>())
      put(KClass::class, KClassPrint)
   }

   fun <T : Any> add(kclass: KClass<out T>, print: Print<T>) {
      shows[kclass] = print
   }

   fun remove(kclass: KClass<*>) {
      shows.remove(kclass)
   }

   fun all(): Map<KClass<*>, Print<*>> = shows.toMap()
}
