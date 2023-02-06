package io.kotest.property.internal

import io.kotest.property.Arb
import io.kotest.property.Constraints
import io.kotest.property.Exhaustive
import io.kotest.property.Gen
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyContext
import io.kotest.property.PropertyTesting
import io.kotest.property.Sample
import io.kotest.property.ShrinkingMode
import io.kotest.property.checkMaxDiscards
import io.kotest.property.classifications.outputClassifications
import io.kotest.property.seed.createRandom
import io.kotest.property.statistics.outputStatistics

suspend fun <A> proptest(
   genA: Gen<A>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A) -> Unit
): PropertyContext {

   config.checkFailOnSeed()

   val constraints = config.constraints
      ?: config.iterations?.let { Constraints.iterations(it) }
      ?: Constraints.iterations(PropertyTesting.defaultIterationCount)

   val context = PropertyContext(config)
   val random = createRandom(config)

   when (genA) {
      is Arb -> {
         genA.generate(random, config.edgeConfig)
            .takeWhile { constraints.evaluate(context) }
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

   outputStatistics(context, 1, true)
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

   val context = PropertyContext(config)
   val random = createRandom(config)

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
         .takeWhile { constraints.evaluate(context) }
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

   outputStatistics(context, 2, true)
   context.outputClassifications(2, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   context.checkMaxDiscards()
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

   val context = PropertyContext(config)
   val random = createRandom(config)

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
         .takeWhile { constraints.evaluate(context) }
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

   outputStatistics(context, 3, true)
   context.outputClassifications(3, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   context.checkMaxDiscards()
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

   val context = PropertyContext(config)
   val random = createRandom(config)

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
         .takeWhile { constraints.evaluate(context) }
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

   outputStatistics(context, 4, true)
   context.outputClassifications(4, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   context.checkMaxDiscards()
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

   val context = PropertyContext(config)
   val random = createRandom(config)

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
         .takeWhile { constraints.evaluate(context) }
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

   outputStatistics(context, 5, true)
   context.outputClassifications(5, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   context.checkMaxDiscards()
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

   val context = PropertyContext(config)
   val random = createRandom(config)

   if (genA is Exhaustive && genB is Exhaustive && genC is Exhaustive && genD is Exhaustive && genE is Exhaustive && genF is Exhaustive) {
      genA.values.forEach { a ->
         genB.values.forEach { b ->
            genC.values.forEach { c ->
               genD.values.forEach { d ->
                  genE.values.forEach { e ->
                     genF.values.forEach { f ->
                        config.listeners.forEach { it.beforeTest() }
                        test(
                           context,
                           config,
                           { emptyList() },
                           listOf(a, b, c, d, e, f),
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
                           context.property(a, b, c, d, e, f)
                        }
                        config.listeners.forEach { it.afterTest() }
                     }
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
         .zip(genF.generate(random, config.edgeConfig))
         .takeWhile { constraints.evaluate(context) }
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
   }

   outputStatistics(context, 6, true)
   context.outputClassifications(6, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   context.checkMaxDiscards()
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

   val context = PropertyContext(config)
   val random = createRandom(config)

   if (genA is Exhaustive && genB is Exhaustive && genC is Exhaustive && genD is Exhaustive && genE is Exhaustive && genF is Exhaustive && genG is Exhaustive) {
      genA.values.forEach { a ->
         genB.values.forEach { b ->
            genC.values.forEach { c ->
               genD.values.forEach { d ->
                  genE.values.forEach { e ->
                     genF.values.forEach { f ->
                        genG.values.forEach { g ->
                           config.listeners.forEach { it.beforeTest() }
                           test(
                              context,
                              config,
                              { emptyList() },
                              listOf(a, b, c, d, e, f, g),
                              listOf(
                                 genA.classifier,
                                 genB.classifier,
                                 genC.classifier,
                                 genD.classifier,
                                 genE.classifier,
                                 genF.classifier,
                                 genG.classifier,
                              ),
                              random.seed
                           ) {
                              context.property(a, b, c, d, e, f, g)
                           }
                           config.listeners.forEach { it.afterTest() }
                        }
                     }
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
         .zip(genF.generate(random, config.edgeConfig))
         .zip(genG.generate(random, config.edgeConfig))
         .takeWhile { constraints.evaluate(context) }
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
   }

   outputStatistics(context, 7, true)
   context.outputClassifications(7, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   context.checkMaxDiscards()
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

   val context = PropertyContext(config)
   val random = createRandom(config)

   if (genA is Exhaustive && genB is Exhaustive && genC is Exhaustive && genD is Exhaustive && genE is Exhaustive && genF is Exhaustive && genG is Exhaustive && genH is Exhaustive) {
      genA.values.forEach { a ->
         genB.values.forEach { b ->
            genC.values.forEach { c ->
               genD.values.forEach { d ->
                  genE.values.forEach { e ->
                     genF.values.forEach { f ->
                        genG.values.forEach { g ->
                           genH.values.forEach { h ->
                              config.listeners.forEach { it.beforeTest() }
                              test(
                                 context,
                                 config,
                                 { emptyList() },
                                 listOf(a, b, c, d, e, f, g, h),
                                 listOf(
                                    genA.classifier,
                                    genB.classifier,
                                    genC.classifier,
                                    genD.classifier,
                                    genE.classifier,
                                    genF.classifier,
                                    genG.classifier,
                                    genH.classifier,
                                 ),
                                 random.seed
                              ) {
                                 context.property(a, b, c, d, e, f, g, h)
                              }
                              config.listeners.forEach { it.afterTest() }
                           }
                        }
                     }
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
         .zip(genF.generate(random, config.edgeConfig))
         .zip(genG.generate(random, config.edgeConfig))
         .zip(genH.generate(random, config.edgeConfig))
         .takeWhile { constraints.evaluate(context) }
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
   }

   outputStatistics(context, 8, true)
   context.outputClassifications(8, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   context.checkMaxDiscards()
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

   val context = PropertyContext(config)
   val random = createRandom(config)

   if (genA is Exhaustive && genB is Exhaustive && genC is Exhaustive && genD is Exhaustive && genE is Exhaustive && genF is Exhaustive && genG is Exhaustive && genH is Exhaustive && genI is Exhaustive) {
      genA.values.forEach { a ->
         genB.values.forEach { b ->
            genC.values.forEach { c ->
               genD.values.forEach { d ->
                  genE.values.forEach { e ->
                     genF.values.forEach { f ->
                        genG.values.forEach { g ->
                           genH.values.forEach { h ->
                              genI.values.forEach { i ->
                                 config.listeners.forEach { it.beforeTest() }
                                 test(
                                    context,
                                    config,
                                    { emptyList() },
                                    listOf(a, b, c, d, e, f, g, h, i),
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
                                    ),
                                    random.seed
                                 ) {
                                    context.property(a, b, c, d, e, f, g, h, i)
                                 }
                                 config.listeners.forEach { it.afterTest() }
                              }
                           }
                        }
                     }
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
         .zip(genF.generate(random, config.edgeConfig))
         .zip(genG.generate(random, config.edgeConfig))
         .zip(genH.generate(random, config.edgeConfig))
         .zip(genI.generate(random, config.edgeConfig))
         .takeWhile { constraints.evaluate(context) }
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
   }

   outputStatistics(context, 9, true)
   context.outputClassifications(9, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   context.checkMaxDiscards()
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

   val context = PropertyContext(config)
   val random = createRandom(config)

   if (genA is Exhaustive && genB is Exhaustive && genC is Exhaustive && genD is Exhaustive && genE is Exhaustive && genF is Exhaustive && genG is Exhaustive && genH is Exhaustive && genI is Exhaustive && genJ is Exhaustive) {
      genA.values.forEach { a ->
         genB.values.forEach { b ->
            genC.values.forEach { c ->
               genD.values.forEach { d ->
                  genE.values.forEach { e ->
                     genF.values.forEach { f ->
                        genG.values.forEach { g ->
                           genH.values.forEach { h ->
                              genI.values.forEach { i ->
                                 genJ.values.forEach { j ->
                                    config.listeners.forEach { it.beforeTest() }
                                    test(
                                       context,
                                       config,
                                       { emptyList() },
                                       listOf(a, b, c, d, e, f, g, h, i, j),
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
                                       ),
                                       random.seed
                                    ) {
                                       context.property(a, b, c, d, e, f, g, h, i, j)
                                    }
                                    config.listeners.forEach { it.afterTest() }
                                 }
                              }
                           }
                        }
                     }
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
         .zip(genF.generate(random, config.edgeConfig))
         .zip(genG.generate(random, config.edgeConfig))
         .zip(genH.generate(random, config.edgeConfig))
         .zip(genI.generate(random, config.edgeConfig))
         .zip(genJ.generate(random, config.edgeConfig))
         .takeWhile { constraints.evaluate(context) }
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
                  j.value
               )
            }
            config.listeners.forEach { it.afterTest() }
         }
   }

   outputStatistics(context, 10, true)
   context.outputClassifications(10, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   context.checkMaxDiscards()
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

   val context = PropertyContext(config)
   val random = createRandom(config)

   if (genA is Exhaustive && genB is Exhaustive && genC is Exhaustive && genD is Exhaustive && genE is Exhaustive && genF is Exhaustive && genG is Exhaustive && genH is Exhaustive && genI is Exhaustive && genJ is Exhaustive && genK is Exhaustive) {
      genA.values.forEach { a ->
         genB.values.forEach { b ->
            genC.values.forEach { c ->
               genD.values.forEach { d ->
                  genE.values.forEach { e ->
                     genF.values.forEach { f ->
                        genG.values.forEach { g ->
                           genH.values.forEach { h ->
                              genI.values.forEach { i ->
                                 genJ.values.forEach { j ->
                                    genK.values.forEach { k ->
                                       config.listeners.forEach { it.beforeTest() }
                                       test(
                                          context,
                                          config,
                                          { emptyList() },
                                          listOf(a, b, c, d, e, f, g, h, i, j, k),
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
                                          ),
                                          random.seed
                                       ) {
                                          context.property(a, b, c, d, e, f, g, h, i, j, k)
                                       }
                                       config.listeners.forEach { it.afterTest() }
                                    }
                                 }
                              }
                           }
                        }
                     }
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
         .zip(genF.generate(random, config.edgeConfig))
         .zip(genG.generate(random, config.edgeConfig))
         .zip(genH.generate(random, config.edgeConfig))
         .zip(genI.generate(random, config.edgeConfig))
         .zip(genJ.generate(random, config.edgeConfig))
         .zip(genK.generate(random, config.edgeConfig))
         .takeWhile { constraints.evaluate(context) }
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
                  k.value
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
   }

   outputStatistics(context, 11, true)
   context.outputClassifications(11, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   context.checkMaxDiscards()
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

   val context = PropertyContext(config)
   val random = createRandom(config)

   if (genA is Exhaustive && genB is Exhaustive && genC is Exhaustive && genD is Exhaustive && genE is Exhaustive && genF is Exhaustive && genG is Exhaustive && genH is Exhaustive && genI is Exhaustive && genJ is Exhaustive && genK is Exhaustive && genL is Exhaustive) {
      genA.values.forEach { a ->
         genB.values.forEach { b ->
            genC.values.forEach { c ->
               genD.values.forEach { d ->
                  genE.values.forEach { e ->
                     genF.values.forEach { f ->
                        genG.values.forEach { g ->
                           genH.values.forEach { h ->
                              genI.values.forEach { i ->
                                 genJ.values.forEach { j ->
                                    genK.values.forEach { k ->
                                       genL.values.forEach { l ->
                                          config.listeners.forEach { it.beforeTest() }
                                          test(
                                             context,
                                             config,
                                             { emptyList() },
                                             listOf(a, b, c, d, e, f, g, h, i, j, k, l),
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
                                                genL.classifier,
                                             ),
                                             random.seed
                                          ) {
                                             context.property(a, b, c, d, e, f, g, h, i, j, k, l)
                                          }
                                          config.listeners.forEach { it.afterTest() }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
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
         .zip(genF.generate(random, config.edgeConfig))
         .zip(genG.generate(random, config.edgeConfig))
         .zip(genH.generate(random, config.edgeConfig))
         .zip(genI.generate(random, config.edgeConfig))
         .zip(genJ.generate(random, config.edgeConfig))
         .zip(genK.generate(random, config.edgeConfig))
         .zip(genL.generate(random, config.edgeConfig))
         .takeWhile { constraints.evaluate(context) }
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
   }

   outputStatistics(context, 12, true)
   context.outputClassifications(12, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   context.checkMaxDiscards()
   return context
}

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M> proptest(
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
   genM: Gen<M>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Unit
): PropertyContext {

   config.checkFailOnSeed()

   val constraints = config.constraints
      ?: config.iterations?.let { Constraints.iterations(it) }
      ?: Constraints.iterations(PropertyTesting.defaultIterationCount)

   val context = PropertyContext(config)
   val random = createRandom(config)

   if (genA is Exhaustive && genB is Exhaustive && genC is Exhaustive && genD is Exhaustive && genE is Exhaustive && genF is Exhaustive && genG is Exhaustive && genH is Exhaustive && genI is Exhaustive && genJ is Exhaustive && genK is Exhaustive && genL is Exhaustive && genM is Exhaustive) {
      genA.values.forEach { a ->
         genB.values.forEach { b ->
            genC.values.forEach { c ->
               genD.values.forEach { d ->
                  genE.values.forEach { e ->
                     genF.values.forEach { f ->
                        genG.values.forEach { g ->
                           genH.values.forEach { h ->
                              genI.values.forEach { i ->
                                 genJ.values.forEach { j ->
                                    genK.values.forEach { k ->
                                       genL.values.forEach { l ->
                                          genM.values.forEach { m ->
                                             config.listeners.forEach { it.beforeTest() }
                                             test(
                                                context,
                                                config,
                                                { emptyList() },
                                                listOf(a, b, c, d, e, f, g, h, i, j, k, l, m),
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
                                                   genL.classifier,
                                                   genM.classifier,
                                                ),
                                                random.seed
                                             ) {
                                                context.property(a, b, c, d, e, f, g, h, i, j, k, l, m)
                                             }
                                             config.listeners.forEach { it.afterTest() }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
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
         .zip(genF.generate(random, config.edgeConfig))
         .zip(genG.generate(random, config.edgeConfig))
         .zip(genH.generate(random, config.edgeConfig))
         .zip(genI.generate(random, config.edgeConfig))
         .zip(genJ.generate(random, config.edgeConfig))
         .zip(genK.generate(random, config.edgeConfig))
         .zip(genL.generate(random, config.edgeConfig))
         .zip(genM.generate(random, config.edgeConfig))
         .takeWhile { constraints.evaluate(context) }
         .forEach { (abcdefghijkl, m) ->
            val (abcdefghijk, l) = abcdefghijkl
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
            val shrinkfn = shrinkfn(a, b, c, d, e, f, g, h, i, j, k, l, m, property, config.shrinkingMode)
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
                  l.value,
                  m.value
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
                  genL.classifier,
                  genM.classifier
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
                  l.value,
                  m.value
               )
            }
            config.listeners.forEach { it.afterTest() }
         }
   }

   outputStatistics(context, 12, true)
   context.outputClassifications(12, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   context.checkMaxDiscards()
   return context
}

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> proptest(
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
   genM: Gen<M>,
   genN: Gen<N>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M, N) -> Unit
): PropertyContext {

   config.checkFailOnSeed()

   val constraints = config.constraints
      ?: config.iterations?.let { Constraints.iterations(it) }
      ?: Constraints.iterations(PropertyTesting.defaultIterationCount)

   val context = PropertyContext(config)
   val random = createRandom(config)

   if (genA is Exhaustive && genB is Exhaustive && genC is Exhaustive && genD is Exhaustive && genE is Exhaustive && genF is Exhaustive && genG is Exhaustive && genH is Exhaustive && genI is Exhaustive && genJ is Exhaustive && genK is Exhaustive && genL is Exhaustive && genM is Exhaustive && genN is Exhaustive) {
      genA.values.forEach { a ->
         genB.values.forEach { b ->
            genC.values.forEach { c ->
               genD.values.forEach { d ->
                  genE.values.forEach { e ->
                     genF.values.forEach { f ->
                        genG.values.forEach { g ->
                           genH.values.forEach { h ->
                              genI.values.forEach { i ->
                                 genJ.values.forEach { j ->
                                    genK.values.forEach { k ->
                                       genL.values.forEach { l ->
                                          genM.values.forEach { m ->
                                             genN.values.forEach { n ->
                                                config.listeners.forEach { it.beforeTest() }
                                                test(
                                                   context,
                                                   config,
                                                   { emptyList() },
                                                   listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n),
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
                                                      genL.classifier,
                                                      genM.classifier,
                                                   ),
                                                   random.seed
                                                ) {
                                                   context.property(a, b, c, d, e, f, g, h, i, j, k, l, m, n)
                                                }
                                                config.listeners.forEach { it.afterTest() }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
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
         .zip(genF.generate(random, config.edgeConfig))
         .zip(genG.generate(random, config.edgeConfig))
         .zip(genH.generate(random, config.edgeConfig))
         .zip(genI.generate(random, config.edgeConfig))
         .zip(genJ.generate(random, config.edgeConfig))
         .zip(genK.generate(random, config.edgeConfig))
         .zip(genL.generate(random, config.edgeConfig))
         .zip(genM.generate(random, config.edgeConfig))
         .zip(genN.generate(random, config.edgeConfig))
         .takeWhile { constraints.evaluate(context) }
         .forEach { (abcdefghijklm, n) ->
            val (abcdefghijkl, m) = abcdefghijklm
            val (abcdefghijk, l) = abcdefghijkl
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
            val shrinkfn = shrinkfn(a, b, c, d, e, f, g, h, i, j, k, l, m, n, property, config.shrinkingMode)
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
                  l.value,
                  m.value,
                  n.value
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
                  genL.classifier,
                  genM.classifier,
                  genN.classifier
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
                  l.value,
                  m.value,
                  n.value
               )
            }
            config.listeners.forEach { it.afterTest() }
         }
   }

   outputStatistics(context, 12, true)
   context.outputClassifications(12, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   context.checkMaxDiscards()
   return context
}

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> proptest(
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
   genM: Gen<M>,
   genN: Gen<N>,
   genO: Gen<O>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O) -> Unit
): PropertyContext {

   config.checkFailOnSeed()

   val constraints = config.constraints
      ?: config.iterations?.let { Constraints.iterations(it) }
      ?: Constraints.iterations(PropertyTesting.defaultIterationCount)

   val context = PropertyContext(config)
   val random = createRandom(config)

   if (genA is Exhaustive && genB is Exhaustive && genC is Exhaustive && genD is Exhaustive && genE is Exhaustive && genF is Exhaustive && genG is Exhaustive && genH is Exhaustive && genI is Exhaustive && genJ is Exhaustive && genK is Exhaustive && genL is Exhaustive && genM is Exhaustive && genN is Exhaustive && genO is Exhaustive) {
      genA.values.forEach { a ->
         genB.values.forEach { b ->
            genC.values.forEach { c ->
               genD.values.forEach { d ->
                  genE.values.forEach { e ->
                     genF.values.forEach { f ->
                        genG.values.forEach { g ->
                           genH.values.forEach { h ->
                              genI.values.forEach { i ->
                                 genJ.values.forEach { j ->
                                    genK.values.forEach { k ->
                                       genL.values.forEach { l ->
                                          genM.values.forEach { m ->
                                             genN.values.forEach { n ->
                                                genO.values.forEach { o ->
                                                   config.listeners.forEach { it.beforeTest() }
                                                   test(
                                                      context,
                                                      config,
                                                      { emptyList() },
                                                      listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o),
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
                                                         genL.classifier,
                                                         genM.classifier,
                                                         genN.classifier,
                                                      ),
                                                      random.seed
                                                   ) {
                                                      context.property(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)
                                                   }
                                                   config.listeners.forEach { it.afterTest() }
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
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
         .zip(genF.generate(random, config.edgeConfig))
         .zip(genG.generate(random, config.edgeConfig))
         .zip(genH.generate(random, config.edgeConfig))
         .zip(genI.generate(random, config.edgeConfig))
         .zip(genJ.generate(random, config.edgeConfig))
         .zip(genK.generate(random, config.edgeConfig))
         .zip(genL.generate(random, config.edgeConfig))
         .zip(genM.generate(random, config.edgeConfig))
         .zip(genN.generate(random, config.edgeConfig))
         .zip(genO.generate(random, config.edgeConfig))
         .takeWhile { constraints.evaluate(context) }
         .forEach { (abcdefghijklmn, o) ->
            val (abcdefghijklm, n) = abcdefghijklmn
            val (abcdefghijkl, m) = abcdefghijklm
            val (abcdefghijk, l) = abcdefghijkl
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
            val shrinkfn = shrinkfn(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, property, config.shrinkingMode)
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
                  l.value,
                  m.value,
                  n.value,
                  o.value
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
                  genL.classifier,
                  genM.classifier,
                  genN.classifier,
                  genO.classifier
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
                  l.value,
                  m.value,
                  n.value,
                  o.value
               )
            }
            config.listeners.forEach { it.afterTest() }
         }
   }

   outputStatistics(context, 12, true)
   context.outputClassifications(12, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   context.checkMaxDiscards()
   return context
}

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> proptest(
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
   genM: Gen<M>,
   genN: Gen<N>,
   genO: Gen<O>,
   genP: Gen<P>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P) -> Unit
): PropertyContext {

   config.checkFailOnSeed()

   val constraints = config.constraints
      ?: config.iterations?.let { Constraints.iterations(it) }
      ?: Constraints.iterations(PropertyTesting.defaultIterationCount)

   val context = PropertyContext(config)
   val random = createRandom(config)

   if (genA is Exhaustive && genB is Exhaustive && genC is Exhaustive && genD is Exhaustive && genE is Exhaustive && genF is Exhaustive && genG is Exhaustive && genH is Exhaustive && genI is Exhaustive && genJ is Exhaustive && genK is Exhaustive && genL is Exhaustive && genM is Exhaustive && genN is Exhaustive && genO is Exhaustive && genP is Exhaustive) {
      genA.values.forEach { a ->
         genB.values.forEach { b ->
            genC.values.forEach { c ->
               genD.values.forEach { d ->
                  genE.values.forEach { e ->
                     genF.values.forEach { f ->
                        genG.values.forEach { g ->
                           genH.values.forEach { h ->
                              genI.values.forEach { i ->
                                 genJ.values.forEach { j ->
                                    genK.values.forEach { k ->
                                       genL.values.forEach { l ->
                                          genM.values.forEach { m ->
                                             genN.values.forEach { n ->
                                                genO.values.forEach { o ->
                                                   genP.values.forEach { p ->
                                                      config.listeners.forEach { it.beforeTest() }
                                                      test(
                                                         context,
                                                         config,
                                                         { emptyList() },
                                                         listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p),
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
                                                            genL.classifier,
                                                            genM.classifier,
                                                            genN.classifier,
                                                            genO.classifier,
                                                         ),
                                                         random.seed
                                                      ) {
                                                         context.property(
                                                            a,
                                                            b,
                                                            c,
                                                            d,
                                                            e,
                                                            f,
                                                            g,
                                                            h,
                                                            i,
                                                            j,
                                                            k,
                                                            l,
                                                            m,
                                                            n,
                                                            o,
                                                            p
                                                         )
                                                      }
                                                      config.listeners.forEach { it.afterTest() }
                                                   }
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
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
         .zip(genF.generate(random, config.edgeConfig))
         .zip(genG.generate(random, config.edgeConfig))
         .zip(genH.generate(random, config.edgeConfig))
         .zip(genI.generate(random, config.edgeConfig))
         .zip(genJ.generate(random, config.edgeConfig))
         .zip(genK.generate(random, config.edgeConfig))
         .zip(genL.generate(random, config.edgeConfig))
         .zip(genM.generate(random, config.edgeConfig))
         .zip(genN.generate(random, config.edgeConfig))
         .zip(genO.generate(random, config.edgeConfig))
         .zip(genP.generate(random, config.edgeConfig))
         .takeWhile { constraints.evaluate(context) }
         .forEach { (abcdefghijklmno, p) ->
            val (abcdefghijklmn, o) = abcdefghijklmno
            val (abcdefghijklm, n) = abcdefghijklmn
            val (abcdefghijkl, m) = abcdefghijklm
            val (abcdefghijk, l) = abcdefghijkl
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
            val shrinkfn = shrinkfn(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, property, config.shrinkingMode)
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
                  l.value,
                  m.value,
                  n.value,
                  o.value,
                  p.value
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
                  genL.classifier,
                  genM.classifier,
                  genN.classifier,
                  genO.classifier,
                  genP.classifier
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
                  l.value,
                  m.value,
                  n.value,
                  o.value,
                  p.value
               )
            }
            config.listeners.forEach { it.afterTest() }
         }
   }

   outputStatistics(context, 12, true)
   context.outputClassifications(12, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   context.checkMaxDiscards()
   return context
}

suspend fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> proptest(
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
   genM: Gen<M>,
   genN: Gen<N>,
   genO: Gen<O>,
   genP: Gen<P>,
   genQ: Gen<Q>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q) -> Unit
): PropertyContext {

   config.checkFailOnSeed()

   val constraints = config.constraints
      ?: config.iterations?.let { Constraints.iterations(it) }
      ?: Constraints.iterations(PropertyTesting.defaultIterationCount)

   val context = PropertyContext(config)
   val random = createRandom(config)

   if (genA is Exhaustive && genB is Exhaustive && genC is Exhaustive && genD is Exhaustive && genE is Exhaustive && genF is Exhaustive && genG is Exhaustive && genH is Exhaustive && genI is Exhaustive && genJ is Exhaustive && genK is Exhaustive && genL is Exhaustive && genM is Exhaustive && genN is Exhaustive && genO is Exhaustive && genP is Exhaustive && genQ is Exhaustive) {
      genA.values.forEach { a ->
         genB.values.forEach { b ->
            genC.values.forEach { c ->
               genD.values.forEach { d ->
                  genE.values.forEach { e ->
                     genF.values.forEach { f ->
                        genG.values.forEach { g ->
                           genH.values.forEach { h ->
                              genI.values.forEach { i ->
                                 genJ.values.forEach { j ->
                                    genK.values.forEach { k ->
                                       genL.values.forEach { l ->
                                          genM.values.forEach { m ->
                                             genN.values.forEach { n ->
                                                genO.values.forEach { o ->
                                                   genP.values.forEach { p ->
                                                      genQ.values.forEach { q ->
                                                         config.listeners.forEach { it.beforeTest() }
                                                         test(
                                                            context,
                                                            config,
                                                            { emptyList() },
                                                            listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q),
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
                                                               genL.classifier,
                                                               genM.classifier,
                                                               genN.classifier,
                                                               genO.classifier,
                                                               genP.classifier,
                                                            ),
                                                            random.seed
                                                         ) {
                                                            context.property(
                                                               a,
                                                               b,
                                                               c,
                                                               d,
                                                               e,
                                                               f,
                                                               g,
                                                               h,
                                                               i,
                                                               j,
                                                               k,
                                                               l,
                                                               m,
                                                               n,
                                                               o,
                                                               p,
                                                               q
                                                            )
                                                         }
                                                         config.listeners.forEach { it.afterTest() }
                                                      }
                                                   }
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
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
         .zip(genF.generate(random, config.edgeConfig))
         .zip(genG.generate(random, config.edgeConfig))
         .zip(genH.generate(random, config.edgeConfig))
         .zip(genI.generate(random, config.edgeConfig))
         .zip(genJ.generate(random, config.edgeConfig))
         .zip(genK.generate(random, config.edgeConfig))
         .zip(genL.generate(random, config.edgeConfig))
         .zip(genM.generate(random, config.edgeConfig))
         .zip(genN.generate(random, config.edgeConfig))
         .zip(genO.generate(random, config.edgeConfig))
         .zip(genP.generate(random, config.edgeConfig))
         .zip(genQ.generate(random, config.edgeConfig))
         .takeWhile { constraints.evaluate(context) }
         .forEach { (abcdefghijklmnop, q) ->
            val (abcdefghijklmno, p) = abcdefghijklmnop
            val (abcdefghijklmn, o) = abcdefghijklmno
            val (abcdefghijklm, n) = abcdefghijklmn
            val (abcdefghijkl, m) = abcdefghijklm
            val (abcdefghijk, l) = abcdefghijkl
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
            val shrinkfn = shrinkfn(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, property, config.shrinkingMode)
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
                  l.value,
                  m.value,
                  n.value,
                  o.value,
                  p.value,
                  q.value
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
                  genL.classifier,
                  genM.classifier,
                  genN.classifier,
                  genO.classifier,
                  genP.classifier,
                  genQ.classifier
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
                  l.value,
                  m.value,
                  n.value,
                  o.value,
                  p.value,
                  q.value
               )
            }
            config.listeners.forEach { it.afterTest() }
         }
   }

   outputStatistics(context, 12, true)
   context.outputClassifications(12, config, random.seed)
   context.checkMaxSuccess(config, random.seed)
   context.checkMaxDiscards()
   return context
}


