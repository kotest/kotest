package io.kotest.property.arbitrary

@Suppress("UNCHECKED_CAST")
inline fun <reified A> Arb.Companion.default(): Arb<A> {
   val classname = A::class.simpleName ?: "<unknown>"
   return forClassName(classname) as Arb<A>
}

fun Arb.Companion.forClassName(className: String): Arb<*> {
   return when (className) {
      "java.lang.String", "kotlin.String", "String" -> Arb.string()
      "java.lang.Integer", "kotlin.Int", "Int" -> Arb.int()
      "java.lang.Long", "kotlin.Long", "Long" -> Arb.long()
      "java.lang.Float", "kotlin.Float", "Float" -> Arb.float()
      "java.lang.Double", "kotlin.Double", "Double" -> Arb.double()
      "java.lang.Byte", "kotlin.Byte", "Byte" -> Arb.byte()
      else -> throw IllegalArgumentException("Cannot infer generator for $className; specify generators explicitly")
   }
}
