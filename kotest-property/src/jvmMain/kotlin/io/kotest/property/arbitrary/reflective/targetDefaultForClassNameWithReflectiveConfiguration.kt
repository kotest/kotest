package io.kotest.property.arbitrary.reflective

import io.kotest.property.Arb
import io.kotest.property.arbitrary.array
import io.kotest.property.arbitrary.bigDecimal
import io.kotest.property.arbitrary.bigInt
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.instant
import io.kotest.property.arbitrary.javaDate
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.localDate
import io.kotest.property.arbitrary.localDateTime
import io.kotest.property.arbitrary.localTime
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.offsetDateTime
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.period
import io.kotest.property.arbitrary.set
import io.kotest.property.arbitrary.year
import io.kotest.property.arbitrary.yearMonth
import io.kotest.property.arbitrary.zonedDateTime
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
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.typeOf

fun targetDefaultForType(
   reflectiveBindConfiguration: ReflectiveBindConfiguration,
   type: KType
): Arb<*>? {
   when (type) {
      typeOf<Instant>(), typeOf<Instant?>() -> Arb.Companion.instant()
      typeOf<Date>(), typeOf<Date?>() -> Arb.Companion.javaDate()
      typeOf<LocalDate>(), typeOf<LocalDate?>() -> Arb.Companion.localDate()
      typeOf<LocalDateTime>(), typeOf<LocalDateTime?>() -> Arb.Companion.localDateTime()
      typeOf<LocalTime>(), typeOf<LocalTime?>() -> Arb.Companion.localTime()
      typeOf<Period>(), typeOf<Period?>() -> Arb.Companion.period()
      typeOf<Year>(), typeOf<Year?>() -> Arb.Companion.year()
      typeOf<YearMonth>(), typeOf<YearMonth?>() -> Arb.Companion.yearMonth()
      typeOf<ZonedDateTime>(), typeOf<ZonedDateTime?>() -> Arb.Companion.zonedDateTime()
      typeOf<OffsetDateTime>(), typeOf<OffsetDateTime?>() -> Arb.Companion.offsetDateTime()
      typeOf<BigDecimal>(), typeOf<BigDecimal?>() -> Arb.Companion.bigDecimal()
      typeOf<BigInteger>(), typeOf<BigInteger?>() -> Arb.Companion.bigInt(maxNumBits = 256)
      else -> null
   }?.let { return it }

   val clazz = type.classifier as? KClass<*> ?: return null
   return when {
      clazz.isSubclassOf(List::class) -> {
         val upperBound = type.arguments.first().type ?: error("No bound for List")
         Arb.Companion.list(Arb.forType(reflectiveBindConfiguration, upperBound) as Arb<*>)
      }
      clazz.java.isArray -> {
         val upperBound = type.arguments.first().type ?: error("No bound for Array")
         Arb.Companion.array(Arb.forType(reflectiveBindConfiguration, upperBound) as Arb<*>) {
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
            Arb.set(Arb.forType(reflectiveBindConfiguration, upperBound) as Arb<*>, 0..maxElements)
         } else if(upperBoundKClass != null && upperBoundKClass.isSealed) {
            val maxElements = upperBoundKClass.sealedSubclasses.size
            Arb.set(Arb.forType(reflectiveBindConfiguration, upperBound) as Arb<*>, 0..maxElements)
         } else {
            Arb.set(Arb.forType(reflectiveBindConfiguration, upperBound) as Arb<*>)
         }
      }
      clazz.isSubclassOf(Pair::class) -> {
         val first = type.arguments[0].type ?: error("No bound for first type parameter of Pair")
         val second = type.arguments[1].type ?: error("No bound for second type parameter of Pair")
         Arb.Companion.pair(Arb.forType(reflectiveBindConfiguration, first)!!, Arb.forType(reflectiveBindConfiguration, second)!!)
      }
      clazz.isSubclassOf(Map::class) -> {
         // map key type can have or have not variance
         val first = type.arguments[0].type ?: error("No bound for first type parameter of Map<K, V>")
         val second = type.arguments[1].type ?: error("No bound for second type parameter of Map<K, V>")
         Arb.Companion.map(Arb.forType(reflectiveBindConfiguration, first)!!, Arb.forType(reflectiveBindConfiguration, second)!!)
      }
      clazz.isSubclassOf(Enum::class) -> {
         Arb.Companion.of(Class.forName(clazz.java.name).enumConstants.map { it as Enum<*> })
      }
      clazz.objectInstance != null -> Arb.Companion.constant(clazz.objectInstance!!)
      clazz.isSealed -> {
         Arb.Companion.choice(clazz.sealedSubclasses.map { subclass ->
            subclass.objectInstance?.let { Arb.constant(it) } ?: Arb.forClassUsingConstructor(reflectiveBindConfiguration, subclass)
         })
      }
      else -> {
        Arb.forClassUsingConstructor(reflectiveBindConfiguration, clazz)
      }
   }
}
