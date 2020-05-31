package io.kotest.properties

import io.kotest.mpp.bestName

@Deprecated("Deprecated and will be removed in 4.2. Migrate to the new property test classes in 4.0")
inline fun <reified T> Gen.Companion.default(): Gen<T> {
   val classname = T::class.bestName()
   val gen = forCommonClassName(classname)
      ?: forPlatformClassName<T>(classname)
      ?: throw IllegalArgumentException("Cannot infer generator for $classname; specify generators explicitly")
   return gen as Gen<T>
}

@Deprecated("Deprecated and will be removed in 4.2. Migrate to the new property test classes in 4.0")
expect inline fun <reified T> forPlatformClassName(className: String): Gen<T>?

@Deprecated("Deprecated and will be removed in 4.2. Migrate to the new property test classes in 4.0")
fun forCommonClassName(className: String): Gen<*>? {
   return when (className) {
      "java.lang.String", "kotlin.String", "String" -> Gen.string()
      "java.lang.Integer", "kotlin.Int", "Int" -> Gen.int()
      "java.lang.Short", "kotlin.Short", "Short" -> Gen.short()
      "java.lang.Byte", "kotlin.Byte", "Byte" -> Gen.byte()
      "java.lang.Long", "kotlin.Long", "Long" -> Gen.long()
      "java.lang.Boolean", "kotlin.Boolean", "Boolean" -> Gen.bool()
      "java.lang.Float", "kotlin.Float", "Float" -> Gen.float()
      "java.lang.Double", "kotlin.Double", "Double" -> Gen.double()
      else -> null
   }
}
