package io.kotest.property.arbitrary

import io.kotest.mpp.bestName
import io.kotest.property.Arb
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
inline fun <reified A> Arb.Companion.default(): Arb<A> = forClass(A::class)

@Suppress("UNCHECKED_CAST")
fun defaultForClass(kClass: KClass<*>): Arb<*>? {
   return when (kClass.bestName()) {
      "java.lang.String", "kotlin.String", "String" -> Arb.string()
      "java.lang.Character", "kotlin.Char", "Char" -> Arb.char()
      "java.lang.Long", "kotlin.Long", "Long" -> Arb.long()
      "kotlin.ULong", "ULong" -> Arb.uLong()
      "java.lang.Integer", "kotlin.Int", "Int" -> Arb.int()
      "kotlin.UInt", "UInt" -> Arb.uInt()
      "java.lang.Short", "kotlin.Short", "Short" -> Arb.short()
      "kotlin.UShort", "UShort" -> Arb.uShort()
      "java.lang.Byte", "kotlin.Byte", "Byte" -> Arb.byte()
      "kotlin.UByte", "UByte" -> Arb.uByte()
      "java.lang.Double", "kotlin.Double", "Double" -> Arb.double()
      "java.lang.Float", "kotlin.Float", "Float" -> Arb.float()
      "java.lang.Boolean", "kotlin.Boolean", "Boolean" -> Arb.boolean()
      else -> null
   }
}

expect inline fun <reified A> targetDefaultForClass(): Arb<A>?

inline fun <reified A> Arb.Companion.forClass(kClass: KClass<*>): Arb<A> =
   defaultForClass(kClass) as Arb<A>?
      ?: targetDefaultForClass()
      ?: throw NoGeneratorFoundException("Cannot infer generator for ${A::class}; specify generators explicitly")

class NoGeneratorFoundException(msg: String) : RuntimeException(msg)
