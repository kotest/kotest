package io.kotest.property.internal

import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.Gen
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyContext
import io.kotest.property.RandomSource
import io.kotest.property.classifications.outputClassifications
import io.kotest.property.computeDefaultIteration
import io.kotest.property.random
import kotlin.math.max

private fun checkMinSize(minSize: Int, iterations: Int) =
   require(iterations >= minSize) { "Require at least $minSize iterations to cover requirements" }

suspend fun <A> proptest(
   genA: Gen<A>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A) -> Unit
): PropertyContext {

   val iterations = config.iterations ?: computeDefaultIteration(genA)
   checkMinSize(genA.minIterations(), iterations)

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.default()

   when (genA) {
      is Arb -> {
         genA.generate(random, config.edgeConfig)
            .take(iterations)
            .forEach { a ->
               val shrinkfn = shrinkfn(a, property, config.shrinkingMode)
               config.listeners.forEach { it.beforeTest() }
               test(
                  context,
                  config,
                  shrinkfn,
                  listOf(a.value),
                  listOf(genA.classifier),
                  random.seed
               ) {
                  context.property(a.value)
               }
               config.listeners.forEach { it.afterTest() }
            }
      }
      is Exhaustive -> {
         genA.values.forEach { a ->
            config.listeners.forEach { it.beforeTest() }
            test(
               context,
               config,
               { emptyList() },
               listOf(a),
               listOf(genA.classifier),
               random.seed
            ) {
               context.property(a)
            }
            config.listeners.forEach { it.afterTest() }
         }
      }
   }

   context.outputClassifications(1, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   return context
}

suspend fun <A, B> proptest(
   genA: Gen<A>,
   genB: Gen<B>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A, B) -> Unit
): PropertyContext {

   // we must have enough iterations to cover the max(minsize).
   val minSize = max(genA.minIterations(), genB.minIterations())
   val iterations = config.iterations ?: computeDefaultIteration(genA, genB)
   checkMinSize(minSize, iterations)

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.default()

   if (genA is Exhaustive && genB is Exhaustive) {
      genA.values.forEach { a ->
         genB.values.forEach { b ->
            config.listeners.forEach { it.beforeTest() }
            test(
               context,
               config,
               { emptyList() },
               listOf(a, b),
               listOf(genA.classifier, genB.classifier),
               random.seed
            ) {
               context.property(a, b)
            }
            config.listeners.forEach { it.afterTest() }
         }
      }
   } else {
      genA.generate(random, config.edgeConfig)
         .zip(genB.generate(random, config.edgeConfig))
         .take(iterations)
         .forEach { (a, b) ->
            val shrinkfn = shrinkfn(a, b, property, config.shrinkingMode)
            config.listeners.forEach { it.beforeTest() }
            test(
               context,
               config,
               shrinkfn,
               listOf(a.value, b.value),
               listOf(genA.classifier, genB.classifier),
               random.seed
            ) {
               context.property(a.value, b.value)
            }
            config.listeners.forEach { it.afterTest() }
         }
   }

   context.outputClassifications(2, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   return context
}

suspend fun <A, B, C> proptest(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A, B, C) -> Unit
): PropertyContext {

   // we must have enough iterations to cover the max(minsize).
   val minSize = max(max(genA.minIterations(), genB.minIterations()), genC.minIterations())
   val iterations = config.iterations ?: computeDefaultIteration(genA, genB, genC)
   checkMinSize(minSize, iterations)

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.default()

   if (genA is Exhaustive && genB is Exhaustive && genC is Exhaustive) {
      genA.values.forEach { a ->
         genB.values.forEach { b ->
            genC.values.forEach { c ->
               config.listeners.forEach { it.beforeTest() }
               test(
                  context,
                  config,
                  { emptyList() },
                  listOf(a, b, c),
                  listOf(genA.classifier, genB.classifier, genC.classifier),
                  random.seed
               ) {
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
         .take(iterations)
         .forEach { (ab, c) ->
            val (a, b) = ab
            val shrinkfn = shrinkfn(a, b, c, property, config.shrinkingMode)
            config.listeners.forEach { it.beforeTest() }
            test(
               context,
               config,
               shrinkfn,
               listOf(a.value, b.value, c.value),
               listOf(genA.classifier, genB.classifier, genC.classifier),
               random.seed
            ) {
               context.property(a.value, b.value, c.value)
            }
            config.listeners.forEach { it.afterTest() }
         }
   }

   context.outputClassifications(3, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   return context
}

suspend fun <A, B, C, D> proptest(
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
   val iterations = config.iterations ?: computeDefaultIteration(genA, genB, genC, genD)
   checkMinSize(minSize, iterations)

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.default()

   if (genA is Exhaustive && genB is Exhaustive && genC is Exhaustive && genD is Exhaustive) {
      genA.values.forEach { a ->
         genB.values.forEach { b ->
            genC.values.forEach { c ->
               genD.values.forEach { d ->
                  config.listeners.forEach { it.beforeTest() }
                  test(
                     context, config, { emptyList() }, listOf(a, b, c, d),
                     listOf(genA.classifier, genB.classifier, genC.classifier, genD.classifier), random.seed
                  ) {
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
         .take(iterations)
         .forEach { (abc, d) ->
            val (ab, c) = abc
            val (a, b) = ab
            val shrinkfn = shrinkfn(a, b, c, d, property, config.shrinkingMode)
            config.listeners.forEach { it.beforeTest() }
            test(
               context, config, shrinkfn,
               listOf(a.value, b.value, c.value, d.value),
               listOf(genA.classifier, genB.classifier, genC.classifier, genD.classifier),
               random.seed
            ) {
               context.property(a.value, b.value, c.value, d.value)
            }
            config.listeners.forEach { it.afterTest() }
         }
   }

   context.outputClassifications(4, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   return context
}

suspend fun <A, B, C, D, E> proptest(
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
   val iterations = config.iterations ?: computeDefaultIteration(genA, genB, genC, genD, genE)
   checkMinSize(minSize, iterations)

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.default()

   if (genA is Exhaustive && genB is Exhaustive && genC is Exhaustive && genD is Exhaustive && genE is Exhaustive) {
      genA.values.forEach { a ->
         genB.values.forEach { b ->
            genC.values.forEach { c ->
               genD.values.forEach { d ->
                  genE.values.forEach { e ->
                     config.listeners.forEach { it.beforeTest() }
                     test(
                        context,
                        config,
                        { emptyList() },
                        listOf(a, b, c, d, e),
                        listOf(
                           genA.classifier,
                           genB.classifier,
                           genC.classifier,
                           genD.classifier,
                           genE.classifier,
                        ),
                        random.seed
                     ) {
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
         .take(iterations)
         .forEach { (abcd, e) ->
            val (abc, d) = abcd
            val (ab, c) = abc
            val (a, b) = ab
            val shrinkfn = shrinkfn(a, b, c, d, e, property, config.shrinkingMode)
            config.listeners.forEach { it.beforeTest() }
            test(
               context,
               config,
               shrinkfn,
               listOf(a.value, b.value, c.value, d.value, e.value),
               listOf(
                  genA.classifier,
                  genB.classifier,
                  genC.classifier,
                  genD.classifier,
                  genE.classifier,
               ),
               random.seed
            ) {
               context.property(a.value, b.value, c.value, d.value, e.value)
            }
            config.listeners.forEach { it.afterTest() }
         }
   }

   context.outputClassifications(5, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   return context
}

suspend fun <A, B, C, D, E, F> proptest(
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
   val iterations = config.iterations ?: computeDefaultIteration(genA, genB, genC, genD, genE, genF)
   checkMinSize(minSize, iterations)

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.default()

   genA.generate(random, config.edgeConfig)
      .zip(genB.generate(random, config.edgeConfig))
      .zip(genC.generate(random, config.edgeConfig))
      .zip(genD.generate(random, config.edgeConfig))
      .zip(genE.generate(random, config.edgeConfig))
      .zip(genF.generate(random, config.edgeConfig))
      .take(iterations)
      .forEach { (abcde, f) ->
         val (abcd, e) = abcde
         val (abc, d) = abcd
         val (ab, c) = abc
         val (a, b) = ab
         val shrinkfn = shrinkfn(a, b, c, d, e, f, property, config.shrinkingMode)
         config.listeners.forEach { it.beforeTest() }
         test(
            context,
            config,
            shrinkfn,
            listOf(a.value, b.value, c.value, d.value, e.value, f.value),
            listOf(
               genA.classifier,
               genB.classifier,
               genC.classifier,
               genD.classifier,
               genE.classifier,
               genF.classifier,
            ),
            random.seed
         ) {
            context.property(a.value, b.value, c.value, d.value, e.value, f.value)
         }
         config.listeners.forEach { it.afterTest() }
      }

   context.outputClassifications(6, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   return context
}

suspend fun <A, B, C, D, E, F, G> proptest(
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
   val iterations = config.iterations ?: computeDefaultIteration(genA, genB, genC, genD, genE, genF, genG)
   checkMinSize(minSize, iterations)

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.default()

   genA.generate(random, config.edgeConfig)
      .zip(genB.generate(random, config.edgeConfig))
      .zip(genC.generate(random, config.edgeConfig))
      .zip(genD.generate(random, config.edgeConfig))
      .zip(genE.generate(random, config.edgeConfig))
      .zip(genF.generate(random, config.edgeConfig))
      .zip(genG.generate(random, config.edgeConfig))
      .take(iterations)
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
            listOf(
               genA.classifier,
               genB.classifier,
               genC.classifier,
               genD.classifier,
               genE.classifier,
               genF.classifier,
               genG.classifier
            ),
            random.seed
         ) {
            context.property(a.value, b.value, c.value, d.value, e.value, f.value, g.value)
         }
         config.listeners.forEach { it.afterTest() }
      }

   context.outputClassifications(7, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   return context
}

suspend fun <A, B, C, D, E, F, G, H> proptest(
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
   val iterations = config.iterations ?: computeDefaultIteration(genA, genB, genC, genD, genE, genF, genG, genH)
   checkMinSize(minSize, iterations)

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
      .take(iterations)
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
            listOf(
               genA.classifier,
               genB.classifier,
               genC.classifier,
               genD.classifier,
               genE.classifier,
               genF.classifier,
               genG.classifier,
               genH.classifier
            ),
            random.seed
         ) {
            context.property(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value)
         }
         config.listeners.forEach { it.afterTest() }
      }

   context.outputClassifications(8, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   return context
}

suspend fun <A, B, C, D, E, F, G, H, I> proptest(
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
   val iterations = config.iterations ?: computeDefaultIteration(genA, genB, genC, genD, genE, genF, genG, genH, genI)
   checkMinSize(minSize, iterations)

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
      .take(iterations)
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
            listOf(
               genA.classifier,
               genB.classifier,
               genC.classifier,
               genD.classifier,
               genE.classifier,
               genF.classifier,
               genG.classifier,
               genH.classifier,
               genI.classifier
            ),
            random.seed
         ) {
            context.property(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value)
         }
         config.listeners.forEach { it.afterTest() }
      }

   context.outputClassifications(9, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   return context
}

suspend fun <A, B, C, D, E, F, G, H, I, J> proptest(
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
   val iterations = config.iterations ?: computeDefaultIteration(genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ)
   checkMinSize(minSize, iterations)

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
      .take(iterations)
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
            listOf(
               genA.classifier,
               genB.classifier,
               genC.classifier,
               genD.classifier,
               genE.classifier,
               genF.classifier,
               genG.classifier,
               genH.classifier,
               genI.classifier,
               genJ.classifier
            ),
            random.seed
         ) {
            context.property(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value)
         }
         config.listeners.forEach { it.afterTest() }
      }

   context.outputClassifications(10, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   return context
}

suspend fun <A, B, C, D, E, F, G, H, I, J, K> proptest(
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
   val iterations = config.iterations ?: computeDefaultIteration(genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK)
   checkMinSize(minSize, iterations)

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
      .take(iterations)
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
            listOf(
               genA.classifier,
               genB.classifier,
               genC.classifier,
               genD.classifier,
               genE.classifier,
               genF.classifier,
               genG.classifier,
               genH.classifier,
               genI.classifier,
               genJ.classifier,
               genK.classifier
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
               k.value
            )
         }
         config.listeners.forEach { it.afterTest() }
      }

   context.outputClassifications(11, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   return context
}

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L> proptest(
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
   val iterations = config.iterations ?: computeDefaultIteration(genA, genB, genC, genD, genE, genF, genG, genH, genI, genJ, genK, genL)
   checkMinSize(minSize, iterations)

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
      .take(iterations)
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
            listOf(
               genA.classifier,
               genB.classifier,
               genC.classifier,
               genD.classifier,
               genE.classifier,
               genF.classifier,
               genG.classifier,
               genH.classifier,
               genI.classifier,
               genJ.classifier,
               genK.classifier,
               genL.classifier
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

   context.outputClassifications(12, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   return context
}
