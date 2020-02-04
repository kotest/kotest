//@file:Suppress("NOTHING_TO_INLINE")
//
//package io.kotest.property.internal
//
//import io.kotest.properties.shrinking.shrink
//import io.kotest.property.*
//
//suspend fun <A> test1(
//   genA: Arg<A>,
//   config: PropTestConfig,
//   property: suspend PropertyContext.(A) -> Unit
//): PropertyContext {
//
//   val context = PropertyContext()
//   val random = config.seed.random()
//
//   with(context) {
//      genA.generate(random).forEach { a ->
//         try {
//            property(a.value)
//            context.markSuccess()
//         } catch (e: AssertionError) {
//            context.markFailure()
//            if (config.maxFailure == 0) {
//               fail(a, shrink(a, property, config), e, attempts())
//            } else if (failures() > config.maxFailure) {
//               val t = AssertionError("Property failed ${failures()} times (maxFailure rate was ${config.maxFailure})")
//               fail(a, shrink(a, property, config), t, attempts())
//            }
//         }
//      }
//      context.checkMaxSuccess(config)
//   }
//
//   return context
//}
//
//// shrinks a single set of failed inputs returning a tuple of the smallest values
//suspend fun <A> shrink(
//    a: ArgValue<A>,
//    property: suspend PropertyContext.(A) -> Unit,
//    config: PropTestConfig
//): A {
//   // we use a new context for the shrinks, as we don't want to affect classification etc
//   val context = PropertyContext()
//   return with(context) {
//      shrink(a, { it }, config.shrinking)
//   }
//}
//
//// creates an exception for failed, shrunk, values and throws
//fun <A> fail(
//   a: ArgValue<A>,
//   shrink: A,
//   e: Error, // the underlying failure reason,
//   attempts: Int
//) {
//   val inputs = listOf(PropertyFailureInput(a.value, shrink))
//   throw propertyAssertionError(e, attempts, inputs)
//}
