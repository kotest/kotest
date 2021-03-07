package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import kotlin.reflect.KClass
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
inline fun <reified T : Any> Arb.Companion.bind(edgecaseDeterminism: Double = 0.9): Arb<T> {
   val kclass = T::class
   val constructor = kclass.primaryConstructor ?: error("could not locate a primary constructor")
   check(constructor.parameters.isNotEmpty()) { "${kclass.qualifiedName} constructor must contain at least 1 parameter" }

   val arbs: List<Arb<Any>> = constructor.parameters.map { param ->
      defaultForClass<Any>(param.type.classifier as KClass<*>)
         ?: error("Could not locate generator for parameter ${kclass.qualifiedName}.${param.name}")
   }

   return object : Arb<T>() {
      override fun edgecases(): List<T> = emptyList()
      override fun generateEdgecase(rs: RandomSource): T =
         arbs
            .map { arbParam ->
               val p = rs.random.nextDouble(0.0, 1.0)
               if (p < edgecaseDeterminism) arbParam.generateEdgecase(rs) else arbParam.single(rs)
            }
            .let { params -> constructor.call(*params.toTypedArray()) }

      override fun sample(rs: RandomSource): Sample<T> =
         Sample(constructor.call(*arbs.map { it.single(rs) }.toTypedArray()))

      override fun values(rs: RandomSource): Sequence<Sample<T>> = generateSequence { sample(rs) }
   }
}
