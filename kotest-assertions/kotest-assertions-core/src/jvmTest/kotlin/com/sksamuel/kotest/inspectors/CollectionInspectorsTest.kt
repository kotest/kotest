package com.sksamuel.kotest.inspectors

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.WordSpec
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forAny
import io.kotest.inspectors.forAtLeastOne
import io.kotest.inspectors.forAtMostOne
import io.kotest.inspectors.forExactly
import io.kotest.inspectors.forNone
import io.kotest.inspectors.forOne
import io.kotest.inspectors.forSingle
import io.kotest.inspectors.forSome
import io.kotest.matchers.comparables.beGreaterThan
import io.kotest.matchers.comparables.beLessThan
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

@Suppress("ConstantConditionIf")
@EnabledIf(LinuxOnlyGithubCondition::class)
class CollectionInspectorsTest : WordSpec() {

   private val list = listOf(1, 2, 3, 4, 5)
   private val array = arrayOf(1, 2, 3, 4, 5)

   data class DummyEntry(
      val id: Int,
      val name: String,
   )

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
         "return itself" {
            array.forAll {
               it.shouldBeGreaterThan(0)
            }.forAll {
               it.shouldBeGreaterThan(0)
            }

            list.forAll {
               it.shouldBeGreaterThan(0)
            }.forAll {
               it.shouldBeGreaterThan(0)
            }
         }
         "fail when an exception is thrown inside an array" {
            shouldThrowAny {
               array.forAll {
                  if (true) throw NullPointerException()
               }
            }.message shouldBe """0 elements passed but expected 5

The following elements passed:
  --none--

The following elements failed:
  [0] 1 => java.lang.NullPointerException
  [1] 2 => java.lang.NullPointerException
  [2] 3 => java.lang.NullPointerException
  [3] 4 => java.lang.NullPointerException
  [4] 5 => java.lang.NullPointerException
"""
         }

         "fail when an exception is thrown inside a list" {
            shouldThrowAny {
               list.forAll {
                  if (true) throw NullPointerException()
               }
            }.message shouldBe """0 elements passed but expected 5

The following elements passed:
  --none--

The following elements failed:
  [0] 1 => java.lang.NullPointerException
  [1] 2 => java.lang.NullPointerException
  [2] 3 => java.lang.NullPointerException
  [3] 4 => java.lang.NullPointerException
  [4] 5 => java.lang.NullPointerException
"""
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
         "return itself" {
            list.forNone {
               it shouldBe 10
            }.forNone {
               it shouldBe 10
            }
            array.forNone {
               it shouldBe 10
            }.forNone {
               it shouldBe 10
            }
         }
         "fail if one elements passes fn test" {
            shouldThrow<AssertionError> {
               list.forNone {
                  it shouldBe 4
               }
            }.message shouldBe """1 elements passed but expected 0

The following elements passed:
  [3] 4

The following elements failed:
  [0] 1 => expected:<4> but was:<1>
  [1] 2 => expected:<4> but was:<2>
  [2] 3 => expected:<4> but was:<3>
  [4] 5 => expected:<4> but was:<5>
"""
         }
         "fail if all elements pass fn test" {
            shouldThrow<AssertionError> {
               list.forNone {
                  it should beGreaterThan(0)
               }
            }.message shouldBe """5 elements passed but expected 0

The following elements passed:
  [0] 1
  [1] 2
  [2] 3
  [3] 4
  [4] 5

The following elements failed:
  --none--
"""
         }
         "work inside assertSoftly block" {
            val dummyEntries = listOf(
               DummyEntry(id = 1, name = "first"),
               DummyEntry(id = 2, name = "second"),
            )

            assertSoftly(dummyEntries) {
               forNone {
                  it.id shouldBe 3
                  it.name shouldBe "third"
               }
            }
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
         "return itself" {
            list.forSome {
               it shouldBe 3
            }.forSome {
               it shouldBe 3
            }

            array.forSome {
               it shouldBe 3
            }.forSome {
               it shouldBe 3
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
  [0] 1 => 1 should be < 0
  [1] 2 => 2 should be < 0
  [2] 3 => 3 should be < 0
  [3] 4 => 4 should be < 0
  [4] 5 => 5 should be < 0
"""
         }
         "fail if all elements pass test"  {
            shouldThrow<AssertionError> {
               list.forSome {
                  it should beGreaterThan(0)
               }
            }.message shouldBe """All elements passed but expected < 5

The following elements passed:
  [0] 1
  [1] 2
  [2] 3
  [3] 4
  [4] 5

The following elements failed:
  --none--
"""
         }
         "work inside assertSoftly block" {
            val dummyEntries = listOf(
               DummyEntry(id = 1, name = "first"),
               DummyEntry(id = 1, name = "first"),
               DummyEntry(id = 2, name = "second"),
            )

            assertSoftly(dummyEntries) {
               forSome {
                  it.id shouldBe 1
                  it.name shouldBe "first"
               }
            }
         }
      }

      "forOne" should {
         "pass if one elements pass test"  {
            list.forOne {
               it shouldBe 3
            }
         }
         "return itself" {
            list.forOne {
               it shouldBe 3
            }.forOne {
               it shouldBe 3
            }

            array.forOne {
               it shouldBe 3
            }.forOne {
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
  [2] 3
  [3] 4
  [4] 5

The following elements failed:
  [0] 1 => 1 should be > 2
  [1] 2 => 2 should be > 2
"""
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
  [0] 1 => expected:<22> but was:<1>
  [1] 2 => expected:<22> but was:<2>
  [2] 3 => expected:<22> but was:<3>
  [3] 4 => expected:<22> but was:<4>
  [4] 5 => expected:<22> but was:<5>
"""
         }
         "work inside assertSoftly block" {
            val dummyEntries = listOf(
               DummyEntry(id = 1, name = "first"),
               DummyEntry(id = 2, name = "second"),
            )

            assertSoftly(dummyEntries) {
               forOne {
                  it.id shouldBe 1
                  it.name shouldBe "first"
               }
            }
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
         "return itself" {
            list.forAny {
               it shouldBe 3
            }.forAny {
               it shouldBe 3
            }

            array.forAny {
               it shouldBe 3
            }.forAny {
               it shouldBe 3
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
  [0] 1 => expected:<6> but was:<1>
  [1] 2 => expected:<6> but was:<2>
  [2] 3 => expected:<6> but was:<3>
  [3] 4 => expected:<6> but was:<4>
  [4] 5 => expected:<6> but was:<5>
"""
         }
         "work inside assertSoftly block" {
            val dummyEntries = listOf(
               DummyEntry(id = 1, name = "first"),
               DummyEntry(id = 2, name = "second"),
            )

            assertSoftly(dummyEntries) {
               forAny {
                  it.id shouldBe 1
                  it.name shouldBe "first"
               }
            }
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
  [2] 3
  [3] 4
  [4] 5

The following elements failed:
  [0] 1 => 1 should be > 2
  [1] 2 => 2 should be > 2
"""
         }
         "fail if less elements pass test"  {
            shouldThrow<AssertionError> {
               array.forExactly(2) {
                  it should beLessThan(2)
               }
            }.message shouldBe """1 elements passed but expected 2

The following elements passed:
  [0] 1

The following elements failed:
  [1] 2 => 2 should be < 2
  [2] 3 => 3 should be < 2
  [3] 4 => 4 should be < 2
  [4] 5 => 5 should be < 2
"""
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
  [0] 1 => expected:<33> but was:<1>
  [1] 2 => expected:<33> but was:<2>
  [2] 3 => expected:<33> but was:<3>
  [3] 4 => expected:<33> but was:<4>
  [4] 5 => expected:<33> but was:<5>
"""
         }
      }

      "forAtMostOne" should {
         "pass if one elements pass test" {
            list.forAtMostOne {
               it shouldBe 3
            }
         }
         "fail if 2 elements pass test" {
            shouldThrow<AssertionError> {
               array.forAtMostOne {
                  it should beGreaterThan(3)
               }
            }.message shouldBe """2 elements passed but expected at most 1

The following elements passed:
  [3] 4
  [4] 5

The following elements failed:
  [0] 1 => 1 should be > 3
  [1] 2 => 2 should be > 3
  [2] 3 => 3 should be > 3
"""
         }
         "work inside assertSoftly block" {
            val dummyEntries = listOf(
               DummyEntry(id = 1, name = "first"),
               DummyEntry(id = 2, name = "second"),
            )

            assertSoftly(dummyEntries) {
               forAtMostOne {
                  it.id shouldBe 1
                  it.name shouldBe "first"
               }
            }
         }
      }

      "forAtLeastOne" should {
         "pass if one elements pass test"  {
            list.forAtLeastOne {
               it shouldBe 3
            }
         }
         "fail if no elements pass test"  {
            shouldThrow<AssertionError> {
               array.forAtLeastOne {
                  it shouldBe 22
               }
            }.message shouldBe """0 elements passed but expected at least 1

The following elements passed:
  --none--

The following elements failed:
  [0] 1 => expected:<22> but was:<1>
  [1] 2 => expected:<22> but was:<2>
  [2] 3 => expected:<22> but was:<3>
  [3] 4 => expected:<22> but was:<4>
  [4] 5 => expected:<22> but was:<5>
"""
         }
         "work inside assertSoftly block" {
            val dummyEntries = listOf(
               DummyEntry(id = 1, name = "first"),
               DummyEntry(id = 2, name = "second"),
            )

            assertSoftly(dummyEntries) {
               forAtLeastOne {
                  it.id shouldBe 1
                  it.name shouldBe "first"
               }
            }
         }
      }

      "forSingle" should {
         "pass list is singular, and the single element pass" {
            listOf(1).forSingle {
               it shouldBeLessThan 3
            }
         }

         "return the single element on success" {
            listOf(1).forSingle { it shouldBeLessThan 3 } shouldBe 1
         }

         "fail if collection consists of multiple elements" {
            shouldFail {
               listOf(
                  DummyEntry(id = 1, name = "first"),
                  DummyEntry(id = 2, name = "second"),
               ).forSingle {
                  it.id shouldBe 1
               }
            }.message shouldBe """
               Expected a single element in the collection, but found 2.

               The following elements passed:
                 [0] DummyEntry(id=1, name=first)

               The following elements failed:
                 [1] DummyEntry(id=2, name=second) => expected:<1> but was:<2>

            """.trimIndent()
         }

         "fail for empty collection" {
            shouldFail {
               arrayOf<Int>().forSingle {
                  it shouldBe 3
               }
            }.message shouldBe """
               Expected a single element in the collection, but it was empty.
            """.trimIndent()
         }

         "fail if single element doesn't match" {
            shouldFail {
               arrayOf(2).forSingle {
                  it shouldBe 3
               }
            }.message shouldBe """Expected a single element to pass, but it failed.

The following elements passed:
  --none--

The following elements failed:
  [0] 2 => expected:<3> but was:<2>
"""
         }

         "work inside assertSoftly block" {
            val dummyEntries = listOf(
               DummyEntry(id = 1, name = "first"),
            )

            assertSoftly(dummyEntries) {
               forSingle {
                  it.id shouldBe 1
                  it.name shouldBe "first"
               }
            }
         }
      }
   }
}
