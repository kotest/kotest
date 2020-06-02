package io.kotest.properties

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType

@Deprecated("Deprecated and will be removed in 4.2. Migrate to the new property test classes introduced in 4.0")
actual inline fun <reified T> forPlatformClassName(className: String): Gen<T>? {
   return when (className) {
      List::class.qualifiedName -> {
         val type = object : TypeReference<T>() {}.type as ParameterizedType
         val first = type.actualTypeArguments.first() as WildcardType
         val upper = first.upperBounds.first() as Class<*>
         return Gen.Companion.list(forCommonClassName(upper.name)!!) as Gen<T>
      }
      Set::class.qualifiedName -> {
         val type = object : TypeReference<T>() {}.type as ParameterizedType
         val first = type.actualTypeArguments.first() as WildcardType
         val upper = first.upperBounds.first() as Class<*>
         Gen.Companion.set(forCommonClassName(upper.name)!!) as Gen<T>
      }
      Pair::class.qualifiedName -> {
         val type = object : TypeReference<T>() {}.type as ParameterizedType
         val first = (type.actualTypeArguments[0] as WildcardType).upperBounds.first() as Class<*>
         val second = (type.actualTypeArguments[1] as WildcardType).upperBounds.first() as Class<*>
         Gen.Companion.pair(forCommonClassName(first.name)!!, forCommonClassName(second.name)!!) as Gen<T>
      }
      Map::class.qualifiedName -> {
         val type = object : TypeReference<T>() {}.type as ParameterizedType
         //map key type can have or have not variance
         val first = if (type.actualTypeArguments[0] is Class<*>) {
            type.actualTypeArguments[0] as Class<*>
         } else {
            (type.actualTypeArguments[0] as WildcardType).upperBounds.first() as Class<*>
         }
         val second = (type.actualTypeArguments[1] as WildcardType).upperBounds.first() as Class<*>
         Gen.Companion.map(forCommonClassName(first.name)!!, forCommonClassName(second.name)!!) as Gen<T>
      }
      "java.util.UUID" -> Gen.uuid() as Gen<T>
      "java.io.File" -> Gen.file() as Gen<T>
      "java.time.LocalDate" -> Gen.localDate() as Gen<T>
      "java.time.LocalDateTime" -> Gen.localDateTime() as Gen<T>
      "java.time.LocalTime" -> Gen.localTime() as Gen<T>
      "java.time.Duration" -> Gen.duration() as Gen<T>
      "java.time.Period" -> Gen.period() as Gen<T>
      else -> null
   }
}

// need some supertype that types a type param so it gets baked into the class file
abstract class TypeReference<T> : Comparable<TypeReference<T>> {
   val type: Type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
   override fun compareTo(other: TypeReference<T>) = 0
}
