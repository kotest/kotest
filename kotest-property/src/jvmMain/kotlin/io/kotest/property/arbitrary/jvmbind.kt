package io.kotest.property.arbitrary

import io.kotest.property.Arb
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.KVisibility
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.typeOf

/**
 * Returns an [Arb] where each value is a randomly created instance of [T].
 *
 * [providedArbs] allows you to provide a mapping of [KClass] to [Arb], for any given class [T].
 * This can be used to control parts of the data generation, as well as including non-data classes in the
 * hierarchies for which an [Arb] is being derived.
 *
 * Note: This method only supports [Arb]s for the following types:
 * - Non-private classes with non-private primary constructor, where its parameters also fall into this category
 * - Pair, where 1st and 2nd fall into this category
 * - Primitives
 * - Enums
 * - LocalDate, LocalDateTime, LocalTime, Period
 * - BigDecimal
 * - Collections (Set, List, Map) of types that fall into this category
 * - Classes for which an [Arb] has been provided through [providedArbs]
 */
inline fun <reified T : Any> Arb.Companion.bind(providedArbs: Map<KClass<*>, Arb<*>> = emptyMap()): Arb<T> =
   bind(providedArbs, T::class, typeOf<T>())

/**
 * Alias for [Arb.Companion.bind]
 *
 * Returns an [Arb] where each value is a randomly created instance of [T].
 *
 * [providedArbs] allows you to provide a mapping of [KClass] to [Arb], for any given class [T].
 * This can be used to control parts of the data generation, as well as including non-data classes in the
 * hierarchies for which an [Arb] is being derived.
 *
 * Note: This method only supports [Arb]s for the following types:
 * - Non-private classes with non-private primary constructor, where its parameters also fall into this category
 * - Pair, where 1st and 2nd fall into this category
 * - Primitives
 * - Enums
 * - LocalDate, LocalDateTime, LocalTime, Period
 * - BigDecimal
 * - Collections (Set, List, Map) of types that fall into this category
 * - Classes for which an [Arb] has been provided through [providedArbs]
 */
inline fun <reified T : Any> Arb.Companion.data(providedArbs: Map<KClass<*>, Arb<*>> = emptyMap()): Arb<T> =
   Arb.bind(providedArbs)

/**
 * **Do not call directly**
 *
 * Callers should use [Arb.Companion.bind] without [KClass] and [KType] parameters instead.
 */
fun <T : Any> Arb.Companion.bind(providedArbs: Map<KClass<*>, Arb<*>>, kclass: KClass<T>, type: KType): Arb<T> {
   val arb = Arb.forType(providedArbs, type)
      ?: error("Could not locate generator for ${kclass.simpleName}, consider making it a dataclass or provide an Arb for it.")
   return arb as Arb<T>
}

/**
 * **Do not call directly**
 *
 * Callers should use [Arb.Companion.bind] without [KClass] and [KType] parameters instead.
 */
@Deprecated(
   "Superceded by bind without KClass parameter.",
   ReplaceWith("bind(providedArbs)")
)
fun <T : Any> Arb.Companion.bind(providedArbs: Map<KClass<*>, Arb<*>>, kclass: KClass<T>): Arb<T> {
   return forClassUsingConstructor(providedArbs, kclass)
}

internal fun <T : Any> Arb.Companion.forClassUsingConstructor(
   providedArbs: Map<KClass<*>, Arb<*>>,
   kclass: KClass<T>
): Arb<T> {
   val className = kclass.qualifiedName ?: kclass.simpleName
   val constructor = kclass.primaryConstructor ?: error("Could not locate a primary constructor for $className")
   check(kclass.visibility != KVisibility.PRIVATE) { "The class $className must be public." }
   check(constructor.visibility != KVisibility.PRIVATE) { "The primary constructor of $className must be public." }

   if (constructor.parameters.isEmpty()) {
      return Arb.constant(constructor.call())
   }

   val arbs: List<Arb<*>> = constructor.parameters.map { param ->
      val arb = arbForParameter(providedArbs, className, param)
      if (param.type.isMarkedNullable) arb.orNull() else arb
   }

   return Arb.bind(arbs) { params -> constructor.call(*params.toTypedArray()) }
}

private fun arbForParameter(providedArbs: Map<KClass<*>, Arb<*>>, className: String?, param: KParameter): Arb<*> {
   val arb =
      try {
         Arb.forType(providedArbs, param.type)
      } catch (e: IllegalStateException) {
         throw IllegalStateException("Failed to create generator for parameter $className.${param.name}", e)
      }
   return arb ?: error("Could not locate generator for parameter $className.${param.name}")
}

internal fun Arb.Companion.forType(providedArbs: Map<KClass<*>, Arb<*>>, type: KType): Arb<*>? {
   return (type.classifier as? KClass<*>)?.let { providedArbs[it] ?: defaultForClass(it) }
      ?: targetDefaultForType(providedArbs, type)
}
