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
import java.time.OffsetDateTime
import java.time.Period
import java.time.Year
import java.time.YearMonth
import java.time.ZonedDateTime
import java.util.Date
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.typeOf

fun targetDefaultForType(
   providedArbs: Map<KClass<*>, Arb<*>>,
   arbsForProps: Map<KProperty1<*, *>, Arb<*>>,
   type: KType
): Arb<*>? {
   when (type) {
      typeOf<Instant>(), typeOf<Instant?>() -> Arb.javaInstant()
      typeOf<Date>(), typeOf<Date?>() -> Arb.javaDate()
      typeOf<LocalDate>(), typeOf<LocalDate?>() -> Arb.localDate()
      typeOf<LocalDateTime>(), typeOf<LocalDateTime?>() -> Arb.localDateTime()
      typeOf<LocalTime>(), typeOf<LocalTime?>() -> Arb.localTime()
      typeOf<Period>(), typeOf<Period?>() -> Arb.period()
      typeOf<Year>(), typeOf<Year?>() -> Arb.year()
      typeOf<YearMonth>(), typeOf<YearMonth?>() -> Arb.yearMonth()
      typeOf<ZonedDateTime>(), typeOf<ZonedDateTime?>() -> Arb.zonedDateTime()
      typeOf<OffsetDateTime>(), typeOf<OffsetDateTime?>() -> Arb.offsetDateTime()
      typeOf<BigDecimal>(), typeOf<BigDecimal?>() -> Arb.bigDecimal()
      typeOf<BigInteger>(), typeOf<BigInteger?>() -> Arb.bigInt(maxNumBits = 256)
      else -> null
   }?.let { return it }

   val clazz = type.classifier as? KClass<*> ?: return null
   return when {
      clazz.isSubclassOf(List::class) -> {
         val upperBound = type.arguments.first().type ?: error("No bound for List")
         Arb.list(Arb.forType(providedArbs, arbsForProps, upperBound) as Arb<*>)
      }
      clazz.java.isArray -> {
         val upperBound = type.arguments.first().type ?: error("No bound for Array")
         Arb.array(Arb.forType(providedArbs, arbsForProps, upperBound) as Arb<*>) {
            val upperBoundKClass = (upperBound.classifier as? KClass<*>) ?: error("No classifier for $upperBound")
            @Suppress("UNCHECKED_CAST")
            val array = java.lang.reflect.Array.newInstance(upperBoundKClass.javaObjectType, this.size) as Array<Any?>
            for ((i, item) in this.withIndex()) {
               java.lang.reflect.Array.set(array, i, item)
            }
            array
         }
      }
      clazz.isSubclassOf(Set::class) -> {
         val upperBound = type.arguments.first().type ?: error("No bound for Set")
         val upperBoundKClass = (upperBound.classifier as? KClass<*>)
         if (upperBoundKClass != null && upperBoundKClass.isSubclassOf(Enum::class)) {
            val maxElements = Class.forName(upperBoundKClass.java.name).enumConstants.size
            Arb.set(Arb.forType(providedArbs, arbsForProps, upperBound) as Arb<*>, 0..maxElements)
         } else if(upperBoundKClass != null && upperBoundKClass.isSealed) {
            val maxElements = upperBoundKClass.sealedSubclasses.size
            Arb.set(Arb.forType(providedArbs, arbsForProps, upperBound) as Arb<*>, 0..maxElements)
         } else {
            Arb.set(Arb.forType(providedArbs, arbsForProps, upperBound) as Arb<*>)
         }
      }
      clazz.isSubclassOf(Pair::class) -> {
         val first = type.arguments[0].type ?: error("No bound for first type parameter of Pair")
         val second = type.arguments[1].type ?: error("No bound for second type parameter of Pair")
         Arb.pair(Arb.forType(providedArbs, arbsForProps, first)!!, Arb.forType(providedArbs, arbsForProps, second)!!)
      }
      clazz.isSubclassOf(Map::class) -> {
         // map key type can have or have not variance
         val first = type.arguments[0].type ?: error("No bound for first type parameter of Map<K, V>")
         val second = type.arguments[1].type ?: error("No bound for second type parameter of Map<K, V>")
         Arb.map(Arb.forType(providedArbs, arbsForProps, first)!!, Arb.forType(providedArbs, arbsForProps, second)!!)
      }
      clazz.isSubclassOf(Enum::class) -> {
         Arb.of(Class.forName(clazz.java.name).enumConstants.map { it as Enum<*> })
      }
      clazz.objectInstance != null -> Arb.constant(clazz.objectInstance!!)
      clazz.isSealed -> {
         Arb.choice(clazz.sealedSubclasses.map { subclass ->
            subclass.objectInstance?.let { Arb.constant(it) } ?: Arb.forClassUsingConstructor(providedArbs, arbsForProps, subclass)
         })
      }
      else -> {
        Arb.forClassUsingConstructor(providedArbs, arbsForProps, clazz)
      }
   }
}

// need some supertype that types a type param so it gets baked into the class file
abstract class TypeReference<T> : Comparable<TypeReference<T>> {
   // this is the type of T
   val type: Type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]

   override fun compareTo(other: TypeReference<T>) = 0
}
