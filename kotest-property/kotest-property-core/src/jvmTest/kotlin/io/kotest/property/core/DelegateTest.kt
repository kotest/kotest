package io.kotest.property.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.ShrinkingMode
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import kotlin.time.Duration.Companion.milliseconds

class DelegateTest : FunSpec() {
   init {
      proptest("delegate syntax 1") {

         iterations = 42 // run this property test 42 times
         failOnSeed = true // will fail if a seed is set
         edgecaseFactor = 0.1 // 10% of generated values will be edgecases
         shouldPrintGeneratedValues = true // output each generated value

         val a by gen { Arb.int() }
         val b by gen { Arb.int() }

         property {
            a + b shouldBe a + b
         }
      }

      proptest("delegate syntax 2") {

         duration = 100.milliseconds // run this property test for as many iteration as 100ms allows
         minSuccess = 5 // require 5 successful tests to ensure we get at least some in a duration based test
         maxFailure = 1 // allow 1 failure for a flakey test
         shrinkingMode = ShrinkingMode.Off // disable shrinking on this prop test

         val first by gen { Arb.string() }
         val second by gen { Arb.string() }

         property {
            val concat = first + second

            concat shouldStartWith first
            concat shouldEndWith second
            concat shouldHaveLength (first.length + second.length)
         }
      }

      proptest("shrinking") {

         seed = 838127382173 // override seed
         shouldPrintShrinkSteps = true // print shrink steps

         val a by gen { Arb.int() }
         val b by gen { Arb.int() }

         property {
            a * b shouldBe a + b
         }
      }

      proptest("assumptions") {

         val a by gen { Arb.int() }
         val b by gen { Arb.int() }
         val c by gen { Arb.int() }

         assume { a != b }
         assume { a != c }
         assume { b != c }

         property {
            a + b + c shouldBe c + b + a
         }
      }

      proptest("callbacks") {

         val a by gen { Arb.int() }
         val b by gen { Arb.int() }
         val c by gen { Arb.int() }

         beforeProperty {
            // setup code
         }

         afterProperty {
            // some tear down code
         }

         property {
            a + b + c shouldBe c + b + a
         }
      }
   }
}
