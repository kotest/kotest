package io.kotest.permutations.statistics

import io.kotest.permutations.Label

/**
 * Contains configuration options for coverage checks.
 */
class CoverageConfiguration {

   internal val coverageCounts: MutableSet<Triple<Label, Any, Int>> = mutableSetOf()
   internal val coveragePercentages: MutableSet<Triple<Label, Any, Double>> = mutableSetOf()

   /**
    * Asserts that the given [value] was applied to at least [count] number of values.
    *
    * For example, to check that at least 150 values were classified as 'even':
    *
    *       coverage {
    *         count("even", 150)
    *       }
    *
    *       check {
    *         classify(a % 2 == 0, "even")
    *         a + a == 2 * a
    *       }
    *
    */
   fun count(
      value: Any,
      count: Int,
   ) {
      coverageCounts += Triple(Label.Default, value, count)
   }

   fun count(
      label: String,
      value: Any,
      count: Int,
   ) {
      coverageCounts += Triple(Label(label), value, count)
   }

   fun percentage(
      value: Any,
      percent: Double,
   ) {
      coveragePercentages += Triple(Label.Default, value, percent)
   }

   fun percentage(
      label: String,
      value: Any,
      percent: Double,
   ) {
      coveragePercentages += Triple(Label(label), value, percent)
   }
}
