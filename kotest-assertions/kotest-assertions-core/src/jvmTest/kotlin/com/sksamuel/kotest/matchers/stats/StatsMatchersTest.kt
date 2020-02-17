package com.sksamuel.kotest.matchers.stats

import io.kotest.data.forAll
import io.kotest.matchers.stats.shouldHaveMean
import io.kotest.matchers.stats.shouldHaveStandardDeviation
import io.kotest.matchers.stats.shouldHaveVariance
import io.kotest.matchers.stats.shouldNotHaveMean
import io.kotest.matchers.stats.shouldNotHaveStandardDeviation
import io.kotest.matchers.stats.shouldNotHaveVariance
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.row
import java.math.BigDecimal

class StatsMatchersTest : StringSpec() {

   init {
      "Mean should be ZERO if collection is empty" {
         emptyList<Int>().shouldHaveMean(BigDecimal("0"))
         emptyList<Int>().shouldHaveMean(BigDecimal(0))
         emptyList<Int>().shouldHaveMean(BigDecimal(0.0))
         emptyList<Int>().shouldHaveMean(0.0)
         emptyList<Int>().shouldHaveMean(0.000000)
         emptyList<Int>().shouldHaveMean(BigDecimal("0"), 5)
         emptyList<Int>().shouldHaveMean(BigDecimal(0), 4)
         emptyList<Int>().shouldHaveMean(BigDecimal(0.0), 3)
         emptyList<Int>().shouldHaveMean(0.0, 2)
         emptyList<Int>().shouldHaveMean(0.000000, 1)
      }

      "Variance should be ZERO if collection is empty" {
         emptyList<Int>().shouldHaveVariance(BigDecimal("0"))
         emptyList<Int>().shouldHaveVariance(BigDecimal(0))
         emptyList<Int>().shouldHaveVariance(BigDecimal(0.0))
         emptyList<Int>().shouldHaveVariance(0.0)
         emptyList<Int>().shouldHaveVariance(0.000000)
         emptyList<Int>().shouldHaveVariance(BigDecimal("0"), 5)
         emptyList<Int>().shouldHaveVariance(BigDecimal(0), 4)
         emptyList<Int>().shouldHaveVariance(BigDecimal(0.0), 3)
         emptyList<Int>().shouldHaveVariance(0.0, 2)
         emptyList<Int>().shouldHaveVariance(0.000000, 1)
      }

      "Standard deviation should be ZERO if collection is empty" {
         emptyList<Int>().shouldHaveStandardDeviation(BigDecimal("0"))
         emptyList<Int>().shouldHaveStandardDeviation(BigDecimal(0))
         emptyList<Int>().shouldHaveStandardDeviation(BigDecimal(0.0))
         emptyList<Int>().shouldHaveStandardDeviation(0.0)
         emptyList<Int>().shouldHaveStandardDeviation(0.000000)
         emptyList<Int>().shouldHaveStandardDeviation(BigDecimal("0"), 5)
         emptyList<Int>().shouldHaveStandardDeviation(BigDecimal(0), 4)
         emptyList<Int>().shouldHaveStandardDeviation(BigDecimal(0.0), 3)
         emptyList<Int>().shouldHaveStandardDeviation(0.0, 2)
         emptyList<Int>().shouldHaveStandardDeviation(0.000000, 1)
      }

      "Collection<Int> should have correct mean value with default precision" {
         forAll(
            row(1.0, listOf(1)),
            row(0.0, listOf<Int>()),
            row(0.0, listOf(0)),
            row(0.0, listOf(-1, 1)),
            row(-0.0, listOf<Int>()),
            row(-0.0, listOf(0)),
            row(-0.0, listOf(-1, 1)),
            row(2.0, listOf(1, 3)),
            row(-2.0, listOf(-1, -3)),
            row(20.0, listOf(20, 10, 30)),
            row(2.000, listOf(1, 3)),
            row(0.3333, listOf(1, 0, 0)),
            row(1.3333, listOf(1, 2, 1)),
            row(266.6667, listOf(1, 2, 797)),
            row(1.5, listOf(1, 2)),
            row(0.0, listOf(-1, 1)),
            row(0.0909, listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1))
         ) { expectedMean: Double, value: Collection<Int> ->
            value.shouldHaveMean(BigDecimal(expectedMean.toString()))
            value.shouldHaveMean(expectedMean)
         }
      }

      "Collection<Int> should NOT have correct mean value with default precision" {
         forAll(
            row(1.0, listOf(-1, 1)),
            row(0.1, listOf(-1, 1)),
            row(0.01, listOf(-1, 1)),
            row(0.001, listOf(-1, 1)),
            row(0.0001, listOf(-1, 1)),
            row(0.00001, listOf(-1, 1)),
            row(0.000001, listOf(-1, 1)),
            row(0.0000001, listOf(-1, 1)),
            row(0.00000001, listOf(-1, 1)),
            row(0.000000001, listOf(-1, 1)),
            row(0.0000000001, listOf(-1, 1)),
            row(266.6666, listOf(1, 2, 797)),
            row(266.6676, listOf(1, 2, 797)),
            row(266.6665, listOf(1, 2, 797)),
            row(266.66666, listOf(1, 2, 797)),
            row(266.66667, listOf(1, 2, 797)),
            row(266.66665, listOf(1, 2, 797)),
            row(266.666, listOf(1, 2, 797)),
            row(266.667, listOf(1, 2, 797)),
            row(266.665, listOf(1, 2, 797))
         ) { wrongMean: Double, value: Collection<Int> ->
            value.shouldNotHaveMean(BigDecimal(wrongMean.toString()))
            value.shouldNotHaveMean(wrongMean)
         }
      }

      "Collection<Int> should have correct mean with specific precision" {
         forAll(
            row(266.6666666667, 10, listOf(1, 2, 797)),
            row(266.666667, 6, listOf(1, 2, 797)),
            row(266.66667, 5, listOf(1, 2, 797)),
            row(266.6667, 4, listOf(1, 2, 797)),
            row(266.667, 3, listOf(1, 2, 797)),
            row(266.67, 2, listOf(1, 2, 797)),
            row(266.7, 1, listOf(1, 2, 797)),
            row(267.0, 0, listOf(1, 2, 797))
         ) { mean: Double, precision: Int, value: Collection<Int> ->
            value.shouldHaveMean(BigDecimal(mean.toString()), precision)
            value.shouldHaveMean(mean, precision)
         }
      }

      "Collection<Double> should have correct mean value with default precision" {
         forAll(
            row(1.0, listOf(1.0)),
            row(0.0, listOf()),
            row(0.0, listOf(0.0)),
            row(0.0, listOf(-1.0, 1.0)),
            row(-0.0, listOf()),
            row(-0.0, listOf(0.0)),
            row(-0.0, listOf(-1.0, 1.0)),
            row(2.0, listOf(1.0, 3.0)),
            row(-2.0, listOf(-1.0, -3.0)),
            row(20.0, listOf(20.0, 10.0, 30.0)),
            row(2.000, listOf(1.0, 3.0)),
            row(0.3333, listOf(1.0, 0.0, 0.0)),
            row(1.3333, listOf(1.0, 2.0, 1.0)),
            row(266.6667, listOf(1.0, 2.0, 797.0)),
            row(1.5, listOf(1.0, 2.0)),
            row(0.0, listOf(-1.0, 1.0)),
            row(0.0909, listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0))
         ) { expectedMean: Double, value: Collection<Double> ->
            value.shouldHaveMean(BigDecimal(expectedMean.toString()))
            value.shouldHaveMean(expectedMean)
         }
      }

      "Collection<Double> should NOT have correct mean value with default precision" {
         forAll(
            row(1.0, listOf(-1.0, 1.0)),
            row(0.1, listOf(-1.0, 1.0)),
            row(0.01, listOf(-1.0, 1.0)),
            row(0.001, listOf(-1.0, 1.0)),
            row(0.0001, listOf(-1.0, 1.0)),
            row(0.00001, listOf(-1.0, 1.0)),
            row(0.000001, listOf(-1.0, 1.0)),
            row(0.0000001, listOf(-1.0, 1.0)),
            row(0.00000001, listOf(-1.0, 1.0)),
            row(0.000000001, listOf(-1.0, 1.0)),
            row(0.0000000001, listOf(-1.0, 1.0)),
            row(266.6666, listOf(1.0, 2.0, 797.0)),
            row(266.6676, listOf(1.0, 2.0, 797.0)),
            row(266.6665, listOf(1.0, 2.0, 797.0)),
            row(266.66666, listOf(1.0, 2.0, 797.0)),
            row(266.66667, listOf(1.0, 2.0, 797.0)),
            row(266.66665, listOf(1.0, 2.0, 797.0)),
            row(266.666, listOf(1.0, 2.0, 797.0)),
            row(266.667, listOf(1.0, 2.0, 797.0)),
            row(266.665, listOf(1.0, 2.0, 797.0))
         ) { wrongMean: Double, value: Collection<Double> ->
            value.shouldNotHaveMean(BigDecimal(wrongMean.toString()))
            value.shouldNotHaveMean(wrongMean)
         }
      }

      "Collection<Double> should have correct mean with specific precision" {
         forAll(
            row(266.6666666667, 10, listOf(1.0, 2.0, 797.0)),
            row(266.666667, 6, listOf(1.0, 2.0, 797.0)),
            row(266.66667, 5, listOf(1.0, 2.0, 797.0)),
            row(266.6667, 4, listOf(1.0, 2.0, 797.0)),
            row(266.667, 3, listOf(1.0, 2.0, 797.0)),
            row(266.67, 2, listOf(1.0, 2.0, 797.0)),
            row(266.7, 1, listOf(1.0, 2.0, 797.0)),
            row(267.0, 0, listOf(1.0, 2.0, 797.0))
         ) { mean: Double, precision: Int, value: Collection<Double> ->
            value.shouldHaveMean(BigDecimal(mean.toString()), precision)
            value.shouldHaveMean(mean, precision)
         }
      }

      "Collection<Number> should have correct mean with mix types" {
         forAll(
            row(266.6666666667, 10, listOf<Number>(1.0f, 2, 797.0)),
            row(3.43333, 5, listOf<Number>(1, 2.0, 3.1f, 4L, 5.toBigInteger(), BigDecimal("5.5")))
         ) { mean: Double, precision: Int, value: Collection<Number> ->
            value.shouldHaveMean(BigDecimal(mean.toString()), precision)
            value.shouldHaveMean(mean, precision)
         }
      }

      "Collection<Int> should have correct variance value with default precision" {
         forAll(
            row(0.6667, listOf(1, 3, 2)),
            row(0.6667, listOf(-1, -3, -2)),
            row(4.2222, listOf(-1, 2, -3)),
            row(0.0, listOf(1))
         ) { variance: Double, collection: Collection<Int> ->
            collection.shouldHaveVariance(BigDecimal(variance.toString()))
            collection.shouldHaveVariance(variance)
         }
      }

      "Collection<Int> should NOT have correct variance value with default precision" {
         forAll(
            row(0.1234, listOf(1, 3, 2)),
            row(0.6666, listOf(-1, -3, -2)),
            row(4.0, listOf(-1, 2, -3)),
            row(0.00000001, listOf(1))
         ) { variance: Double, collection: Collection<Int> ->
            collection.shouldNotHaveVariance(BigDecimal(variance.toString()))
            collection.shouldNotHaveVariance(variance)
         }
      }

      "Collection<Int> should have correct variance value with specific precision" {
         forAll(
            row(1.0, 0, listOf(1, 3, 2)),
            row(0.7, 1, listOf(1, 3, 2)),
            row(0.67, 2, listOf(1, 3, 2)),
            row(0.667, 3, listOf(1, 3, 2)),
            row(0.6667, 4, listOf(1, 3, 2)),
            row(0.66667, 5, listOf(1, 3, 2))
         ) { variance: Double, precision: Int, collection: Collection<Int> ->
            collection.shouldHaveVariance(BigDecimal(variance.toString()), precision)
            collection.shouldHaveVariance(variance, precision)
         }
      }

      "Collection<Double> should have correct variance value with default precision" {
         forAll(
            row(0.6667, listOf(1.0, 3.0, 2.0)),
            row(0.6667, listOf(-1.0, -3.0, -2.0)),
            row(4.2222, listOf(-1.0, 2.0, -3.0)),
            row(0.0, listOf(1.0))
         ) { variance: Double, collection: Collection<Double> ->
            collection.shouldHaveVariance(BigDecimal(variance.toString()))
            collection.shouldHaveVariance(variance)
         }
      }

      "Collection<Double> should NOT have correct variance value with default precision" {
         forAll(
            row(0.1234, listOf(1.0, 3.0, 2.0)),
            row(0.6666, listOf(-1.0, -3.0, -2.0)),
            row(4.0, listOf(-1.0, 2.0, -3.0)),
            row(0.00000001, listOf(1.0))
         ) { variance: Double, collection: Collection<Double> ->
            collection.shouldNotHaveVariance(BigDecimal(variance.toString()))
            collection.shouldNotHaveVariance(variance)
         }
      }

      "Collection<Double> should have correct variance value with specific precision" {
         forAll(
            row(1.0, 0, listOf(1.0, 3.0, 2.0)),
            row(0.7, 1, listOf(1.0, 3.0, 2.0)),
            row(0.67, 2, listOf(1.0, 3.0, 2.0)),
            row(0.667, 3, listOf(1.0, 3.0, 2.0)),
            row(0.6667, 4, listOf(1.0, 3.0, 2.0)),
            row(0.66667, 5, listOf(1.0, 3.0, 2.0))
         ) { variance: Double, precision: Int, collection: Collection<Double> ->
            collection.shouldHaveVariance(BigDecimal(variance.toString()), precision)
            collection.shouldHaveVariance(variance, precision)
         }
      }

      "Collection<Int> should have correct standard deviation value with default precision" {
         forAll(
            row(0.8165, listOf(1, 3, 2)),
            row(0.8165, listOf(-1, -3, -2)),
            row(2.0548, listOf(-1, 2, -3)),
            row(0.0, listOf(1))
         ) { standardDeviation: Double, collection: Collection<Int> ->
            collection.shouldHaveStandardDeviation(BigDecimal(standardDeviation.toString()))
            collection.shouldHaveStandardDeviation(standardDeviation)
         }
      }

      "Collection<Int> should NOT have correct standard deviation value with default precision" {
         forAll(
            row(0.8165, listOf(1, 3, 2, 4)),
            row(0.81, listOf(-1, -3, -2, 0)),
            row(2.0548, listOf(-1, 2)),
            row(0.0, listOf(10, 1))
         ) { standardDeviation: Double, collection: Collection<Int> ->
            collection.shouldNotHaveStandardDeviation(BigDecimal(standardDeviation.toString()))
            collection.shouldNotHaveStandardDeviation(standardDeviation)
         }
      }

      "Collection<Int> should have correct standard deviation value with specific precision" {
         forAll(
            row(1.0, 0, listOf(1, 3, 2)),
            row(0.8, 1, listOf(1, 3, 2)),
            row(0.82, 2, listOf(1, 3, 2)),
            row(0.816, 3, listOf(1, 3, 2)),
            row(2.0, 0, listOf(-1, 2, -3)),
            row(2.1, 1, listOf(-1, 2, -3)),
            row(2.05, 2, listOf(-1, 2, -3)),
            row(2.055, 3, listOf(-1, 2, -3))
         ) { standardDeviation: Double, precision: Int, collection: Collection<Int> ->
            collection.shouldHaveStandardDeviation(BigDecimal(standardDeviation.toString()), precision)
            collection.shouldHaveStandardDeviation(standardDeviation, precision)
         }
      }

      "Collection<Double> should have correct standard deviation value with default precision" {
         forAll(
            row(0.8165, listOf(1.0, 3.0, 2.0)),
            row(0.8165, listOf(-1.0, -3.0, -2.0)),
            row(2.0548, listOf(-1.0, 2.0, -3.0)),
            row(0.0, listOf(1.0))
         ) { standardDeviation: Double, collection: Collection<Double> ->
            collection.shouldHaveStandardDeviation(BigDecimal(standardDeviation.toString()))
            collection.shouldHaveStandardDeviation(standardDeviation)
         }
      }

      "Collection<Double> should NOT have correct standard deviation value with default precision" {
         forAll(
            row(0.8165, listOf(1.0, 3.0, 2.0, 4.0)),
            row(0.81, listOf(-1.0, -3.0, -2.0, 0.0)),
            row(2.0548, listOf(-1.0, 2.0)),
            row(0.0, listOf(10.0, 1.0))
         ) { standardDeviation: Double, collection: Collection<Double> ->
            collection.shouldNotHaveStandardDeviation(BigDecimal(standardDeviation.toString()))
            collection.shouldNotHaveStandardDeviation(standardDeviation)
         }
      }

      "Collection<Double> should have correct standard deviation value with specific precision" {
         forAll(
            row(1.0, 0, listOf(1.0, 3.0, 2.0)),
            row(0.8, 1, listOf(1.0, 3.0, 2.0)),
            row(0.82, 2, listOf(1.0, 3.0, 2.0)),
            row(0.816, 3, listOf(1.0, 3.0, 2.0)),
            row(2.0, 0, listOf(-1.0, 2.0, -3.0)),
            row(2.1, 1, listOf(-1.0, 2.0, -3.0)),
            row(2.05, 2, listOf(-1.0, 2.0, -3.0)),
            row(2.055, 3, listOf(-1.0, 2.0, -3.0))
         ) { standardDeviation: Double, precision: Int, collection: Collection<Double> ->
            collection.shouldHaveStandardDeviation(BigDecimal(standardDeviation.toString()), precision)
            collection.shouldHaveStandardDeviation(standardDeviation, precision)
         }
      }

      "check matchers with big numbers" {
         forAll(
            row(
               BigDecimal("493833345678.55532725"),
               BigDecimal("243859180040573015450155.6869068511"),
               BigDecimal("493820999999.56767275"),
               listOf(
                  12345678.9876545,
                  987654345678.123
               ))
         ) { mean, variance, standardDeviation, collection: Collection<Number> ->
            collection.shouldHaveMean(mean, 10)
            collection.shouldHaveVariance(variance, 10)
            collection.shouldHaveStandardDeviation(standardDeviation, 10)
         }
      }
   }
}
