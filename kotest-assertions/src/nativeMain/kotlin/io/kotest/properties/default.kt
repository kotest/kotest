package io.kotest.properties

actual inline fun <reified T> Gen.Companion.default(): Gen<T> {
  val classname = T::class.simpleName ?: "<anon>"
  return forClassName(classname) as Gen<T>
}

fun forClassName(className: String): Gen<*> {
  return when (className) {
    "java.lang.String", "kotlin.String", "String" -> Gen.string()
    "java.lang.Integer", "kotlin.Int", "Int" -> Gen.int()
    "java.lang.Short", "kotlin.Short", "Short" -> Gen.short()
    "java.lang.Byte", "kotlin.Byte", "Byte" -> Gen.byte()
    "java.lang.Long", "kotlin.Long", "Long" -> Gen.long()
    "java.lang.Boolean", "kotlin.Boolean", "Boolean" -> Gen.bool()
    "java.lang.Float", "kotlin.Float", "Float" -> Gen.float()
    "java.lang.Double", "kotlin.Double", "Double" -> Gen.double()
    else -> throw IllegalArgumentException("Cannot infer generator for $className; specify generators explicitly")
  }
}
