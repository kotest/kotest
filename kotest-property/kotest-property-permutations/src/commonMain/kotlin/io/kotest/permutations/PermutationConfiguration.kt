@file:OptIn(ExperimentalKotest::class)

package io.kotest.permutations

import io.kotest.common.ExperimentalKotest
import io.kotest.permutations.constraints.Constraints
import io.kotest.permutations.constraints.ConstraintsBuilder
import io.kotest.permutations.delegates.GenDelegate
import io.kotest.permutations.delegates.GenDelegateRegistry
import io.kotest.permutations.seeds.SeedOperations
import io.kotest.permutations.statistics.CoverageConfiguration
import io.kotest.property.Gen
import io.kotest.property.PropertyTesting
import io.kotest.property.ShrinkingMode
import io.kotest.property.statistics.StatisticsReportMode
import kotlin.time.Duration

@ExperimentalKotest
class PermutationConfiguration {

   /**
    * Use iteration based [Constraints]
    */
   var iterations: Int = PermutationTesting.defaultIterationCount

   /**
    * Use duration based [Constraints].
    * If this is specified, it will override [iterations].
    */
   var duration: Duration? = null

   /**
    * Specify custom [Constraints].
    * If this is specified, it will override [iterations] and [duration]
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

   var discardCheckThreshold: Int = PropertyTesting.discardCheckThreshold

   // output each generated value
   var shouldPrintGeneratedValues: Boolean = PropertyTesting.shouldPrintGeneratedValues

   var outputStatistics: Boolean = PropertyTesting.defaultOutputClassifications

   // The shouldPrintConfig variable is used to specify whether the configuration of the property test is printed
   var shouldPrintConfig: Boolean = PropertyTesting.shouldPrintConfig

   // The failOnSeed variable is used to specify whether a property test should fail if a seed is set.
   var failOnSeed: Boolean = PropertyTesting.failOnSeed

   // used to specify whether a seed should be written to the test output if a test fails.
   var writeFailedSeed: Boolean = PropertyTesting.writeFailedSeed

   // Custom seed to use for this property test. If null, a random seed will be used.
   var seed: Long? = null

   var statisticsReportMode: StatisticsReportMode = PropertyTesting.statisticsReportMode

   internal val registry = GenDelegateRegistry()

   // callbacks
   internal var beforePermutation: (suspend () -> Unit)? = null
   internal var afterPermutation: (suspend () -> Unit)? = null

   internal var coverage: (CoverageConfiguration.() -> Unit)? = null

   // the main test
   internal var test: (suspend Permutation.() -> Unit)? = null

   /**
    * Registers the test logic to execute.
    */
   fun check(test: suspend Permutation.() -> Unit) {
      if (this.test != null) error("check has already been set")
      this.test = test
   }

   /**
    * Registers a set of coverage checks for this permutation test.
    * Can only be set once; calling this method more than once will throw an error.
    *
    * The coverage checks specifed in the provided lambda will be executed once all permutations have completed.
    */
   fun coverage(fn: CoverageConfiguration.() -> Unit) {
      if (this.coverage != null) error("coverage has already been set")
      this.coverage = fn
   }

   /**
    * Registers a callback to be invoked before each permutation iteration.
    *
    * Can only be set once; calling this method more than once will throw an error.
    */
   fun before(fn: suspend () -> Unit) {
      if (this.beforePermutation != null) error("before has already been set")
      beforePermutation = fn
   }

   /**
    * Registers a callback to be invoked after each permutation iteration.
    *
    * Can only be set once; calling this method more than once will throw an error.
    */
   fun after(fn: suspend () -> Unit) {
      if (this.afterPermutation != null) error("after has already been set")
      afterPermutation = fn
   }

   /**
    * Register a generator with this permutation test.
    */
   fun <T> gen(fn: () -> Gen<T>): GenDelegate<T> {
      val delegate = GenDelegate(fn(), shouldPrintGeneratedValues)
      registry.add(delegate)
      return delegate
   }

   /**
    * Applies the given [other] configuration to this configuration.
    */
   fun from(other: PermutationConfiguration) {
      this.maxFailures = other.maxFailures
      this.minSuccess = other.minSuccess
      this.edgecasesGenerationProbability = other.edgecasesGenerationProbability
      this.shouldPrintShrinkSteps = other.shouldPrintShrinkSteps
      this.shrinkingMode = other.shrinkingMode
      this.maxDiscardPercentage = other.maxDiscardPercentage
      this.shouldPrintGeneratedValues = other.shouldPrintGeneratedValues
      this.outputStatistics = other.outputStatistics
      this.statisticsReportMode = other.statisticsReportMode
      this.shouldPrintConfig = other.shouldPrintConfig
      this.failOnSeed = other.failOnSeed
      this.writeFailedSeed = other.writeFailedSeed
      this.seed = other.seed
      this.constraints = other.constraints
      this.iterations = other.iterations
   }
}

/**
 * Returns an immutable [PermutationContext] from this configuration.
 *
 * This immutable object will be used by the [PermutationExecutor] to execute
 * each permutation with the provided configuration.
 */
suspend fun PermutationConfiguration.toContext(): PermutationContext {
   return PermutationContext(
      constraints = ConstraintsBuilder.build(this),
      minSuccess = minSuccess,
      maxFailures = maxFailures,
      edgecasesGenerationProbability = edgecasesGenerationProbability,
      printShrinkSteps = shouldPrintShrinkSteps,
      shrinkingMode = shrinkingMode,
      maxDiscardPercentage = maxDiscardPercentage,
      discardCheckThreshold = discardCheckThreshold,
      printGeneratedValues = shouldPrintGeneratedValues,
      outputStatistics = outputStatistics,
      statisticsReportMode = statisticsReportMode,
      printConfig = shouldPrintConfig,
      customSeed = this.seed != null,
      failOnSeed = failOnSeed,
      writeFailedSeed = writeFailedSeed,
      rs = SeedOperations.createRandomSource(this),
      registry = registry,
      coverage = coverage?.let { CoverageConfiguration().apply(it) } ?: CoverageConfiguration(),
      classifications = Classifications(),
      beforePermutation = beforePermutation ?: {},
      afterPermutation = afterPermutation ?: {},
      test = test ?: error("test has not been set"),
   )
}
