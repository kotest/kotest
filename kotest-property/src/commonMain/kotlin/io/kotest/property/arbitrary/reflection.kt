package io.kotest.property.arbitrary

import io.kotest.mpp.bestName

@Suppress("UNCHECKED_CAST")
inline fun <reified A> Arb.Companion.default(): Arb<A> {
   val classname = A::class.bestName()
   return forClassName(classname) as Arb<A>
}

fun defaultForClassName(className: String): Arb<*>? {
   return when (className) {
      "java.lang.String", "kotlin.String", "String" -> Arb.string()
      "java.lang.Integer", "kotlin.Int", "Int" -> Arb.int()
      "java.lang.Long", "kotlin.Long", "Long" -> Arb.long()
      "java.lang.Float", "kotlin.Float", "Float" -> Arb.float()
      "java.lang.Double", "kotlin.Double", "Double" -> Arb.double()
      "java.lang.Byte", "kotlin.Byte", "Byte" -> Arb.byte()
      else -> null
   }
}

expect fun targetDefaultForClassName(className: String): Arb<*>?

fun Arb.Companion.forClassName(className: String): Arb<*> =
   defaultForClassName(className)
      ?: targetDefaultForClassName(className)
      ?: throw IllegalArgumentException("Cannot infer generator for $className; specify generators explicitly")
