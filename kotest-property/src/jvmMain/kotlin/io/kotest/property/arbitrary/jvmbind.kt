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
 * Note: This method only supports "basic" parameter types - string, boolean and so on.
 * If your class has more complex requirements, you can use Arb.bind(gen1, gen2...) where
 * the parameter generators are supplied programatically.
 */
inline fun <reified T : Any> Arb.Companion.bind(providedArbs: Map<KClass<*>, Arb<*>> = emptyMap()): Arb<T> =
   bind(providedArbs, T::class)

/**
 * Alias for [Arb.Companion.bind]
 */
inline fun <reified T : Any> Arb.Companion.data(providedArbs: Map<KClass<*>, Arb<*>> = emptyMap()): Arb<T> =
   Arb.bind(providedArbs)

/**
 * Returns an [Arb] where each value is a randomly created instance of [T].
 * These instances are created by selecting the primaryConstructor of T and then
 * auto-detecting a generator for each parameter of that constructor.
 * T must be a class type.
 *
 * Note: This method only supports "basic" parameter types - string, boolean and so on.
 * If your class has more complex requirements, you can use Arb.bind(gen1, gen2...) where
 * the parameter generators are supplied programatically.
 */
fun <T : Any> Arb.Companion.bind(providedArbs: Map<KClass<*>, Arb<*>>, kclass: KClass<T>): Arb<T> {
   val constructor = kclass.primaryConstructor ?: error("could not locate a primary constructor")
   check(constructor.parameters.isNotEmpty()) { "${kclass.qualifiedName} constructor must contain at least 1 parameter" }

   val arbs: List<Arb<*>> = constructor.parameters.map { param ->
      Arb.forType(providedArbs, param.type)
         ?: error("Could not locate generator for parameter ${kclass.qualifiedName}.${param.name}")
   }

   return Arb.bind(arbs) { params -> constructor.call(*params.toTypedArray()) }
}

internal fun Arb.Companion.forType(providedArbs: Map<KClass<*>, Arb<*>>, type: KType): Arb<*>? {
   return (type.classifier as? KClass<*>)?.let { providedArbs[it] ?: defaultForClass(it) }
      ?: targetDefaultForType(providedArbs, type)
}
