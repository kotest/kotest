package com.sksamuel.kotest.property.shrinking

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.system.captureStandardOut
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyTesting
import io.kotest.property.ShrinkingMode
import io.kotest.property.arbitrary.StringShrinker
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.property.internal.doShrinking
import io.kotest.property.rtree

class StringShrinkerTest : DescribeSpec({

   beforeSpec {
      PropertyTesting.shouldPrintShrinkSteps = false
   }

   afterSpec {
      PropertyTesting.shouldPrintShrinkSteps = true
   }

   describe("StringShrinker should") {
      it("include bisected input") {
         checkAll { a: String ->
            if (a.length > 1) {
               val candidates = StringShrinker.shrink(a)
               candidates.forAtLeastOne {
                  it.shouldHaveLength(a.length / 2 + a.length % 2)
               }
               candidates.forAtLeastOne {
                  it.shouldHaveLength(a.length / 2)
               }

            }
         }
      }

      it("should include 2 padded 'a's") {
         checkAll { a: String ->
            if (a.length > 1) {
               val candidates = StringShrinker.shrink(a)
               candidates.forAtLeastOne {
                  it.shouldEndWith("a".repeat(a.length / 2))
               }
               candidates.forAtLeastOne {
                  it.shouldStartWith("a".repeat(a.length / 2))
               }
            }
         }
      }

      it("shrink to expected value") {
         val prt = PropertyTesting.shouldPrintShrinkSteps
         PropertyTesting.shouldPrintShrinkSteps = false

         checkAll<String> { a ->

            val shrinks = StringShrinker.rtree(a)
            val shrunk = doShrinking(shrinks, ShrinkingMode.Unbounded) {
               it.shouldNotContain("#")
            }

            if (a.contains("#")) {
               shrunk.shrink shouldBe "#"
            } else {
               shrunk.shrink shouldBe a
            }
         }

         PropertyTesting.shouldPrintShrinkSteps = prt
      }

      it("prefer padded values") {
         val prt = PropertyTesting.shouldPrintShrinkSteps
         PropertyTesting.shouldPrintShrinkSteps = false

         val a = "97asd!@#ASD'''234)*safmasd"
         val shrinks = StringShrinker.rtree(a)
         doShrinking(shrinks, ShrinkingMode.Unbounded) {
            it.length.shouldBeLessThan(3)
         }.shrink shouldBe "aaa"

         doShrinking(shrinks, ShrinkingMode.Unbounded) {
            it.length.shouldBeLessThan(8)
         }.shrink shouldBe "aaaaaaaa"

         PropertyTesting.shouldPrintShrinkSteps = prt
      }

      it("respect min value") {
         val prt = PropertyTesting.shouldPrintShrinkSteps
         PropertyTesting.shouldPrintShrinkSteps = true
         val stdout = captureStandardOut {
            shouldFail {
               checkAll(PropTestConfig(seed = 123123), Arb.string(4, 8)) { a ->
                  // will cause the value to fail and shrinks be used, but nothing should be shrunk
                  // past the min value of 4, even though we fail on anything >= 2
                  a.shouldHaveLength(1)
               }
            }
         }
         stdout.shouldContain(
            """
Attempting to shrink arg ")E2v${'$'}"
Shrink #1: ")E2a" fail
Shrink #2: "aE2a" fail
Shrink #3: "aa2a" fail
Shrink #4: "aaaa" fail
Shrink result (after 4 shrinks) => "aaaa"
            """.trim()
         )
         PropertyTesting.shouldPrintShrinkSteps = prt
      }
   }
})
