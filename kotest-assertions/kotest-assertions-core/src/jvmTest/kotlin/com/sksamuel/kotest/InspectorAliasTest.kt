package com.sksamuel.kotest

import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forAny
import io.kotest.inspectors.forAtLeast
import io.kotest.inspectors.forAtLeastOne
import io.kotest.inspectors.forAtMost
import io.kotest.inspectors.forAtMostOne
import io.kotest.inspectors.forExactly
import io.kotest.inspectors.forNone
import io.kotest.inspectors.forOne
import io.kotest.inspectors.forSome
import io.kotest.inspectors.shouldForAll
import io.kotest.inspectors.shouldForAny
import io.kotest.inspectors.shouldForAtLeast
import io.kotest.inspectors.shouldForAtLeastOne
import io.kotest.inspectors.shouldForAtMost
import io.kotest.inspectors.shouldForAtMostOne
import io.kotest.inspectors.shouldForExactly
import io.kotest.inspectors.shouldForNone
import io.kotest.inspectors.shouldForOne
import io.kotest.inspectors.shouldForSome
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.mockkStatic
import io.mockk.verify

class InspectorAliasTest : FunSpec(
   {
      beforeEach {
         mockkStatic("io.kotest.inspectors.InspectorsKt")
      }

      afterEach {
         clearAllMocks()
      }

      val array = arrayOf(1, 2, 3)
      val list = listOf(1, 2, 3)
      val sequence = sequenceOf(1, 2, 3)

      context("forAll") {
         fun block(x: Int) = x shouldBeGreaterThan 0

         test("array") {
            array.shouldForAll(::block)
            verify { array.forAll(::block) }
         }

         test("list") {
            list.shouldForAll(::block)
            verify { list.forAll(::block) }
         }

         test("sequence") {
            sequence.shouldForAll(::block)
            verify { sequence.forAll(::block) }
         }
      }

      context("forOne") {
         fun block(x: Int) = x shouldBe 2

         test("array") {
            array.shouldForOne(::block)
            verify { array.forOne(::block) }
         }

         test("list") {
            list.shouldForOne(::block)
            verify { list.forOne(::block) }
         }

         test("sequence") {
            sequence.shouldForOne(::block)
            verify { sequence.forOne(::block) }
         }
      }

      context("forExactly") {
         fun block(x: Int) = x shouldBeGreaterThan 1
         val n = 2

         test("array") {
            array.shouldForExactly(n, ::block)
            verify { array.forExactly(n, ::block) }
         }

         test("list") {
            list.shouldForExactly(n, ::block)
            verify { list.forExactly(n, ::block) }
         }

         test("sequence") {
            sequence.shouldForExactly(n, ::block)
            verify { sequence.forExactly(n, ::block) }
         }
      }

      context("forSome") {
         fun block(x: Int) = x shouldBeGreaterThan 2

         test("array") {
            array.shouldForSome(::block)
            verify { array.forSome(::block) }
         }

         test("list") {
            list.shouldForSome(::block)
            verify { list.forSome(::block) }
         }

         test("sequence") {
            sequence.shouldForSome(::block)
            verify { sequence.forSome(::block) }
         }
      }

      context("forAny") {
         fun block(x: Int) = x shouldBeGreaterThan 0

         test("array") {
            array.shouldForAny(::block)
            verify { array.forAny(::block) }
         }

         test("list") {
            list.shouldForAny(::block)
            verify { list.forAny(::block) }
         }

         test("sequence") {
            sequence.shouldForAny(::block)
            verify { sequence.forAny(::block) }
         }
      }

      context("forAtLeast") {
         fun block(x: Int) = x shouldBeGreaterThan 0
         val n = 3

         test("array") {
            array.shouldForAtLeast(n, ::block)
            verify { array.forAtLeast(n, ::block) }
         }

         test("list") {
            list.shouldForAtLeast(n, ::block)
            verify { list.forAtLeast(n, ::block) }
         }

         test("sequence") {
            sequence.shouldForAtLeast(n, ::block)
            verify { sequence.forAtLeast(n, ::block) }
         }
      }

      context("forAtLeastOne") {
         fun block(x: Int) = x shouldBeGreaterThan 0

         test("array") {
            array.shouldForAtLeastOne(::block)
            verify { array.forAtLeastOne(::block) }
         }

         test("list") {
            list.shouldForAtLeastOne(::block)
            verify { list.forAtLeastOne(::block) }
         }

         test("sequence") {
            sequence.shouldForAtLeastOne(::block)
            verify { sequence.forAtLeastOne(::block) }
         }
      }

      context("forAtMost") {
         fun block(x: Int) = x shouldBeGreaterThan 0

         test("array") {
            val arr = arrayOf(0, 0, 1)
            arr.shouldForAtMost(1, ::block)
            verify { arr.forAtMost(1, ::block) }
         }

         test("list") {
            val l = listOf(0, 1, 1)
            l.shouldForAtMost(2, ::block)
            verify { l.forAtMost(2, ::block) }
         }

         test("sequence") {
            sequence.shouldForAtMost(3, ::block)
            verify { sequence.forAtMost(3, ::block) }
         }
      }

      context("forNone") {
         fun block(x: Int) = x shouldBeLessThan 1

         test("array") {
            array.shouldForNone(::block)
            verify { array.forNone(::block) }
         }

         test("list") {
            list.shouldForNone(::block)
            verify { list.forNone(::block) }
         }

         test("sequence") {
            sequence.shouldForNone(::block)
            verify { sequence.forNone(::block) }
         }
      }

      context("forAtMostOne") {
         fun block(x: Int) = x shouldBe 1

         test("array") {
            array.shouldForAtMostOne(::block)
            verify { array.forAtMostOne(::block) }
         }

         test("list") {
            list.shouldForAtMostOne(::block)
            verify { list.forAtMostOne(::block) }
         }

         test("sequence") {
            sequence.shouldForAtMostOne(::block)
            verify { sequence.forAtMostOne(::block) }
         }
      }
   }
)
