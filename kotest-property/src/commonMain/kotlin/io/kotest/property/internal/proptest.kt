package io.kotest.property.internal

import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.Gen
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyContext
import io.kotest.property.RandomSource
import io.kotest.property.random
import kotlin.math.max

suspend fun <A> proptest(
   iterations: Int,
   genA: Gen<A>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A) -> Unit
): PropertyContext {

   require(iterations >= genA.minIterations()) { "Require at least ${genA.minIterations()} iterations to cover requirements" }

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.Default

   when (genA) {
      is Arb -> {
         genA.generate(random)
            .take(iterations)
            .forEach { a ->
               val shrinkfn = shrinkfn(a, property, config.shrinkingMode)
               config.listeners.forEach { it.beforeTest() }
               test(context, config, shrinkfn, listOf(a.value), random.seed) {
                  context.property(a.value)
               }
               config.listeners.forEach { it.afterTest() }
            }
      }
      is Exhaustive -> {
         genA.values.forEach { a ->
            config.listeners.forEach { it.beforeTest() }
            test(context, config, { emptyList() }, listOf(a), random.seed) {
               context.property(a)
            }
            config.listeners.forEach { it.afterTest() }
         }
      }
   }

   context.checkMaxSuccess(config, random.seed)
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
   val random = config.seed?.random() ?: RandomSource.Default

   if (genA is Exhaustive && genB is Exhaustive) {
      genA.values.forEach { a ->
         genB.values.forEach { b ->
            config.listeners.forEach { it.beforeTest() }
            test(context, config, { emptyList() }, listOf(a, b), random.seed) {
               context.property(a, b)
            }
            config.listeners.forEach { it.afterTest() }
         }
      }
   } else {
      genA.generate(random).zip(genB.generate(random))
         .take(iterations)
         .forEach { (a, b) ->
            val shrinkfn = shrinkfn(a, b, property, config.shrinkingMode)
            config.listeners.forEach { it.beforeTest() }
            test(context, config, shrinkfn, listOf(a.value, b.value), random.seed) {
               context.property(a.value, b.value)
            }
            config.listeners.forEach { it.afterTest() }
         }
   }

   context.checkMaxSuccess(config, random.seed)
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
   val random = config.seed?.random() ?: RandomSource.Default

   if (genA is Exhaustive && genB is Exhaustive && genC is Exhaustive) {
      genA.values.forEach { a ->
         genB.values.forEach { b ->
            genC.values.forEach { c ->
               config.listeners.forEach { it.beforeTest() }
               test(context, config, { emptyList() }, listOf(a, b, c), random.seed) {
                  context.property(a, b, c)
               }
               config.listeners.forEach { it.afterTest() }
            }
         }
      }
   } else {
      genA.generate(random).zip(genB.generate(random)).zip(genC.generate(random))
         .take(iterations)
         .forEach { (ab, c) ->
            val (a, b) = ab
            val shrinkfn = shrinkfn(a, b, c, property, config.shrinkingMode)
            config.listeners.forEach { it.beforeTest() }
            test(context, config, shrinkfn, listOf(a.value, b.value, c.value), random.seed) {
               context.property(a.value, b.value, c.value)
            }
            config.listeners.forEach { it.afterTest() }
         }
   }

   context.checkMaxSuccess(config, random.seed)
   return context
}

suspend fun <A, B, C, D> proptest(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A, B, C, D) -> Unit
): PropertyContext {

   // we must have enough iterations to cover the max(minsize).

   val minSize =
      listOf(genA.minIterations(), genB.minIterations(), genC.minIterations(), genD.minIterations()).max() ?: 0
   require(iterations >= minSize) { "Require at least $minSize iterations to cover requirements" }

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.Default

   if (genA is Exhaustive && genB is Exhaustive && genC is Exhaustive && genD is Exhaustive) {
      genA.values.forEach { a ->
         genB.values.forEach { b ->
            genC.values.forEach { c ->
               genD.values.forEach { d ->
                  config.listeners.forEach { it.beforeTest() }
                  test(context, config, { emptyList() }, listOf(a, b, c, d), random.seed) {
                     context.property(a, b, c, d)
                  }
                  config.listeners.forEach { it.afterTest() }
               }
            }
         }
      }
   } else {

      genA.generate(random).zip(genB.generate(random)).zip(genC.generate(random)).zip(genD.generate(random))
         .take(iterations)
         .forEach { (abc, d) ->
            val (ab, c) = abc
            val (a, b) = ab
            val shrinkfn = shrinkfn(a, b, c, d, property, config.shrinkingMode)
            config.listeners.forEach { it.beforeTest() }
            test(context, config, shrinkfn, listOf(a.value, b.value, c.value, d.value), random.seed) {
               context.property(a.value, b.value, c.value, d.value)
            }
            config.listeners.forEach { it.afterTest() }
         }
   }

   context.checkMaxSuccess(config, random.seed)
   return context
}

suspend fun <A, B, C, D, E> proptest(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A, B, C, D, E) -> Unit
): PropertyContext {

   // we must have enough iterations to cover the max(minsize).

   val minSize = listOf(
      genA.minIterations(),
      genB.minIterations(),
      genC.minIterations(),
      genD.minIterations(),
      genE.minIterations()
   ).max() ?: 0
   require(iterations >= minSize) { "Require at least $minSize iterations to cover requirements" }

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.Default

   if (genA is Exhaustive && genB is Exhaustive && genC is Exhaustive && genD is Exhaustive && genE is Exhaustive) {
      genA.values.forEach { a ->
         genB.values.forEach { b ->
            genC.values.forEach { c ->
               genD.values.forEach { d ->
                  genE.values.forEach { e ->
                     config.listeners.forEach { it.beforeTest() }
                     test(context, config, { emptyList() }, listOf(a, b, c, d, e), random.seed) {
                        context.property(a, b, c, d, e)
                     }
                     config.listeners.forEach { it.afterTest() }
                  }
               }
            }
         }
      }
   } else {
      genA.generate(random).zip(genB.generate(random)).zip(genC.generate(random)).zip(genD.generate(random))
         .zip(genE.generate(random))
         .take(iterations)
         .forEach { (abcd, e) ->
            val (abc, d) = abcd
            val (ab, c) = abc
            val (a, b) = ab
            val shrinkfn = shrinkfn(a, b, c, d, e, property, config.shrinkingMode)
            config.listeners.forEach { it.beforeTest() }
            test(context, config, shrinkfn, listOf(a.value, b.value, c.value, d.value, e.value), random.seed) {
               context.property(a.value, b.value, c.value, d.value, e.value)
            }
            config.listeners.forEach { it.afterTest() }
         }
   }

   context.checkMaxSuccess(config, random.seed)
   return context
}

suspend fun <A, B, C, D, E, F> proptest(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Unit
): PropertyContext {

   // we must have enough iterations to cover the max(minsize).

   val minSize = listOf(
      genA.minIterations(),
      genB.minIterations(),
      genC.minIterations(),
      genD.minIterations(),
      genE.minIterations(),
      genF.minIterations()
   ).max() ?: 0
   require(iterations >= minSize) { "Require at least $minSize iterations to cover requirements" }

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.Default

   genA.generate(random).zip(genB.generate(random)).zip(genC.generate(random)).zip(genD.generate(random))
      .zip(genE.generate(random)).zip(genF.generate(random))
      .take(iterations)
      .forEach { (abcde, f) ->
         val (abcd, e) = abcde
         val (abc, d) = abcd
         val (ab, c) = abc
         val (a, b) = ab
         val shrinkfn = shrinkfn(a, b, c, d, e, f, property, config.shrinkingMode)
         config.listeners.forEach { it.beforeTest() }
         test(context, config, shrinkfn, listOf(a.value, b.value, c.value, d.value, e.value, f.value), random.seed) {
            context.property(a.value, b.value, c.value, d.value, e.value, f.value)
         }
         config.listeners.forEach { it.afterTest() }
      }
   context.checkMaxSuccess(config, random.seed)
   return context
}
