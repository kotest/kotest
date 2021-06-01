package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.sequences.shouldHaveAtLeastSize
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll

class DoubleTest : FunSpec({
   test("Numeric Doubles should generate negative values by default") {
      Arb.numericDouble()
         .take(10_000)
         .filter { it < 0 }
         .distinct()
         .shouldHaveAtLeastSize(100)
   }

   test("Double should generate corner cases, positive and negative values") {
      // Is there a better way to do this?
      // "proptest" does quite a lot on top of the Gen instance.
      // That's why I'm reluctant to use generate directly.
      val values: List<Double> = mutableListOf<Double>().also { vs ->
         checkAll(5000, Arb.double()) { x -> vs += x }
      }
      values shouldContain -0.0
      values shouldContain 0.0
      values shouldContain 1.0
      values shouldContain -1.0
      values shouldContain Double.MIN_VALUE
      values shouldContain -Double.MIN_VALUE
      values shouldContain -Double.MAX_VALUE
      values shouldContain Double.MAX_VALUE
      values shouldContain Double.POSITIVE_INFINITY
      values shouldContain Double.NEGATIVE_INFINITY
      values shouldContain Double.NaN

      // The results of these counts follow a binomial distribution with
      // n = 5000 and p = 0.5 (almost, due to NaN and the offset of 1.0).
      // The probability of the conditions to be true is so close to one
      // that Wolfram Alpha does actually print "1".
      // https://www.wolframalpha.com/input/?i=prob+x+%3E+2000+for+x+binomial+with+n+%3D+5000+and+p+%3D+0.5

      values.count { it > 1.0 } shouldBeGreaterThan 2000
      values.count { it < -1.0 } shouldBeGreaterThan 2000
   }
})
