package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.RandomSource
import io.kotest.property.Shrinker
import io.kotest.property.arbitrary.IntShrinker
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.bind
import io.kotest.property.checkAll

class BindShrinkTest : StringSpec({
   data class MaximumComponents(
      val a: Int, val b: Int, val c: Int, val d: Int, val e: Int,
      val f: Int, val g: Int, val h: Int, val i: Int, val j: Int,
      val k: Int, val l: Int, val m: Int, val n: Int
   )

   fun createArb(shrinker: Shrinker<Int>): Arb<MaximumComponents> {
      val intArb = arbitrary(shrinker) { 1000 }

      return Arb.bind(
         intArb, intArb, intArb, intArb, intArb,
         intArb, intArb, intArb, intArb, intArb,
         intArb, intArb, intArb, intArb
      ) { a, b, c, d, e, f, g, h, i, j, k, l, m, n ->
         MaximumComponents(a, b, c, d, e, f, g, h, i, j, k, l, m, n)
      }
   }

   "Arb.bind shrinks all components" {
      val arb = createArb { i -> listOf(0, i / 2, i - 1) }
      val sample = arb.sample(RandomSource.default())

      // Shrinker produces three new values for each component
      sample.shrinks.children.value shouldHaveSize 3 * 14
   }

   "Shrinks all components to minimum value" {
      val arb = createArb(IntShrinker(0..1000))

      val stdout = captureStandardOut {
         shouldThrowAny {
            checkAll(PropTestConfig(seed = 0), arb) {
               it.m shouldBeLessThan 100
            }
         }
      }

      stdout shouldContain """Shrink result (after 45 shrinks) => MaximumComponents(a=0, b=0, c=0, d=0, e=0, f=0, g=0, h=0, i=0, j=0, k=0, l=0, m=100, n=0)"""
   }
})
