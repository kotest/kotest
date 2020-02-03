package io.kotest.property.internal

import io.kotest.property.Gen
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyContext
import io.kotest.property.random
import kotlin.math.max

suspend fun <A> proptest(
   iterations: Int,
   genA: Gen<A>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A) -> Unit
): PropertyContext {

   require(iterations >= genA.minIterations()) { "Require at least $iterations iterations to cover requirements" }

   val context = PropertyContext()
   val random = config.seed.random()

   genA.generate(random)
      .take(iterations)
      .forEach { a ->
         val shrinkfn = shrinkfn(a, property, config.shrinkingMode)
         test(context, config, shrinkfn, listOf(a.value)) {
            context.property(a.value)
         }
      }
   context.checkMaxSuccess(config)
   return context
}


suspend fun <A, B> proptest(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A, B) -> Unit
): PropertyContext {

   // we must have enough iterations to cover the max(minsize).
   val minSize = max(genA.minIterations(), genB.minIterations())
   require(iterations >= minSize) { "Require at least $minSize iterations to cover requirements" }

   val context = PropertyContext()
   val random = config.seed.random()

   genA.generate(random).zip(genB.generate(random))
      .take(iterations)
      .forEach { (a, b) ->
         val shrinkfn = shrinkfn(a, b, property, config.shrinkingMode)
         test(context, config, shrinkfn, listOf(a.value, b.value)) {
            context.property(a.value, b.value)
         }
      }

   context.checkMaxSuccess(config)
   return context
}

suspend fun <A, B, C> proptest(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A, B, C) -> Unit
): PropertyContext {

   // we must have enough iterations to cover the max(minsize).
   val minSize = max(max(genA.minIterations(), genB.minIterations()), genC.minIterations())
   require(iterations >= minSize) { "Require at least $minSize iterations to cover requirements" }

   val context = PropertyContext()
   val random = config.seed.random()

   genA.generate(random).zip(genB.generate(random)).zip(genC.generate(random))
      .take(iterations)
      .forEach { (ab, c) ->
         val (a, b) = ab
         val shrinkfn = shrinkfn(a, b, c, property, config.shrinkingMode)
         test(context, config, shrinkfn, listOf(a.value, b.value)) {
            context.property(a.value, b.value, c.value)
         }
      }

   context.checkMaxSuccess(config)
   return context
}
