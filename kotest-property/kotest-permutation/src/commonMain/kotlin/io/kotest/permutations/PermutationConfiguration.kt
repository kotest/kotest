package io.kotest.permutations

import io.kotest.common.ExperimentalKotest
import io.kotest.permutations.constraints.Constraints
import io.kotest.permutations.constraints.ConstraintsBuilder
import io.kotest.permutations.delegates.GenDelegate
import io.kotest.permutations.delegates.GenDelegateRegistry
import io.kotest.permutations.seeds.SeedOperations
import io.kotest.permutations.statistics.DefaultStatisticsReporter
import io.kotest.permutations.statistics.StatisticsReporter
import io.kotest.property.AssumptionFailedException
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

   var outputStatistics: Boolean = PropertyTesting.defaultOutputClassifications

   // The shouldPrintConfig variable is used to specify whether the configuration of the property test is printed
   var shouldPrintConfig: Boolean = PropertyTesting.shouldPrintConfig

   // The failOnSeed variable is used to specify whether a property test should fail if a seed is set.
   var failOnSeed: Boolean = PropertyTesting.failOnSeed

   // used to specify whether a seed should be written to the test output if a test fails.
   var writeFailedSeed: Boolean = PropertyTesting.writeFailedSeed

   // Custom seed to use for this property test. If null, a random seed will be used.
   var seed: Long? = null

   // override the reporter used for statistics
   var statisticsReporter: StatisticsReporter? = null

   var statisticsReportMode: StatisticsReportMode = PropertyTesting.statisticsReportMode

   internal val registry = GenDelegateRegistry()

   // callbacks
   internal var beforePermutation: (suspend () -> Unit)? = null
   internal var afterPermutation: (suspend () -> Unit)? = null

   // the main test
   internal var test: (suspend Permutation.() -> Unit)? = null

   var requiredCoverageCounts: Map<Any?, Int> = emptyMap()
   var requiredCoveragePercentages: Map<Any?, Double> = emptyMap()

   /**
    * Registers the permutation test to execute.
    */
   fun forEach(test: suspend Permutation.() -> Unit) {
      if (this.test != null) error("forEach has already been set")
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

   /**
    * Register a generator with this permutation test.
    */
   fun <T> gen(fn: () -> Gen<T>): GenDelegate<T> {
      val delegate = GenDelegate(fn(), shouldPrintGeneratedValues)
      registry.add(delegate)
      return delegate
   }

   /**
    * Adds an assumption to the test. If the assumption function throws an [AssertionError],
    * that permutation is discarded.
    */
   fun assume(assumptions: () -> Unit) {
      try {
         assumptions()
      } catch (e: AssertionError) {
         throw AssumptionFailedException
      }
   }

   /**
    * Adds an assumption to the test. If the [predicate] is false, that permutation is discarded.
    */
   fun assume(predicate: Boolean) {
      if (!predicate) throw AssumptionFailedException
   }

   /**
    * Asserts that the given [classification] was applied to at least [percentage] number of permutations.
    *
    * For example, to check that at least 25% of the iterations where classified as 'even':
    *
    *       requireCoveragePercentage("even", 25.0)
    *
    *       forEach {
    *          classify(a % 2 == 0, "even")
    *          a + a == 2 * a
    *       }
    *
    */
   fun requireCoveragePercentage(
      classification: Any?,
      percentage: Double,
   ) {
      requireCoveragePercentages(mapOf(classification to percentage))
   }

   /**
    * Asserts that the given classifications percentages were statisfied.
    *
    * For example, to check that at least 25% of the iterations where classified as 'even':
    *
    *       requireCoveragePercentages("even", 25.0)
    *
    *       forEach {
    *          classify(a % 2 == 0, "even")
    *          a + a == 2 * a
    *       }
    *
    */
   fun requireCoveragePercentages(
      classifications: Map<Any?, Double>,
   ) {
      requiredCoveragePercentages = requiredCoveragePercentages + classifications
//      val stats = context.statistics()[null] ?: emptyMap()
//      classifications.forEach { (classification, min) ->
//         val count = stats[classification] ?: 0
//         val attempts = context.attempts()
//         val actual = (count.toDouble() / attempts.toDouble()) * 100.0
//         if (actual < min)
//            fail("Required coverage of $min% for [${classification}] but was [${actual.toInt()}%]")
//      }
//      return context
   }

   /**
    * Asserts that the given [classification] was applied to at least [count] number of permutations.
    *
    * For example, to check that at least 150 of the iterations were classified as 'even':
    *
    *       requireCoverageCount("even", 150)
    *
    *       forEach {
    *             classify(a % 2 == 0, "even")
    *             a + a == 2 * a
    *       }
    *
    */
   fun requireCoverageCount(
      classification: Any?,
      count: Int,
   ) {
      requireCoverageCounts(mapOf(classification to count))
   }

   /**
    * Asserts that the given classifications were statisfied.
    *
    * For example, to check that at least 150 of the iterations were classified as 'even', and 200 were 'positive':
    *
    *       requireCoverageCounts("even", 150, "positive", 200)
    *
    *       forEach {
    *             classify(a % 2 == 0, "even")
    *             a + a == 2 * a
    *       }
    *
    */
   fun requireCoverageCounts(
      classifications: Map<Any?, Int>,
   ) {
      requiredCoverageCounts = requiredCoverageCounts + classifications
//      val context = f()
//      val stats = context.statistics()[null] ?: emptyMap()
//      classifications.forEach { (classification, min) ->
//         val actual = stats[classification] ?: 0
//         if (actual < min)
//            fail("Required coverage of $min for [${classification}] but was [${actual}]")
//      }
//      return context
   }

   fun from(default: PermutationConfiguration) {
      this.maxFailures = default.maxFailures
      this.minSuccess = default.minSuccess
      this.edgecasesGenerationProbability = default.edgecasesGenerationProbability
      this.shouldPrintShrinkSteps = default.shouldPrintShrinkSteps
      this.shrinkingMode = default.shrinkingMode
      this.maxDiscardPercentage = default.maxDiscardPercentage
      this.shouldPrintGeneratedValues = default.shouldPrintGeneratedValues
      this.outputStatistics = default.outputStatistics
      this.statisticsReporter = default.statisticsReporter
      this.statisticsReportMode = default.statisticsReportMode
      this.requiredCoveragePercentages = default.requiredCoveragePercentages
      this.requiredCoverageCounts = default.requiredCoverageCounts
      this.shouldPrintConfig = default.shouldPrintConfig
      this.failOnSeed = default.failOnSeed
      this.writeFailedSeed = default.writeFailedSeed
      this.seed = default.seed
      this.constraints = default.constraints
      this.iterations = default.iterations
   }
}

suspend fun PermutationConfiguration.toContext(): PermutationContext {
   return PermutationContext(
      constraints = ConstraintsBuilder.build(this),
      minSuccess = minSuccess,
      maxFailures = maxFailures,
      edgecasesGenerationProbability = edgecasesGenerationProbability,
      printShrinkSteps = shouldPrintShrinkSteps,
      shrinkingMode = shrinkingMode,
      maxDiscardPercentage = maxDiscardPercentage,
      printGeneratedValues = shouldPrintGeneratedValues,
      outputStatistics = outputStatistics,
      statisticsReporter = statisticsReporter ?: DefaultStatisticsReporter,
      statisticsReportMode = statisticsReportMode,
      requiredCoveragePercentages = requiredCoveragePercentages,
      requiredCoverageCounts = requiredCoverageCounts,
      printConfig = shouldPrintConfig,
      customSeed = this.seed != null,
      failOnSeed = failOnSeed,
      writeFailedSeed = writeFailedSeed,
      rs = SeedOperations.createRandomSource(this),
      registry = registry,
      beforePermutation = beforePermutation ?: {},
      afterPermutation = afterPermutation ?: {},
      test = test ?: error("test has not been set"),
   )
}
