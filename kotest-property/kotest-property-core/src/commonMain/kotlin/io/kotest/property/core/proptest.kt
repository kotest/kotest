package io.kotest.property.core

import io.kotest.mpp.sysprop
import io.kotest.property.Gen
import io.kotest.property.PropertyTesting
import io.kotest.property.RandomSource
import io.kotest.property.ShrinkingMode
import kotlin.random.Random
import kotlin.time.Duration

class PermutationContext {

   var iterations: Int? = null // converted to iteration based constraint
   var duration: Duration? = null // converted to duration based constraint
   var constraints: Constraints? = null // specify manual constraits
   var minSuccess = -1
   var maxFailure = -1
   var edgecaseFactor = PropertyTesting.defaultEdgecasesGenerationProbability
   var shouldPrintShrinkSteps = false // sysprop("kotest.proptest.output.shrink-steps", true)
   var shrinkingMode: ShrinkingMode = PropertyTesting.defaultShrinkingMode
   var maxDiscardPercentage: Int = 20
   var shouldPrintGeneratedValues: Boolean = PropertyTesting.shouldPrintGeneratedValues

   var seed: Long? = null
   var failOnSeed: Boolean = sysprop("kotest.proptest.seed.fail-if-set", false)

   internal val rs = lazy { RandomSource.seeded(seed ?: Random.nextLong()) }

   internal val container = GenDelegateContainer()

   internal var beforePermutation: suspend () -> Unit = {}
   internal var afterPermutation: suspend () -> Unit = {}

   fun assume(predicate: () -> Boolean) {

   }

   fun <T> gen(initializer: () -> Gen<T>): GenDelegate<T> {
      val gen = initializer()
      val delegate = GenDelegate(rs.value, gen)
      container.add(delegate)
      return delegate
   }
}
