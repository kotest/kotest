package io.kotest.property.core

import io.kotest.common.ExperimentalKotest
import io.kotest.property.AssumptionFailedException
import io.kotest.property.Gen
import io.kotest.property.PropertyTesting
import io.kotest.property.ShrinkingMode
import io.kotest.property.classifications.ClassificationReporter
import io.kotest.property.classifications.StandardClassificationReporter
import io.kotest.property.core.constraints.Constraints
import io.kotest.property.core.constraints.ConstraintsBuilder
import io.kotest.property.core.delegates.GenDelegate
import io.kotest.property.core.delegates.GenDelegateRegistry
import io.kotest.property.core.seeds.createRandomSource
import kotlin.time.Duration

@ExperimentalKotest
class PermutationConfiguration {

   /**
    * Use iteration based [Constraints]
    */
   var iterations: Int = PropertyTesting.defaultIterationCount

   /**
    * Use duration based [Constraints], if not null will override [iterations]
    */
   var duration: Duration? = null

   /**
    * Specify custom [Constraints], if not null will override [iterations] and [duration]
    */
   var constraints: Constraints? = null

   // The minSuccess variable is used to specify the minimum number of successful permutations that must be achieved
   // during property testing. This ensures that a certain number of tests pass before the test suite
   // considers the property test successful.
   var minSuccess: Int = PropertyTesting.defaultMinSuccess

   // The maxFailure variable is used to specify the maximum number of allowable permutations failures during
   // property testing. This ensures that the property test will fail if the number of failed tests exceeds
   // this threshold. It is useful for controlling the tolerance for flaky tests or tests that are expected to
   // have some level of failure.
   var maxFailures: Int = PropertyTesting.defaultMaxFailure

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

   // override the labels reporter used for classifications
   var classificationReporter: ClassificationReporter? = null

   internal val registry = GenDelegateRegistry()

   // callbacks
   internal var beforePermutation: (suspend () -> Unit)? = null
   internal var afterPermutation: (suspend () -> Unit)? = null

   // the main test
   internal var test: (suspend Permutation.() -> Unit)? = null

   fun forEach(test: suspend Permutation.() -> Unit) {
      if (this.test != null) error("test has already been set")
      this.test = test
   }

   fun beforePermutation(fn: suspend () -> Unit) {
      if (this.beforePermutation != null) error("beforePermutation has already been set")
      beforePermutation = fn
   }

   fun afterPermutation(fn: suspend () -> Unit) {
      if (this.afterPermutation != null) error("afterPermutation has already been set")
      afterPermutation = fn
   }

   fun <T> gen(initializer: () -> Gen<T>): GenDelegate<T> {
      val gen = initializer()
      val delegate = GenDelegate(TODO(), gen)
      registry.add(delegate)
      return delegate
   }

   fun assume(assumptions: () -> Unit) {
      try {
         assumptions()
      } catch (e: AssertionError) {
         throw AssumptionFailedException
      }
   }

   fun assume(predicate: Boolean) {
      if (!predicate) throw AssumptionFailedException
   }
}

internal suspend fun PermutationConfiguration.toContext(): PermutationContext {
   return PermutationContext(
      constraints = ConstraintsBuilder.build(this),
      minSuccess = minSuccess,
      maxFailures = maxFailures,
      edgecasesGenerationProbability = edgecasesGenerationProbability,
      shouldPrintShrinkSteps = shouldPrintShrinkSteps,
      shrinkingMode = shrinkingMode,
      maxDiscardPercentage = maxDiscardPercentage,
      shouldPrintGeneratedValues = shouldPrintGeneratedValues,
      outputClassifications = outputClassifications,
      classificationReporter = StandardClassificationReporter,
      shouldPrintConfig = shouldPrintConfig,
      customSeed = this.seed == null,
      failOnSeed = failOnSeed,
      writeFailedSeed = writeFailedSeed,
      rs = createRandomSource(this),
      registry = registry,
      beforePermutation = beforePermutation ?: {},
      afterPermutation = afterPermutation ?: {},
      test = test ?: error("test has not been set"),
   )
}
