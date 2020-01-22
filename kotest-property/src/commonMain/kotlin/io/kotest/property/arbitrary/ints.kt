package io.kotest.property.arbitrary


//sealed class IntDistribution {
//
//   abstract fun get(k: Int, iterations: Int, range: LongRange): LongRange
//
//   /**
//    * Splits the range into discrete "blocks" to ensure that random values are distributed
//    * across the entire range in a uniform manner.
//    */
//   object Uniform : IntDistribution() {
//      override fun get(k: Int, iterations: Int, range: LongRange): LongRange {
//         val step = (range.last - range.first) / iterations
//         return (step * k)..(step * (k + 1))
//      }
//   }
//
//   /**
//    * Values are distributed according to the Pareto distribution.
//    * See https://en.wikipedia.org/wiki/Pareto_distribution
//    * Sometimes referred to as the 80-20 rule
//    *
//    * tl;dr - more values are produced at the lower bound than the upper bound.
//    */
//   object Pareto : IntDistribution() {
//      override fun get(k: Int, iterations: Int, range: LongRange): LongRange {
//         // this isn't really the pareto distribution so either implement it properly, or rename this implementation
//         val step = (range.last - range.first) / iterations
//         return 0..(step * k + 1)
//      }
//   }
//}
