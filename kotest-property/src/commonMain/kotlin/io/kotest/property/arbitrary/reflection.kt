package io.kotest.property.arbitrary

import io.kotest.mpp.bestName
import io.kotest.property.Arb
import io.kotest.property.resolution.default
import kotlin.reflect.KClass

@Deprecated("This logic has moved to io.kotest.property.resolution.Resolve and this function will be removed in 5.6. Since 5.5")
inline fun <reified A> Arb.Companion.default(): Arb<A> = default()

@Suppress("UNCHECKED_CAST")
@Deprecated("This logic has moved to ArbResolver and this function will be removed in 5.6. Since 5.5")
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

@Deprecated("This logic has moved to ArbResolver and this function will be removed in 5.6. Since 5.5")
expect inline fun <reified A> targetDefaultForClass(): Arb<A>?

@Suppress("UNCHECKED_CAST")
@Deprecated("This logic has moved to ArbResolver and this function will be removed in 5.6. Since 5.5")
inline fun <reified A> Arb.Companion.forClass(kClass: KClass<*>): Arb<A> =
   defaultForClass(kClass) as Arb<A>?
      ?: targetDefaultForClass()
      ?: throw NoGeneratorFoundException("Cannot infer generator for ${A::class}; specify generators explicitly")

@Deprecated("Package change. Use io.kotest.property.resolution.NoGeneratorFoundException and this alias will be removed in 5.6. Since 5.5")
typealias NoGeneratorFoundException = io.kotest.property.resolution.NoGeneratorFoundException
