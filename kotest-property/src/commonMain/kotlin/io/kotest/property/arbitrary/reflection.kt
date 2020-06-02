package io.kotest.property.arbitrary

import io.kotest.mpp.bestName
import io.kotest.property.Arb
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
inline fun <reified A> Arb.Companion.default(): Arb<A> = forClass(A::class)

@Suppress("UNCHECKED_CAST")
fun <A> defaultForClass(kClass: KClass<*>): Arb<A>? {
   return when (kClass.bestName()) {
      "java.lang.String", "kotlin.String", "String" -> Arb.string() as Arb<A>
      "java.lang.Character", "kotlin.Char", "Char" -> Arb.char() as Arb<A>
      "java.lang.Long", "kotlin.Long", "Long" -> Arb.long() as Arb<A>
      "java.lang.Integer", "kotlin.Int", "Int" -> Arb.int() as Arb<A>
      "java.lang.Short", "kotlin.Short", "Short" -> Arb.short() as Arb<A>
      "java.lang.Byte", "kotlin.Byte", "Byte" -> Arb.byte() as Arb<A>
      "java.lang.Double", "kotlin.Double", "Double" -> Arb.double() as Arb<A>
      "java.lang.Float", "kotlin.Float", "Float" -> Arb.float() as Arb<A>
      "java.lang.Boolean", "kotlin.Boolean", "Boolean" -> Arb.bool() as Arb<A>
      else -> null
   }
}

expect inline fun <reified A> targetDefaultForClass(): Arb<A>?

inline fun <reified A> Arb.Companion.forClass(kClass: KClass<*>): Arb<A> =
   defaultForClass(kClass)
      ?: targetDefaultForClass()
      ?: throw NoGeneratorFoundException("Cannot infer generator for ${A::class}; specify generators explicitly")

class NoGeneratorFoundException(msg: String) : RuntimeException(msg)
