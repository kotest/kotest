package io.kotest.property.arbitrary

@Suppress("UNCHECKED_CAST")
inline fun <reified A> Arb.Companion.default(): Arb<A> {
   val classname = A::class.simpleName ?: "<unknown>"
   return forClassName(classname) as Arb<A>
}

fun Arb.Companion.forClassName(className: String): Arb<*> {
   return when (className) {
      "java.lang.Integer", "kotlin.Int", "Int" -> Arb.ints()
      "java.lang.Long", "kotlin.Long", "Long" -> Arb.longs()
      "java.lang.Float", "kotlin.Float", "Float" -> Arb.floats()
      "java.lang.Double", "kotlin.Double", "Double" -> Arb.doubles()
      else -> throw IllegalArgumentException("Cannot infer generator for $className; specify generators explicitly")
   }
}
