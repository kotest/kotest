package io.kotest.permutations.delegates

import io.kotest.permutations.Input
import io.kotest.property.Sample

/**
 * The [GenDelegateRegistry] is a container for all generators in a property test.
 * Each time a generator is created from a delegated property, it is registered in the container.
 *
 * This allows us to clear all generators between each property test.
 *
 * It also allows us to obtain the current sample from each generator in order to pass
 * the current values to the shrinking function (if one is defined).
 */
class GenDelegateRegistry {

   val delegates = mutableListOf<GenDelegate<*>>()

   fun add(delegate: GenDelegate<*>) {
      delegates.add(delegate)
   }

   /**
    * Reset all random values for another iteration.
    */
   fun reset() {
      delegates.forEach { it.reset() }
   }

   /**
    * Returns the current sample from each generator.
    */
   fun samples(): List<Sample<Any?>> {
      return delegates.map { it.sample() }
   }

   fun inputs(): List<Input> {
      return delegates.map { it.inputs() }
   }
}
