package io.kotest.property.arbitrary.reflective

import io.kotest.common.DelicateKotest
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.orNull
import io.kotest.property.resolution.CommonTypeArbResolver
import io.kotest.property.resolution.GlobalArbResolver
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.KVisibility
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.kotlinFunction
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
 * - Sealed classes
 * - LocalDate, LocalDateTime, LocalTime, Period
 * - BigDecimal
 * - Collections (Set, List, Map) of types that fall into this category
 * - Classes for which an [Arb] has been provided through [providedArbs]
 */
inline fun <reified T : Any> Arb.Companion.bind(reflectiveBindConfiguration: ReflectiveBindConfiguration): Arb<T> =
   bind(reflectiveBindConfiguration, T::class, typeOf<T>())

inline fun <reified T : Any> Arb.Companion.bind(builder: ReflectiveBindConfigurationBuilder.() -> Unit): Arb<T> =
   ReflectiveBindConfigurationBuilder().apply(builder).run {
      bind(classArbs, propertyArbs, T::class, typeOf<T>())
   }

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
 * - Sealed classes
 * - LocalDate, LocalDateTime, LocalTime, Period
 * - BigDecimal
 * - Collections (Set, List, Map) of types that fall into this category
 * - Classes for which an [Arb] has been provided through [providedArbs]
 */
inline fun <reified T : Any> Arb.Companion.data(reflectiveBindConfiguration: ReflectiveBindConfiguration): Arb<T> =
   Arb.Companion.bind(reflectiveBindConfiguration)

inline fun <reified T : Any> Arb.Companion.data(builder: ReflectiveBindConfigurationBuilder.() -> Unit): Arb<T> =
   ReflectiveBindConfigurationBuilder().apply(builder).build().let(::bind)

/**
 * **Do not call directly**
 *
 * Callers should use [Arb.Companion.bind] without [KClass] and [KType] parameters instead.
 */
fun <T : Any> Arb.Companion.bind(
   reflectiveBindConfiguration: ReflectiveBindConfiguration,
   kclass: KClass<T>,
   type: KType
): Arb<T> {
   val arb = Arb.Companion.forType(reflectiveBindConfiguration, type)
      ?: error("Could not locate generator for ${kclass.simpleName}, consider making it a dataclass or provide an Arb for it.")
   @Suppress("UNCHECKED_CAST")
   return arb as Arb<T>
}

/**
 * Most callers should use [Arb.Companion.bind] without [KClass] and [KType] parameters instead.
 *
 * This method's primary use-case is for generating [Arb]s for types that are not available at compile-time.
 * See [#3362](https://github.com/kotest/kotest/issues/3362) for an example of such usage.
 */
@DelicateKotest
fun <T : Any> Arb.Companion.bind(
   reflectiveBindConfiguration: ReflectiveBindConfiguration,
   kclass: KClass<T>
): Arb<T> {
   return forClassUsingConstructor(reflectiveBindConfiguration, kclass)
}

internal fun <T : Any> Arb.Companion.forClassUsingConstructor(
   reflectiveBindConfiguration: ReflectiveBindConfiguration,
   kclass: KClass<T>
): Arb<T> {
   val className = kclass.qualifiedName ?: kclass.simpleName

   @Suppress("UNCHECKED_CAST")
   val constructor =
      reflectiveBindConfiguration.preferredClassContructors.firstOrNull { it.kclass == kclass }
         ?.let { it.constructor as? KFunction<T> }
         ?: kclass.primaryConstructor
         ?: (kclass.java.constructors.firstNotNullOfOrNull { it.kotlinFunction as? KFunction<T> })
         ?: error("Could not locate a primary constructor for $className")

   check(kclass.visibility != KVisibility.PRIVATE) { "The class $className must be public." }
   check(constructor.visibility != KVisibility.PRIVATE) { "The primary constructor of $className must be public." }

   if (constructor.parameters.isEmpty()) {
      return Arb.Companion.constant(constructor.call())
   }

   val relevantProps =
      reflectiveBindConfiguration.arbsForProps.filter { it.key.parameters[0].type == constructor.returnType }
         .toList()

   val arbs: List<Arb<*>> = constructor.parameters.map { param ->
      val arbForProp = relevantProps.firstOrNull { (key, _) -> key.name == param.name }?.second
      when {
         arbForProp != null -> arbForProp
         else -> {
            val arb = arbForParameter(reflectiveBindConfiguration, className, param)
            if (param.type.isMarkedNullable) arb.orNull() else arb
         }
      }

   }

   return Arb.bind(arbs) { params -> constructor.call(*params.toTypedArray()) }
}

private fun arbForParameter(
   reflectiveBindConfiguration: ReflectiveBindConfiguration,
   className: String?,
   param: KParameter
): Arb<*> {
   val arb =
      try {
         Arb.Companion.forType(reflectiveBindConfiguration, param.type)
      } catch (e: IllegalStateException) {
         throw IllegalStateException("Failed to create generator for parameter $className.${param.name}", e)
      }
   return arb ?: error("Could not locate generator for parameter $className.${param.name}")
}

internal fun Arb.Companion.forType(
   reflectiveBindConfiguration: ReflectiveBindConfiguration,
   type: KType
): Arb<*>? {
   return (type.classifier as? KClass<*>)
      ?.let { reflectiveBindConfiguration.providedArbs[it] ?: CommonTypeArbResolver.resolve(it.starProjectedType) }
      ?: GlobalArbResolver.resolve(type)
      ?: targetDefaultForType(reflectiveBindConfiguration, type)
}
