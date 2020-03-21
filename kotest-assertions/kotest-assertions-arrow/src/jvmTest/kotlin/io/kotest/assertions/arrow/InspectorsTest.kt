package io.kotest.assertions.arrow

import arrow.core.NonEmptyList
import io.kotest.assertions.arrow.nel.forAny
import io.kotest.assertions.arrow.nel.forExactly
import io.kotest.assertions.arrow.nel.forNone
import io.kotest.assertions.arrow.nel.forOne
import io.kotest.assertions.arrow.nel.forSome
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.comparables.beGreaterThan
import io.kotest.matchers.comparables.beLessThan
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

class InspectorsTest : WordSpec() {

   private val nel = NonEmptyList(1, 2, 3, 4, 5)

   init {

      "forNone" should {
         "pass if no elements pass fn test for a list" {
            nel.forNone {
               it shouldBe 10
            }
         }
         "fail if one elements passes fn test" {
            val t = shouldThrow<AssertionError> {
               nel.forNone {
                  it shouldBe 4
               }
            }
            println(t.message)
            t.message shouldBe """1 elements passed but expected 0

The following elements passed:
4

The following elements failed:
1 => expected:<4> but was:<1>
2 => expected:<4> but was:<2>
3 => expected:<4> but was:<3>
5 => expected:<4> but was:<5>"""
         }
         "fail if all elements pass fn test" {
            shouldThrow<AssertionError> {
               nel.forNone {
                  it should beGreaterThan(0)
               }
            }.message shouldBe """5 elements passed but expected 0

The following elements passed:
1
2
3
4
5

The following elements failed:
--none--"""
         }
      }

      "forSome" should {
         "pass if one elements pass test"  {
            nel.forSome {
               it shouldBe 3
            }
         }
         "pass if size-1 elements pass test"  {
            nel.forSome {
               it should beGreaterThan(1)
            }
         }
         "fail if no elements pass test"  {
            shouldThrow<AssertionError> {
               nel.forSome {
                  it should beLessThan(0)
               }
            }.message shouldBe """No elements passed but expected at least one

The following elements passed:
--none--

The following elements failed:
1 => 1 should be < 0
2 => 2 should be < 0
3 => 3 should be < 0
4 => 4 should be < 0
5 => 5 should be < 0"""
         }
         "fail if all elements pass test"  {
            shouldThrow<AssertionError> {
               nel.forSome {
                  it should beGreaterThan(0)
               }
            }.message shouldBe """All elements passed but expected < 5

The following elements passed:
1
2
3
4
5

The following elements failed:
--none--"""
         }
      }

      "forOne" should {
         "pass if one elements pass test"  {
            nel.forOne {
               it shouldBe 3
            }
         }
         "fail if > 1 elements pass test"  {
            shouldThrow<AssertionError> {
               nel.forOne {
                  it should beGreaterThan(2)
               }
            }.message shouldBe """3 elements passed but expected 1

The following elements passed:
3
4
5

The following elements failed:
1 => 1 should be > 2
2 => 2 should be > 2"""
         }
         "fail if no elements pass test"  {
            shouldThrow<AssertionError> {
               nel.forOne {
                  it shouldBe 22
               }
            }.message shouldBe """0 elements passed but expected 1

The following elements passed:
--none--

The following elements failed:
1 => expected:<22> but was:<1>
2 => expected:<22> but was:<2>
3 => expected:<22> but was:<3>
4 => expected:<22> but was:<4>
5 => expected:<22> but was:<5>"""
         }
      }

      "forAny" should {
         "pass if one elements pass test"  {
            nel.forAny {
               it shouldBe 3
            }
         }
         "pass if at least elements pass test"  {
            nel.forAny {
               it should beGreaterThan(2)
            }
         }
         "fail if no elements pass test"  {
            shouldThrow<AssertionError> {
               nel.forAny {
                  it shouldBe 6
               }
            }.message shouldBe """0 elements passed but expected at least 1

The following elements passed:
--none--

The following elements failed:
1 => expected:<6> but was:<1>
2 => expected:<6> but was:<2>
3 => expected:<6> but was:<3>
4 => expected:<6> but was:<4>
5 => expected:<6> but was:<5>"""
         }
      }

      "forExactly" should {
         "pass if exactly k elements pass"  {
            nel.forExactly(2) {
               it should beLessThan(3)
            }
         }
         "fail if more elements pass test"  {
            shouldThrow<AssertionError> {
               nel.forExactly(2) {
                  it should beGreaterThan(2)
               }
            }.message shouldBe """3 elements passed but expected 2

The following elements passed:
3
4
5

The following elements failed:
1 => 1 should be > 2
2 => 2 should be > 2"""
         }
         "fail if less elements pass test"  {
            shouldThrow<AssertionError> {
               nel.forExactly(2) {
                  it should beLessThan(2)
               }
            }.message shouldBe """1 elements passed but expected 2

The following elements passed:
1

The following elements failed:
2 => 2 should be < 2
3 => 3 should be < 2
4 => 4 should be < 2
5 => 5 should be < 2"""
         }
         "fail if no elements pass test"  {
            shouldThrow<AssertionError> {
               nel.forExactly(2) {
                  it shouldBe 33
               }
            }.message shouldBe """0 elements passed but expected 2

The following elements passed:
--none--

The following elements failed:
1 => expected:<33> but was:<1>
2 => expected:<33> but was:<2>
3 => expected:<33> but was:<3>
4 => expected:<33> but was:<4>
5 => expected:<33> but was:<5>"""
         }
      }
   }
}
