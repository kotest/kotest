package io.kotest.properties

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType

@Suppress("UNCHECKED_CAST")
actual inline fun <reified T> Gen.Companion.default(): Gen<T> {
  return when (T::class.qualifiedName) {
    List::class.qualifiedName -> {
      val type = object : TypeReference<T>() {}.type as ParameterizedType
      val first = type.actualTypeArguments.first() as WildcardType
      val upper = first.upperBounds.first() as Class<*>
      list(forClassName(upper.name) as Gen<Any>) as Gen<T>
    }
    Set::class.qualifiedName -> {
      val type = object : TypeReference<T>() {}.type as ParameterizedType
      val first = type.actualTypeArguments.first() as WildcardType
      val upper = first.upperBounds.first() as Class<*>
      set(forClassName(upper.name) as Gen<Any>) as Gen<T>
    }
    Pair::class.qualifiedName -> {
      val type = object : TypeReference<T>() {}.type as ParameterizedType
      val first = (type.actualTypeArguments[0] as WildcardType).upperBounds.first() as Class<*>
      val second = (type.actualTypeArguments[1] as WildcardType).upperBounds.first() as Class<*>
      pair(forClassName(first.name), forClassName(second.name)) as Gen<T>
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
      map(forClassName(first.name), forClassName(second.name)) as Gen<T>
    }
    else -> Gen.forClassName(T::class.qualifiedName!!) as Gen<T>
  }
}

fun Gen.Companion.forClassName(className: String): Gen<*> {
  return when (className) {
    "java.lang.String", "kotlin.String" -> Gen.string()
    "java.lang.Integer", "kotlin.Int" -> Gen.int()
    "java.lang.Short", "kotlin.Short" -> Gen.short()
    "java.lang.Byte", "kotlin.Byte" -> Gen.byte()
    "java.lang.Long", "kotlin.Long" -> Gen.long()
    "java.lang.Boolean", "kotlin.Boolean" -> Gen.bool()
    "java.lang.Float", "kotlin.Float" -> Gen.float()
    "java.lang.Double", "kotlin.Double" -> Gen.double()
    "java.util.UUID" -> Gen.uuid()
    "java.io.File" -> Gen.file()
    "java.time.LocalDate" -> Gen.localDate()
    "java.time.LocalDateTime" -> Gen.localDateTime()
    "java.time.LocalTime" -> Gen.localTime()
    "java.time.Duration" -> Gen.duration()
    "java.time.Period" -> Gen.period()
    else -> throw IllegalArgumentException("Cannot infer generator for $className; specify generators explicitly")
  }
}

// need some supertype that types a type param so it gets baked into the class file
abstract class TypeReference<T> : Comparable<TypeReference<T>> {
  val type: Type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
  override fun compareTo(other: TypeReference<T>) = 0
}
