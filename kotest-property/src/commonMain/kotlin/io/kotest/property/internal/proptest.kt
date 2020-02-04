package io.kotest.property.internal

import io.kotest.property.Gen
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyContext
import io.kotest.property.random

suspend fun <A> proptest(
   genA: Gen<A>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A) -> Unit
): PropertyContext {

   val context = PropertyContext()
   val random = config.seed.random()

   genA.generate(random).forEach { a ->
      val shrinkfn = shrinkfn(a, property)
      test(context, config, shrinkfn, listOf(a.value)) {
         context.property(a.value)
      }
   }
   context.checkMaxSuccess(config)
   return context
}


suspend fun <A, B> proptest(
   genA: Gen<A>,
   genB: Gen<B>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A, B) -> Unit
): PropertyContext {

   val context = PropertyContext()
   val random = config.seed.random()

   genA.generate(random).forEach { a ->
      genB.generate(random).forEach { b ->
         val shrinkfn = shrinkfn(a, b, property)
         test(context, config, shrinkfn, listOf(a.value, b.value)) {
            context.property(a.value, b.value)
         }
      }
   }
   context.checkMaxSuccess(config)
   return context
}

suspend fun <A, B, C> proptest(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   config: PropTestConfig,
   property: suspend PropertyContext.(A, B, C) -> Unit
): PropertyContext {

   val context = PropertyContext()
   val random = config.seed.random()

   genA.generate(random).forEach { a ->
      genB.generate(random).forEach { b ->
         genC.generate(random).forEach { c ->
            val shrinkfn = shrinkfn(a, b, c, property)
            test(context, config, shrinkfn, listOf(a.value, b.value)) {
               context.property(a.value, b.value, c.value)
            }
         }
      }
   }
   context.checkMaxSuccess(config)
   return context
}
