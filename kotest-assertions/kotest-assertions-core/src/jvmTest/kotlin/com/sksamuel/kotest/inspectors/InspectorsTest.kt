package com.sksamuel.kotest.inspectors

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forAny
import io.kotest.inspectors.forExactly
import io.kotest.inspectors.forNone
import io.kotest.inspectors.forOne
import io.kotest.inspectors.forSome
import io.kotest.matchers.comparables.beGreaterThan
import io.kotest.matchers.comparables.beLessThan
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

@Suppress("ConstantConditionIf")
class InspectorsTest : WordSpec() {

  private val list = listOf(1, 2, 3, 4, 5)
  private val array = arrayOf(1, 2, 3, 4, 5)

  init {

    "forAll" should {
      "pass if all elements of an array pass" {
        array.forAll {
          it.shouldBeGreaterThan(0)
        }
      }
      "pass if all elements of a collection pass" {
        list.forAll {
          it.shouldBeGreaterThan(0)
        }
      }
      "fail when an exception is thrown inside an array" {
        shouldThrowAny {
          array.forAll {
            if (true) throw NullPointerException()
          }
        }.message shouldBe "0 elements passed but expected 5\n" +
            "\n" +
            "The following elements passed:\n" +
            "--none--\n" +
            "\n" +
            "The following elements failed:\n" +
            "1 => java.lang.NullPointerException\n" +
            "2 => java.lang.NullPointerException\n" +
            "3 => java.lang.NullPointerException\n" +
            "4 => java.lang.NullPointerException\n" +
            "5 => java.lang.NullPointerException"
      }
      "fail when an exception is thrown inside a list" {
        shouldThrowAny {
          list.forAll {
            if (true) throw NullPointerException()
          }
        }.message shouldBe "0 elements passed but expected 5\n" +
            "\n" +
            "The following elements passed:\n" +
            "--none--\n" +
            "\n" +
            "The following elements failed:\n" +
            "1 => java.lang.NullPointerException\n" +
            "2 => java.lang.NullPointerException\n" +
            "3 => java.lang.NullPointerException\n" +
            "4 => java.lang.NullPointerException\n" +
            "5 => java.lang.NullPointerException"
      }
    }

    "forNone" should {
      "pass if no elements pass fn test for a list" {
        list.forNone {
          it shouldBe 10
        }
      }
      "pass if no elements pass fn test for an array" {
        array.forNone {
          it shouldBe 10
        }
      }
      "pass if an element throws an exception" {
        val items = listOf(1, 2, 3)
        items.forNone {
          if (true) throw NullPointerException()
        }
      }
      "fail if one elements passes fn test" {
        shouldThrow<AssertionError> {
          list.forNone {
            it shouldBe 4
          }
        }.message shouldBe """1 elements passed but expected 0

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
          list.forNone {
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
        list.forSome {
          it shouldBe 3
        }
      }
      "pass if size-1 elements pass test"  {
        list.forSome {
          it should beGreaterThan(1)
        }
      }
      "fail if no elements pass test"  {
        shouldThrow<AssertionError> {
          array.forSome {
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
          list.forSome {
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
        list.forOne {
          it shouldBe 3
        }
      }
      "fail if > 1 elements pass test"  {
        shouldThrow<AssertionError> {
          list.forOne {
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
          array.forOne {
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
        list.forAny {
          it shouldBe 3
        }
      }
      "pass if at least elements pass test"  {
        list.forAny {
          it should beGreaterThan(2)
        }
      }
      "fail if no elements pass test"  {
        shouldThrow<AssertionError> {
          array.forAny {
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
        list.forExactly(2) {
          it should beLessThan(3)
        }
      }
      "fail if more elements pass test"  {
        shouldThrow<AssertionError> {
          list.forExactly(2) {
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
          array.forExactly(2) {
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
          array.forExactly(2) {
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
