package io.kotest.property.internal

import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.Gen
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyContext
import io.kotest.property.RandomSource
import io.kotest.property.random
import kotlin.math.max

private fun actualIterations(iterations: Int, config: PropTestConfig) =
   config.iterations ?: iterations

private fun checkMinSize(minSize: Int, iterations: Int) =
   require(iterations >= minSize) { "Require at least $minSize iterations to cover requirements" }

suspend fun <A> proptest(
   iterations: Int,
   genA: Gen<A>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A) -> Unit
): PropertyContext {

   val actualIterations = actualIterations(iterations, config)
   checkMinSize(genA.minIterations(), actualIterations)

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.default()

   when (genA) {
      is Arb -> {
         genA.generate(random, config.edgeConfig)
            .take(actualIterations)
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
   val actualIterations = actualIterations(iterations, config)
   checkMinSize(minSize, actualIterations)

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.default()

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
      genA.generate(random, config.edgeConfig)
         .zip(genB.generate(random, config.edgeConfig))
         .take(actualIterations)
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
   val actualIterations = actualIterations(iterations, config)
   checkMinSize(minSize, actualIterations)

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.default()

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
      genA.generate(random, config.edgeConfig)
         .zip(genB.generate(random, config.edgeConfig))
         .zip(genC.generate(random, config.edgeConfig))
         .take(actualIterations)
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
      listOf(genA.minIterations(), genB.minIterations(), genC.minIterations(), genD.minIterations()).maxOrNull() ?: 0
   val actualIterations = actualIterations(iterations, config)
   checkMinSize(minSize, actualIterations)

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.default()

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

      genA.generate(random, config.edgeConfig)
         .zip(genB.generate(random, config.edgeConfig))
         .zip(genC.generate(random, config.edgeConfig))
         .zip(genD.generate(random, config.edgeConfig))
         .take(actualIterations)
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
   ).maxOrNull() ?: 0
   val actualIterations = actualIterations(iterations, config)
   checkMinSize(minSize, actualIterations)

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.default()

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
      genA.generate(random, config.edgeConfig)
         .zip(genB.generate(random, config.edgeConfig))
         .zip(genC.generate(random, config.edgeConfig))
         .zip(genD.generate(random, config.edgeConfig))
         .zip(genE.generate(random, config.edgeConfig))
         .take(actualIterations)
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
   ).maxOrNull() ?: 0
   val actualIterations = actualIterations(iterations, config)
   checkMinSize(minSize, actualIterations)

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.default()

   genA.generate(random, config.edgeConfig)
      .zip(genB.generate(random, config.edgeConfig))
      .zip(genC.generate(random, config.edgeConfig))
      .zip(genD.generate(random, config.edgeConfig))
      .zip(genE.generate(random, config.edgeConfig))
      .zip(genF.generate(random, config.edgeConfig))
      .take(actualIterations)
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

suspend fun <A, B, C, D, E, F, G> proptest(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A, B, C, D, E, F, G) -> Unit
): PropertyContext {

   // we must have enough iterations to cover the max(minsize).

   val minSize = listOf(
      genA.minIterations(),
      genB.minIterations(),
      genC.minIterations(),
      genD.minIterations(),
      genE.minIterations(),
      genF.minIterations(),
      genG.minIterations(),
   ).maxOrNull() ?: 0
   val actualIterations = actualIterations(iterations, config)
   checkMinSize(minSize, actualIterations)

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.default()

   genA.generate(random, config.edgeConfig)
      .zip(genB.generate(random, config.edgeConfig))
      .zip(genC.generate(random, config.edgeConfig))
      .zip(genD.generate(random, config.edgeConfig))
      .zip(genE.generate(random, config.edgeConfig))
      .zip(genF.generate(random, config.edgeConfig))
      .zip(genG.generate(random, config.edgeConfig))
      .take(actualIterations)
      .forEach { (abcdef, g) ->
         val (abcde, f) = abcdef
         val (abcd, e) = abcde
         val (abc, d) = abcd
         val (ab, c) = abc
         val (a, b) = ab
         val shrinkfn = shrinkfn(a, b, c, d, e, f, g, property, config.shrinkingMode)
         config.listeners.forEach { it.beforeTest() }
         test(
            context,
            config,
            shrinkfn,
            listOf(a.value, b.value, c.value, d.value, e.value, f.value, g.value),
            random.seed
         ) {
            context.property(a.value, b.value, c.value, d.value, e.value, f.value, g.value)
         }
         config.listeners.forEach { it.afterTest() }
      }
   context.checkMaxSuccess(config, random.seed)
   return context
}

suspend fun <A, B, C, D, E, F, G, H> proptest(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H) -> Unit
): PropertyContext {

   // we must have enough iterations to cover the max(minsize).

   val minSize = listOf(
      genA.minIterations(),
      genB.minIterations(),
      genC.minIterations(),
      genD.minIterations(),
      genE.minIterations(),
      genF.minIterations(),
      genG.minIterations(),
      genH.minIterations(),
   ).maxOrNull() ?: 0
   val actualIterations = actualIterations(iterations, config)
   checkMinSize(minSize, actualIterations)

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.default()

   genA.generate(random, config.edgeConfig)
      .zip(genB.generate(random, config.edgeConfig))
      .zip(genC.generate(random, config.edgeConfig))
      .zip(genD.generate(random, config.edgeConfig))
      .zip(genE.generate(random, config.edgeConfig))
      .zip(genF.generate(random, config.edgeConfig))
      .zip(genG.generate(random, config.edgeConfig))
      .zip(genH.generate(random, config.edgeConfig))
      .take(actualIterations)
      .forEach { (abcdefg, h) ->
         val (abcdef, g) = abcdefg
         val (abcde, f) = abcdef
         val (abcd, e) = abcde
         val (abc, d) = abcd
         val (ab, c) = abc
         val (a, b) = ab
         val shrinkfn = shrinkfn(a, b, c, d, e, f, g, h, property, config.shrinkingMode)
         config.listeners.forEach { it.beforeTest() }
         test(
            context,
            config,
            shrinkfn,
            listOf(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value),
            random.seed
         ) {
            context.property(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value)
         }
         config.listeners.forEach { it.afterTest() }
      }
   context.checkMaxSuccess(config, random.seed)
   return context
}

suspend fun <A, B, C, D, E, F, G, H, I> proptest(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   genI: Gen<I>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I) -> Unit
): PropertyContext {

   // we must have enough iterations to cover the max(minsize).

   val minSize = listOf(
      genA.minIterations(),
      genB.minIterations(),
      genC.minIterations(),
      genD.minIterations(),
      genE.minIterations(),
      genF.minIterations(),
      genG.minIterations(),
      genH.minIterations(),
      genI.minIterations(),
   ).maxOrNull() ?: 0
   val actualIterations = actualIterations(iterations, config)
   checkMinSize(minSize, actualIterations)

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.default()

   genA.generate(random, config.edgeConfig)
      .zip(genB.generate(random, config.edgeConfig))
      .zip(genC.generate(random, config.edgeConfig))
      .zip(genD.generate(random, config.edgeConfig))
      .zip(genE.generate(random, config.edgeConfig))
      .zip(genF.generate(random, config.edgeConfig))
      .zip(genG.generate(random, config.edgeConfig))
      .zip(genH.generate(random, config.edgeConfig))
      .zip(genI.generate(random, config.edgeConfig))
      .take(actualIterations)
      .forEach { (abcdefgh, i) ->
         val (abcdefg, h) = abcdefgh
         val (abcdef, g) = abcdefg
         val (abcde, f) = abcdef
         val (abcd, e) = abcde
         val (abc, d) = abcd
         val (ab, c) = abc
         val (a, b) = ab
         val shrinkfn = shrinkfn(a, b, c, d, e, f, g, h, i, property, config.shrinkingMode)
         config.listeners.forEach { it.beforeTest() }
         test(
            context,
            config,
            shrinkfn,
            listOf(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value),
            random.seed
         ) {
            context.property(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value)
         }
         config.listeners.forEach { it.afterTest() }
      }
   context.checkMaxSuccess(config, random.seed)
   return context
}

suspend fun <A, B, C, D, E, F, G, H, I, J> proptest(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   genI: Gen<I>,
   genJ: Gen<J>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J) -> Unit
): PropertyContext {

   // we must have enough iterations to cover the max(minsize).

   val minSize = listOf(
      genA.minIterations(),
      genB.minIterations(),
      genC.minIterations(),
      genD.minIterations(),
      genE.minIterations(),
      genF.minIterations(),
      genG.minIterations(),
      genH.minIterations(),
      genI.minIterations(),
      genJ.minIterations(),
   ).maxOrNull() ?: 0
   val actualIterations = actualIterations(iterations, config)
   checkMinSize(minSize, actualIterations)

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.default()

   genA.generate(random, config.edgeConfig)
      .zip(genB.generate(random, config.edgeConfig))
      .zip(genC.generate(random, config.edgeConfig))
      .zip(genD.generate(random, config.edgeConfig))
      .zip(genE.generate(random, config.edgeConfig))
      .zip(genF.generate(random, config.edgeConfig))
      .zip(genG.generate(random, config.edgeConfig))
      .zip(genH.generate(random, config.edgeConfig))
      .zip(genI.generate(random, config.edgeConfig))
      .zip(genJ.generate(random, config.edgeConfig))
      .take(actualIterations)
      .forEach { (abcdefghi, j) ->
         val (abcdefgh, i) = abcdefghi
         val (abcdefg, h) = abcdefgh
         val (abcdef, g) = abcdefg
         val (abcde, f) = abcdef
         val (abcd, e) = abcde
         val (abc, d) = abcd
         val (ab, c) = abc
         val (a, b) = ab
         val shrinkfn = shrinkfn(a, b, c, d, e, f, g, h, i, j, property, config.shrinkingMode)
         config.listeners.forEach { it.beforeTest() }
         test(
            context,
            config,
            shrinkfn,
            listOf(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value),
            random.seed
         ) {
            context.property(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value)
         }
         config.listeners.forEach { it.afterTest() }
      }
   context.checkMaxSuccess(config, random.seed)
   return context
}

suspend fun <A, B, C, D, E, F, G, H, I, J, K> proptest(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   genI: Gen<I>,
   genJ: Gen<J>,
   genK: Gen<K>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Unit
): PropertyContext {

   // we must have enough iterations to cover the max(minsize).

   val minSize = listOf(
      genA.minIterations(),
      genB.minIterations(),
      genC.minIterations(),
      genD.minIterations(),
      genE.minIterations(),
      genF.minIterations(),
      genG.minIterations(),
      genH.minIterations(),
      genI.minIterations(),
      genJ.minIterations(),
      genK.minIterations(),
   ).maxOrNull() ?: 0
   val actualIterations = actualIterations(iterations, config)
   checkMinSize(minSize, actualIterations)

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.default()

   genA.generate(random, config.edgeConfig)
      .zip(genB.generate(random, config.edgeConfig))
      .zip(genC.generate(random, config.edgeConfig))
      .zip(genD.generate(random, config.edgeConfig))
      .zip(genE.generate(random, config.edgeConfig))
      .zip(genF.generate(random, config.edgeConfig))
      .zip(genG.generate(random, config.edgeConfig))
      .zip(genH.generate(random, config.edgeConfig))
      .zip(genI.generate(random, config.edgeConfig))
      .zip(genJ.generate(random, config.edgeConfig))
      .zip(genK.generate(random, config.edgeConfig))
      .take(actualIterations)
      .forEach { (abcdefghij, k) ->
         val (abcdefghi, j) = abcdefghij
         val (abcdefgh, i) = abcdefghi
         val (abcdefg, h) = abcdefgh
         val (abcdef, g) = abcdefg
         val (abcde, f) = abcdef
         val (abcd, e) = abcde
         val (abc, d) = abcd
         val (ab, c) = abc
         val (a, b) = ab
         val shrinkfn = shrinkfn(a, b, c, d, e, f, g, h, i, j, k, property, config.shrinkingMode)
         config.listeners.forEach { it.beforeTest() }
         test(
            context,
            config,
            shrinkfn,
            listOf(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value),
            random.seed
         ) {
            context.property(
               a.value,
               b.value,
               c.value,
               d.value,
               e.value,
               f.value,
               g.value,
               h.value,
               i.value,
               j.value,
               k.value
            )
         }
         config.listeners.forEach { it.afterTest() }
      }
   context.checkMaxSuccess(config, random.seed)
   return context
}

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L> proptest(
   iterations: Int,
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   genH: Gen<H>,
   genI: Gen<I>,
   genJ: Gen<J>,
   genK: Gen<K>,
   genL: Gen<L>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L) -> Unit
): PropertyContext {

   // we must have enough iterations to cover the max(minsize).

   val minSize = listOf(
      genA.minIterations(),
      genB.minIterations(),
      genC.minIterations(),
      genD.minIterations(),
      genE.minIterations(),
      genF.minIterations(),
      genG.minIterations(),
      genH.minIterations(),
      genI.minIterations(),
      genJ.minIterations(),
      genK.minIterations(),
      genL.minIterations(),
   ).maxOrNull() ?: 0
   val actualIterations = actualIterations(iterations, config)
   checkMinSize(minSize, actualIterations)

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.default()

   genA.generate(random, config.edgeConfig)
      .zip(genB.generate(random, config.edgeConfig))
      .zip(genC.generate(random, config.edgeConfig))
      .zip(genD.generate(random, config.edgeConfig))
      .zip(genE.generate(random, config.edgeConfig))
      .zip(genF.generate(random, config.edgeConfig))
      .zip(genG.generate(random, config.edgeConfig))
      .zip(genH.generate(random, config.edgeConfig))
      .zip(genI.generate(random, config.edgeConfig))
      .zip(genJ.generate(random, config.edgeConfig))
      .zip(genK.generate(random, config.edgeConfig))
      .zip(genL.generate(random, config.edgeConfig))
      .take(actualIterations)
      .forEach { (abcdefghijk, l) ->
         val (abcdefghij, k) = abcdefghijk
         val (abcdefghi, j) = abcdefghij
         val (abcdefgh, i) = abcdefghi
         val (abcdefg, h) = abcdefgh
         val (abcdef, g) = abcdefg
         val (abcde, f) = abcdef
         val (abcd, e) = abcde
         val (abc, d) = abcd
         val (ab, c) = abc
         val (a, b) = ab
         val shrinkfn = shrinkfn(a, b, c, d, e, f, g, h, i, j, k, l, property, config.shrinkingMode)
         config.listeners.forEach { it.beforeTest() }
         test(
            context,
            config,
            shrinkfn,
            listOf(
               a.value,
               b.value,
               c.value,
               d.value,
               e.value,
               f.value,
               g.value,
               h.value,
               i.value,
               j.value,
               k.value,
               l.value
            ),
            random.seed
         ) {
            context.property(
               a.value,
               b.value,
               c.value,
               d.value,
               e.value,
               f.value,
               g.value,
               h.value,
               i.value,
               j.value,
               k.value,
               l.value
            )
         }
         config.listeners.forEach { it.afterTest() }
      }
   context.checkMaxSuccess(config, random.seed)
   return context
}
