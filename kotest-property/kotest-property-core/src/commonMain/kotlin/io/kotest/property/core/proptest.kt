package io.kotest.property.core

import io.kotest.mpp.sysprop
import io.kotest.property.AssumptionFailedException
import io.kotest.property.Gen
import io.kotest.property.PropertyTesting
import io.kotest.property.RandomSource
import io.kotest.property.ShrinkingMode
import kotlin.random.Random
import kotlin.time.Duration

class PropTestContext {

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

   // must call seed before rs otherwise has no effect
   var seed: Long? = null
   var failOnSeed: Boolean = sysprop("kotest.proptest.seed.fail-if-set", false)

   internal val rs = lazy { RandomSource.seeded(seed ?: Random.nextLong()) }

   internal val container = GenDelegateContainer()

   internal var property: suspend () -> Unit = {}
   internal var beforeProperty: suspend () -> Unit = {}
   internal var afterProperty: suspend () -> Unit = {}

   fun assume(predicate: () -> Boolean) {

   }

   fun property(fn: suspend () -> Unit) {
      property = fn
   }

   fun beforeProperty(fn: suspend () -> Unit) {
      beforeProperty = fn
   }

   fun afterProperty(fn: suspend () -> Unit) {
      afterProperty = fn
   }

   fun <T> gen(initializer: () -> Gen<T>): GenDelegate<T> {
      val gen = initializer()
      val delegate = GenDelegate(rs.value, gen)
      container.add(delegate)
      return delegate
   }
}
