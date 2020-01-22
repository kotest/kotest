package io.kotest.property

import kotlin.random.Random

/**
 * A Generator, or [Gen] is responsible for generating data to be used in testing.
 *
 * Each generator will generate data for a specific type <T>.
 *
 * There are many built in generators, such as for ints, doubles, etc, and there are many
 * methods for manipulating generators, such as map, flatMap and so on.
 *
 * A generator can be converted into an [Arbitrary] by calling [take] on a generator.
 * The returned arbitrary can then be used inside a property test method such as forAll.
 */
interface Gen<T> {

   /**
    * Generates a single random value of type T.
    */
   fun generate(random: Random): T

   /**
    * Returns an optional shrinker for the type T compatible with the values produced by this generator.
    * If this type does not provide shrinking, then this function can return null.
    */
   fun shrinker(): Shrinker<T>? = null

   /**
    * When used in a property testing scenario, edge cases are used as common
    *
    * If edgecases are not applicable for the type T, or this generator is not intended for use in
    * property testing, then this function can return an empty list.
    */
   fun edgecases(): Iterable<T> = emptyList()

   companion object
}

/**
 * Returns a new [Arbitrary] created from this [Gen] with a fixed number of iterations.
 */
fun <T> Gen<T>.take(iterations: Int, mode: ShrinkingMode = ShrinkingMode.Bounded(10)): Arbitrary<T> {
   require(iterations > 0)
   if (iterations > 100000)
      println("Warning: Iteration count is high at $iterations")
   return object : Arbitrary<T> {
      override fun samples(random: Random): Sequence<ArgumentValue<T>> {
         return sequence {
            for (k in 0 until iterations) {
               val value = this@take.generate(random)
               val rtree = this@take.shrinker()?.shrinks(value, mode) ?: RTree(value, emptyList())
               yield(ArgumentValue(value, rtree))
            }
         }
      }
   }
}

/**
 * Returns a new [Gen] where the edge cases of the generator are replaced with the edge
 * cases given as input to this function.
 */
fun <T> Gen<T>.setEdgeCases(vararg edgecases: T): Gen<T> = setEdgeCases(edgecases.asList())

/**
 * Returns a new [Gen] where the edge cases of the generator are replaced with the edge
 * cases given as input to this function.
 */
fun <T> Gen<T>.setEdgeCases(edgecases: Iterable<T>): Gen<T> = object : Gen<T> {
   override fun edgecases(): Iterable<T> = edgecases
   override fun generate(random: Random): T = this@setEdgeCases.generate(random)
   override fun shrinker(): Shrinker<T>? = this@setEdgeCases.shrinker()
}
