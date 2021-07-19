package io.kotest.property.arbitrary

import io.kotest.property.Arb
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

actual inline fun <reified A : Any> Arb.Companion.default(): Arb<A> {

   val type = object : TypeReference<A>() {}.type as ParameterizedType
   val tparams = type.actualTypeArguments.map { it as WildcardType }.map { it.upperBounds.first() as Class<*> }

   return defaultForClass(A::class)
      ?: targetDefaultForClass(A::class, tparams)
      ?: throw NoGeneratorFoundException("Cannot locate generator for ${A::class}; specify generators explicitly")
}

fun <A : Any> forClass(kclass: KClass<A>): Arb<A> {
   return defaultForClass(kclass)
      ?: targetDefaultForClass(kclass, emptyList())
      ?: throw NoGeneratorFoundException("Cannot locate generator for ${kclass}; specify generators explicitly")
}

@Suppress("UNCHECKED_CAST")
fun <A : Any> targetDefaultForClass(kclass: KClass<A>, tparams: List<Class<*>>): Arb<A>? {
   return when {
      kclass.isSubclassOf(List::class) -> Arb.list(defaultForClass<Any>(tparams.first().kotlin) as Arb<Any>) as Arb<A>
      kclass.isSubclassOf(Set::class) -> Arb.set(defaultForClass<Any>(tparams.first().kotlin) as Arb<Any>) as Arb<A>
      kclass.isSubclassOf(Pair::class) -> Arb.pair(
         defaultForClass<Any>(tparams[0].kotlin)!!,
         defaultForClass<Any>(tparams[1].kotlin)!!
      ) as Arb<A>
      kclass.isSubclassOf(Map::class) -> {
//          map key type can have or have not variance
//         val first = if (type.actualTypeArguments[0] is Class<*>) {
//            type.actualTypeArguments[0] as Class<*>
//         } else {
//            (type.actualTypeArguments[0] as WildcardType).upperBounds.first() as Class<*>
//         }
//         val second = (type.actualTypeArguments[1] as WildcardType).upperBounds.first() as Class<*>
         Arb.map(defaultForClass<Any>(tparams[0].kotlin)!!, defaultForClass<Any>(tparams[1].kotlin)!!) as Arb<A>
      }
      kclass == LocalDate::class -> Arb.localDate() as Arb<A>
      kclass == LocalDateTime::class -> Arb.localDateTime() as Arb<A>
      kclass == LocalTime::class -> Arb.localTime() as Arb<A>
      kclass == Period::class -> Arb.period() as Arb<A>
      kclass == BigDecimal::class -> Arb.bigDecimal() as Arb<A>
      kclass.isData -> forClass(kclass)
      else -> null
   }
}

// need some supertype that types a type param so it gets baked into the class file
abstract class TypeReference<T> : Comparable<TypeReference<T>> {
   // this is the type of T
   val type: Type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]

   override fun compareTo(other: TypeReference<T>) = 0
}
