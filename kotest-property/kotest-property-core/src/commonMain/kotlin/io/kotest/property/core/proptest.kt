package io.kotest.property.core

import io.kotest.common.ExperimentalKotest
import io.kotest.property.Gen
import io.kotest.property.PropertyTesting
import io.kotest.property.RandomSource
import io.kotest.property.ShrinkingMode
import io.kotest.property.statistics.Label
import kotlin.random.Random
import kotlin.time.Duration

@ExperimentalKotest
class PermutationContext {

   // use iteration based constraint
   var iterations: Int = PropertyTesting.defaultIterationCount

   // use duration based constraint, if not null will override iterations
   var duration: Duration? = null

   // specify custom constraits, if not null will override iterations and duration
   var constraints: Constraints? = null

   // The minSuccess variable is used to specify the minimum number of successful permutations that must be achieved
   // during property testing. This ensures that a certain number of tests pass before the test suite
   // considers the property test successful.
   var minSuccess: Int = PropertyTesting.defaultMinSuccess

   // The maxFailure variable is used to specify the maximum number of allowable permutations failures during
   // property testing. This ensures that the property test will fail if the number of failed tests exceeds
   // this threshold. It is useful for controlling the tolerance for flaky tests or tests that are expected to
   // have some level of failure.
   var maxFailure: Int = PropertyTesting.defaultMaxFailure

   // The edgecasesGenerationProbability is used to determine the likelihood that a generated
   // value will be an edge case rather than a random sample. This probability is used within
   // the generate method of the Gen class to bias the generation of values towards edge cases.
   var edgecasesGenerationProbability = PropertyTesting.defaultEdgecasesGenerationProbability

   var shouldPrintShrinkSteps = PropertyTesting.shouldPrintShrinkSteps

   var shrinkingMode: ShrinkingMode = PropertyTesting.defaultShrinkingMode

   var maxDiscardPercentage: Int = PropertyTesting.maxDiscardPercentage

   // output each generated value
   var shouldPrintGeneratedValues: Boolean = PropertyTesting.shouldPrintGeneratedValues

   var outputClassifications: Boolean = PropertyTesting.defaultOutputClassifications

   // The shouldPrintConfig variable is used to specify whether the configuration of the property test is printed
   var shouldPrintConfig: Boolean = PropertyTesting.shouldPrintConfig

   // The failOnSeed variable is used to specify whether a property test should fail if a seed is set.
   var failOnSeed: Boolean = PropertyTesting.failOnSeed

   // used to specify whether a seed should be written to the test output if a test fails.
   var writeFailedSeed: Boolean = PropertyTesting.writeFailedSeed

   // Custom seed to use for this property test. If null, a random seed will be generated.
   var seed: Long? = null

   val rs = RandomSource.seeded(seed ?: Random.nextLong())

   internal val registry = GenDelegateRegistry()

   internal val statistics = Statistics()

   // callbacks
   internal var beforePermutation: suspend () -> Unit = {}
   internal var afterPermutation: suspend () -> Unit = {}

   fun assume(predicateFn: () -> Unit) {

   }

   fun assume(predicate: Boolean) {
   }

   fun collect(classification: Any?) {
      collect(null, classification)
   }

   fun collect(label: String, classification: Any?) {
      collect(Label(label), classification)
   }

   private fun collect(label: Label?, classification: Any?) {
//      val stats = statistics.getOrPut(label) { mutableMapOf() }
//      val count = stats.getOrElse(classification) { 0 }
//      stats[classification] = count + 1
   }

   suspend fun forEach(test: suspend EvaluationContextToBeRenamed.() -> Unit) {
      val result = executePropTest(this, test)
      checkMinSuccess(this, result)
   }

   fun beforePermutation(fn: suspend () -> Unit) {
      beforePermutation = fn
   }

   fun afterPermutation(fn: suspend () -> Unit) {
      afterPermutation = fn
   }

   fun <T> gen(initializer: () -> Gen<T>): GenDelegate<T> {
      val gen = initializer()
      val delegate = GenDelegate(rs, gen)
      registry.add(delegate)
      return delegate
   }
}

data class EvaluationContextToBeRenamed(val seed: Long)

class Statistics(
   val statistics: MutableMap<Label, Any> = mutableMapOf()
)
