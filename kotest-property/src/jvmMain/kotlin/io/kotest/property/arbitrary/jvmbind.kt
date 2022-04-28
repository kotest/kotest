package io.kotest.property.arbitrary

import io.kotest.property.Arb
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor

/**
 * Returns an [Arb] where each value is a randomly created instance of [T].
 * These instances are created by selecting the primaryConstructor of T and then
 * auto-detecting a generator for each parameter of that constructor.
 * T must be a class type.
 *
 * [providedArbs] allows you to provide a mapping of [KClass] to [Arb], for any given class [T].
 * This can be used to control parts of the data generation, as well as including non-data classes in the
 * hierarchies for which an [Arb] is being derived.
 *
 * Note: This method only supports parameters the following kind of parameter:
 * - Data classes, where all properties also fall into this category
 * - Pair, where 1st and 2nd fall into this category
 * - Primitives
 * - LocalDate, LocalDateTime, LocalTime, Period
 * - BigDecimal
 * - Collections (Set, List, Map)
 * - Classes for which an [Arb] has been provided through [providedArbs]
 */
inline fun <reified T : Any> Arb.Companion.bind(providedArbs: Map<KClass<*>, Arb<*>> = emptyMap()): Arb<T> =
   bind(providedArbs, T::class)

/**
 * Alias for [Arb.Companion.bind]
 *
 * Returns an [Arb] where each value is a randomly created instance of [T].
 * These instances are created by selecting the primaryConstructor of T and then
 * auto-detecting a generator for each parameter of that constructor.
 * T must be a class type.
 *
 * [providedArbs] allows you to provide a mapping of [KClass] to [Arb], for any given class [T].
 * This can be used to control parts of the data generation, as well as including non-data classes in the
 * hierarchies for which an [Arb] is being derived.
 *
 * Note: This method only supports parameters the following kind of parameter:
 * - Data classes, where all properties also fall into this category
 * - Pair, where 1st and 2nd fall into this category
 * - Primitives
 * - LocalDate, LocalDateTime, LocalTime, Period
 * - BigDecimal
 * - Collections (Set, List, Map)
 */
inline fun <reified T : Any> Arb.Companion.data(providedArbs: Map<KClass<*>, Arb<*>> = emptyMap()): Arb<T> =
   Arb.bind(providedArbs)

/**
 * Returns an [Arb] where each value is a randomly created instance of [T].
 * These instances are created by selecting the primaryConstructor of T and then
 * auto-detecting a generator for each parameter of that constructor.
 * T must be a class type.
 *
 * [providedArbs] allows you to provide a mapping of [KClass] to [Arb], for any given class [T].
 * This can be used to control parts of the data generation, as well as including non-data classes in the
 * hierarchies for which an [Arb] is being derived.
 *
 * Note: This method only supports parameters the following kind of parameter:
 * - Data classes, where all properties also fall into this category
 * - Pair, where 1st and 2nd fall into this category
 * - Primitives
 * - Enums
 * - LocalDate, LocalDateTime, LocalTime, Period, Instant
 * - BigDecimal, BigInteger
 * - Collections (Set, List, Map)
 */
fun <T : Any> Arb.Companion.bind(providedArbs: Map<KClass<*>, Arb<*>>, kclass: KClass<T>): Arb<T> {
   val constructor = kclass.primaryConstructor ?: error("could not locate a primary constructor")
   check(constructor.parameters.isNotEmpty()) { "${kclass.qualifiedName} constructor must contain at least 1 parameter" }

   val arbs: List<Arb<*>> = constructor.parameters.map { param ->
      val arb = Arb.forType(providedArbs, param.type)
         ?: error("Could not locate generator for parameter ${kclass.qualifiedName}.${param.name}")
      if (param.type.isMarkedNullable) arb.orNull() else arb
   }

   return Arb.bind(arbs) { params -> constructor.call(*params.toTypedArray()) }
}

internal fun Arb.Companion.forType(providedArbs: Map<KClass<*>, Arb<*>>, type: KType): Arb<*>? {
   return (type.classifier as? KClass<*>)?.let { providedArbs[it] ?: defaultForClass(it) }
      ?: targetDefaultForType(providedArbs, type)
}
