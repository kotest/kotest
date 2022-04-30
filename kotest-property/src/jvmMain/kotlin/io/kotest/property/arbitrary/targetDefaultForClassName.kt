package io.kotest.property.arbitrary

import io.kotest.property.Arb
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.superclasses
import kotlin.reflect.typeOf

@Suppress("UNCHECKED_CAST")
actual inline fun <reified A> targetDefaultForClass(): Arb<A>? = targetDefaultForType(type = typeOf<A>()) as Arb<A>?

fun targetDefaultForType(providedArbs: Map<KClass<*>, Arb<*>> = emptyMap(), type: KType): Arb<*>? {
   val clazz = type.classifier as? KClass<*>
   return when {
      clazz?.isSubclassOf(List::class) == true -> {
         val upperBound = type.arguments.first().type ?: error("No bound for List")
         Arb.list(Arb.forType(providedArbs, upperBound) as Arb<Any>)
      }
      clazz?.isSubclassOf(Set::class) == true -> {
         val upperBound = type.arguments.first().type ?: error("No bound for Set")
         Arb.set(Arb.forType(providedArbs, upperBound) as Arb<Any>)
      }
      clazz?.isSubclassOf(Pair::class) == true -> {
         val first = type.arguments[0].type ?: error("No bound for first type parameter of Pair")
         val second = type.arguments[1].type ?: error("No bound for second type parameter of Pair")
         Arb.pair(Arb.forType(providedArbs, first)!!, Arb.forType(providedArbs, second)!!)
      }
      clazz?.isSubclassOf(Map::class) == true -> {
         // map key type can have or have not variance
         val first = type.arguments[0].type ?: error("No bound for first type parameter of Map<K, V>")
         val second = type.arguments[1].type ?: error("No bound for second type parameter of Map<K, V>")
         Arb.map(Arb.forType(providedArbs, first)!!, Arb.forType(providedArbs, second)!!)
      }
      clazz?.isSubclassOf(Enum::class) == true -> {
         Arb.of(Class.forName(clazz.java.name).enumConstants.map { it as Enum<*> })
      }
      type == typeOf<Instant>() -> Arb.instant()
      type == typeOf<LocalDate>() -> Arb.localDate()
      type == typeOf<LocalDateTime>() -> Arb.localDateTime()
      type == typeOf<LocalTime>() -> Arb.localTime()
      type == typeOf<Period>() -> Arb.period()
      type == typeOf<BigDecimal>() -> Arb.bigDecimal()
      type == typeOf<BigInteger>() -> Arb.bigInt(maxNumBits = 256)
      clazz?.isData == true -> {
         val k = clazz as KClass<Any>
         Arb.bind(providedArbs, k)
      }
      else -> null
   }
}

// need some supertype that types a type param so it gets baked into the class file
abstract class TypeReference<T> : Comparable<TypeReference<T>> {
   // this is the type of T
   val type: Type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]

   override fun compareTo(other: TypeReference<T>) = 0
}
