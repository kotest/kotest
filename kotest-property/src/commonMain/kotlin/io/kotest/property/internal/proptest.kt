package io.kotest.property.internal

import io.kotest.property.Arb
import io.kotest.property.Constraints
import io.kotest.property.Exhaustive
import io.kotest.property.Gen
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyContext
import io.kotest.property.PropertyTesting
import io.kotest.property.RandomSource
import io.kotest.property.classifications.outputClassifications
import io.kotest.property.random

suspend fun <A> proptest(
   genA: Gen<A>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A) -> Unit
): PropertyContext {

   config.checkFailOnSeed()

   val constraints = config.constraints
      ?: config.iterations?.let { Constraints.iterations(it) }
      ?: Constraints.iterations(PropertyTesting.defaultIterationCount)

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.default()

   when (genA) {
      is Arb -> {
         genA.generate(random, config.edgeConfig)
            .takeWhile { constraints.evaluate() }
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

   config.checkFailOnSeed()

   val constraints = config.constraints
      ?: config.iterations?.let { Constraints.iterations(it) }
      ?: Constraints.iterations(PropertyTesting.defaultIterationCount)

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
         .takeWhile { constraints.evaluate() }
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

   config.checkFailOnSeed()

   val constraints = config.constraints
      ?: config.iterations?.let { Constraints.iterations(it) }
      ?: Constraints.iterations(PropertyTesting.defaultIterationCount)

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
         .takeWhile { constraints.evaluate() }
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

   config.checkFailOnSeed()

   val constraints = config.constraints
      ?: config.iterations?.let { Constraints.iterations(it) }
      ?: Constraints.iterations(PropertyTesting.defaultIterationCount)

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
         .takeWhile { constraints.evaluate() }
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

   config.checkFailOnSeed()

   val constraints = config.constraints
      ?: config.iterations?.let { Constraints.iterations(it) }
      ?: Constraints.iterations(PropertyTesting.defaultIterationCount)

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
         .takeWhile { constraints.evaluate() }
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

   config.checkFailOnSeed()

   val constraints = config.constraints
      ?: config.iterations?.let { Constraints.iterations(it) }
      ?: Constraints.iterations(PropertyTesting.defaultIterationCount)

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.default()

   genA.generate(random, config.edgeConfig)
      .zip(genB.generate(random, config.edgeConfig))
      .zip(genC.generate(random, config.edgeConfig))
      .zip(genD.generate(random, config.edgeConfig))
      .zip(genE.generate(random, config.edgeConfig))
      .zip(genF.generate(random, config.edgeConfig))
      .takeWhile { constraints.evaluate() }
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

   config.checkFailOnSeed()

   val constraints = config.constraints
      ?: config.iterations?.let { Constraints.iterations(it) }
      ?: Constraints.iterations(PropertyTesting.defaultIterationCount)

   val context = PropertyContext()
   val random = config.seed?.random() ?: RandomSource.default()

   genA.generate(random, config.edgeConfig)
      .zip(genB.generate(random, config.edgeConfig))
      .zip(genC.generate(random, config.edgeConfig))
      .zip(genD.generate(random, config.edgeConfig))
      .zip(genE.generate(random, config.edgeConfig))
      .zip(genF.generate(random, config.edgeConfig))
      .zip(genG.generate(random, config.edgeConfig))
      .takeWhile { constraints.evaluate() }
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

   config.checkFailOnSeed()

   val constraints = config.constraints
      ?: config.iterations?.let { Constraints.iterations(it) }
      ?: Constraints.iterations(PropertyTesting.defaultIterationCount)

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
      .takeWhile { constraints.evaluate() }
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

   config.checkFailOnSeed()

   val constraints = config.constraints
      ?: config.iterations?.let { Constraints.iterations(it) }
      ?: Constraints.iterations(PropertyTesting.defaultIterationCount)

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
      .takeWhile { constraints.evaluate() }
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

   config.checkFailOnSeed()

   val constraints = config.constraints
      ?: config.iterations?.let { Constraints.iterations(it) }
      ?: Constraints.iterations(PropertyTesting.defaultIterationCount)

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
      .takeWhile { constraints.evaluate() }
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

   config.checkFailOnSeed()

   val constraints = config.constraints
      ?: config.iterations?.let { Constraints.iterations(it) }
      ?: Constraints.iterations(PropertyTesting.defaultIterationCount)

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
      .takeWhile { constraints.evaluate() }
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

   config.checkFailOnSeed()

   val constraints = config.constraints
      ?: config.iterations?.let { Constraints.iterations(it) }
      ?: Constraints.iterations(PropertyTesting.defaultIterationCount)

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
      .takeWhile { constraints.evaluate() }
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
