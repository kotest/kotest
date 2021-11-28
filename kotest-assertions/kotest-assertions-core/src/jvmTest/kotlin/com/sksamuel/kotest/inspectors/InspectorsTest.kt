package com.sksamuel.kotest.inspectors

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forAllKeys
import io.kotest.inspectors.forAllValues
import io.kotest.inspectors.forAny
import io.kotest.inspectors.forAnyKey
import io.kotest.inspectors.forAnyValue
import io.kotest.inspectors.forAtLeastOne
import io.kotest.inspectors.forAtMostOne
import io.kotest.inspectors.forExactly
import io.kotest.inspectors.forKeysExactly
import io.kotest.inspectors.forNone
import io.kotest.inspectors.forNoneKey
import io.kotest.inspectors.forNoneValue
import io.kotest.inspectors.forOne
import io.kotest.inspectors.forOneKey
import io.kotest.inspectors.forOneValue
import io.kotest.inspectors.forSome
import io.kotest.inspectors.forSomeKeys
import io.kotest.inspectors.forSomeValues
import io.kotest.inspectors.forValuesExactly
import io.kotest.matchers.comparables.beGreaterThan
import io.kotest.matchers.comparables.beLessThan
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

@Suppress("ConstantConditionIf")
class InspectorsTest : WordSpec() {

   private val list = listOf(1, 2, 3, 4, 5)
   private val array = arrayOf(1, 2, 3, 4, 5)
   private val map = mapOf(1 to "1", 2 to "2", 3 to "3", 4 to "4", 5 to "5")

   data class DummyEntry(
      val id: Int,
      val name: String,
   )

   init {

      "forAllKeys" should {
         "pass if all keys of a map pass" {
            map.forAllKeys {
               it.shouldBeGreaterThan(0)
            }
         }
         "return itself" {
            map.forAllKeys {
               it.shouldBeGreaterThan(0)
            }.forAllKeys {
               it.shouldBeGreaterThan(0)
            }
         }
      }

      "forAllValues" should {
         "pass if all values of a map pass" {
            map.forAllValues {
               it.toInt().shouldBeGreaterThan(0)
            }
         }
         "return itself" {
            map.forAllValues {
               it.toInt().shouldBeGreaterThan(0)
            }.forAllKeys {
               it.toInt().shouldBeGreaterThan(0)
            }
         }
      }

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
         "pass if all entries of a map pass" {
            map.forAll {
               it.key.shouldBe(it.value.toInt())
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

            map.forAll {
               it.key.shouldBe(it.value.toInt())
            }.forAll {
               it.key.shouldBe(it.value.toInt())
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
         "fail when an exception is thrown inside a map" {
            shouldThrowAny {
               map.forAll {
                  if (true) throw NullPointerException()
               }
            }.message shouldBe "0 elements passed but expected 5\n" +
               "\n" +
               "The following elements passed:\n" +
               "--none--\n" +
               "\n" +
               "The following elements failed:\n" +
               "1=1 => java.lang.NullPointerException\n" +
               "2=2 => java.lang.NullPointerException\n" +
               "3=3 => java.lang.NullPointerException\n" +
               "4=4 => java.lang.NullPointerException\n" +
               "5=5 => java.lang.NullPointerException"
         }
      }

      "forNoneKeys" should {
         "pass if no keys pass fn test for a map"  {
            map.forNoneKey {
               it.shouldBeGreaterThan(10)
            }
         }
         "return itself" {
            map.forNoneKey {
               it.shouldBeGreaterThan(10)
            }.forNoneKey {
               it.shouldBeGreaterThan(10)
            }
         }
      }

      "forNoneValues" should {
         "pass if no values pass fn test for a map"  {
            map.forNoneValue {
               it.toInt().shouldBeGreaterThan(10)
            }
         }
         "return itself" {
            map.forNoneValue {
               it.toInt().shouldBeGreaterThan(10)
            }.forNoneValue {
               it.toInt().shouldBeGreaterThan(10)
            }
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
         "pass if no entries of a map pass" {
            map.forNone {
               it shouldBe mapOf(10 to "10").entries.first()
            }
         }
         "pass if an entry throws an exception" {
            map.forNone {
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
            map.forNone {
               it shouldBe mapOf(10 to "10").entries.first()
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
         "fail if one entry passes fn test" {
            shouldThrow<AssertionError> {
               map.forNone {
                  it shouldBe mapOf(4 to "4").entries.first()
               }
            }.message shouldBe """1 elements passed but expected 0

The following elements passed:
4=4

The following elements failed:
1=1 => expected:<4=4> but was:<1=1>
2=2 => expected:<4=4> but was:<2=2>
3=3 => expected:<4=4> but was:<3=3>
5=5 => expected:<4=4> but was:<5=5>"""
         }
         "fail if all entries pass fn test" {
            shouldThrow<AssertionError> {
               map.forNone {
                  it.key shouldBe it.value.toInt()
               }
            }.message shouldBe """5 elements passed but expected 0

The following elements passed:
1=1
2=2
3=3
4=4
5=5

The following elements failed:
--none--"""
         }
         "work inside assertSoftly block (for map)" {
            assertSoftly(map) {
               forNone {
                  it.key shouldBe 10
                  it.value shouldBe "10"
               }
            }
         }
      }

      "forSomeKeys" should {
         "pass if one key pass fn test for a map"  {
            map.forSomeKeys {
               it shouldBe 1
            }
         }
         "return itself" {
            map.forSomeKeys {
               it shouldBe 1
            }.forSomeKeys {
               it shouldBe 1
            }
         }
      }

      "forSomeValues" should {
         "pass if one value pass fn test for a map"  {
            map.forSomeValues {
               it.toInt() shouldBe 1
            }
         }
         "return itself" {
            map.forSomeValues {
               it.toInt() shouldBe 1
            }.forSomeValues {
               it.toInt() shouldBe 1
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
         "pass if one entry pass test"  {
            map.forSome {
               it shouldBe mapOf(1 to "1").entries.first()
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

            map.forSome {
               it shouldBe mapOf(1 to "1").entries.first()
            }.forSome {
               it shouldBe mapOf(1 to "1").entries.first()
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
         "fail if no entries pass test"  {
            shouldThrow<AssertionError> {
               map.forSome {
                  it shouldBe mapOf(0 to "0").entries.first()
               }
            }.message shouldBe """No elements passed but expected at least one

The following elements passed:
--none--

The following elements failed:
1=1 => expected:<0=0> but was:<1=1>
2=2 => expected:<0=0> but was:<2=2>
3=3 => expected:<0=0> but was:<3=3>
4=4 => expected:<0=0> but was:<4=4>
5=5 => expected:<0=0> but was:<5=5>"""
         }
         "fail if all entries pass test"  {
            shouldThrow<AssertionError> {
               map.forSome {
                  it.key shouldBe it.value.toInt()
               }
            }.message shouldBe """All elements passed but expected < 5

The following elements passed:
1=1
2=2
3=3
4=4
5=5

The following elements failed:
--none--"""
         }
         "work inside assertSoftly block (for map)" {
            assertSoftly(map) {
               forSome {
                  it.key shouldBe 1
                  it.value shouldBe "1"
               }
            }
         }
      }

      "forOneKey" should {
         "pass if one key pass fn test for a map"  {
            map.forOneKey {
               it shouldBe 1
            }
         }
         "return itself" {
            map.forOneKey {
               it shouldBe 1
            }.forOneKey {
               it shouldBe 1
            }
         }
      }

      "forOneValue" should {
         "pass if one value pass fn test for a map"  {
            map.forOneValue {
               it.toInt() shouldBe 1
            }
         }
         "return itself" {
            map.forOneValue {
               it.toInt() shouldBe 1
            }.forOneValue {
               it.toInt() shouldBe 1
            }
         }
      }

      "forOne" should {
         "pass if one elements pass test"  {
            list.forOne {
               it shouldBe 3
            }
         }
         "pass if one entry pass test"  {
            map.forOne {
               it shouldBe mapOf(1 to "1").entries.first()
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

            map.forOne {
               it shouldBe mapOf(1 to "1").entries.first()
            }.forOne {
               it shouldBe mapOf(1 to "1").entries.first()
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
         "fail if > 1 entries pass test"  {
            shouldThrow<AssertionError> {
               map.forOne {
                  mapOf(3 to "3", 4 to "4", 5 to "5").shouldContain(it.toPair())
               }
            }.message shouldBe """3 elements passed but expected 1

The following elements passed:
3=3
4=4
5=5

The following elements failed:
1=1 => Map should contain mapping 1=1 but was {3=3, 4=4, 5=5}
2=2 => Map should contain mapping 2=2 but was {3=3, 4=4, 5=5}"""
         }
         "fail if no entries pass test"  {
            shouldThrow<AssertionError> {
               map.forOne {
                  it shouldBe mapOf(22 to "22").entries.first()
               }
            }.message shouldBe """0 elements passed but expected 1

The following elements passed:
--none--

The following elements failed:
1=1 => expected:<22=22> but was:<1=1>
2=2 => expected:<22=22> but was:<2=2>
3=3 => expected:<22=22> but was:<3=3>
4=4 => expected:<22=22> but was:<4=4>
5=5 => expected:<22=22> but was:<5=5>"""
         }
         "work inside assertSoftly block (for map)" {
            assertSoftly(map) {
               forOne {
                  it.key shouldBe 1
                  it.value shouldBe "1"
               }
            }
         }
      }

      "forAnyKey" should {
         "pass if one key pass fn test for a map"  {
            map.forAnyKey {
               it shouldBe 1
            }
         }
         "return itself" {
            map.forAnyKey {
               it shouldBe 1
            }.forAnyKey {
               it shouldBe 1
            }
         }
      }

      "forAnyValue" should {
         "pass if one value pass fn test for a map"  {
            map.forAnyValue {
               it.toInt() shouldBe 1
            }
         }
         "return itself" {
            map.forAnyValue {
               it.toInt() shouldBe 1
            }.forAnyValue {
               it.toInt() shouldBe 1
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
         "pass if any entries pass test"  {
            map.forAny {
               mapOf(1 to "1", 2 to "2").shouldContain(it.toPair())
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

            map.forAny {
               mapOf(1 to "1", 2 to "2").shouldContain(it.toPair())
            }.forAny {
               mapOf(1 to "1", 2 to "2").shouldContain(it.toPair())
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
         "fail if no entries pass test"  {
            shouldThrow<AssertionError> {
               map.forAny {
                  it shouldBe mapOf(6 to "6").entries.first()
               }
            }.message shouldBe """0 elements passed but expected at least 1

The following elements passed:
--none--

The following elements failed:
1=1 => expected:<6=6> but was:<1=1>
2=2 => expected:<6=6> but was:<2=2>
3=3 => expected:<6=6> but was:<3=3>
4=4 => expected:<6=6> but was:<4=4>
5=5 => expected:<6=6> but was:<5=5>"""
         }
         "work inside assertSoftly block (for map)" {
            assertSoftly(map) {
               forAny {
                  it.key shouldBe 1
                  it.value shouldBe "1"
               }
            }
         }
      }
      "forKeysExactly" should {
         "pass if one key pass fn test for a map"  {
            map.forKeysExactly(1) {
               it shouldBe 1
            }
         }
         "return itself" {
            map.forKeysExactly(1) {
               it shouldBe 1
            }.forKeysExactly(1) {
               it shouldBe 1
            }
         }
      }

      "forValuesExactly" should {
         "pass if one value pass fn test for a map"  {
            map.forValuesExactly(1) {
               it.toInt() shouldBe 1
            }
         }
         "return itself" {
            map.forValuesExactly(1) {
               it.toInt() shouldBe 1
            }.forValuesExactly(1) {
               it.toInt() shouldBe 1
            }
         }
      }


      "forExactly" should {
         "pass if exactly k elements pass"  {
            list.forExactly(2) {
               it should beLessThan(3)
            }
         }
         "pass if exactly k entries pass"  {
            map.forExactly(4) {
               it shouldNotBe mapOf(1 to "1").entries.first()
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
         "fail if more entries pass test"  {
            shouldThrow<AssertionError> {
               map.forExactly(3) {
                  it shouldNotBe mapOf(1 to "1").entries.first()
               }
            }.message shouldBe """4 elements passed but expected 3

The following elements passed:
2=2
3=3
4=4
5=5

The following elements failed:
1=1 => 1=1 should not equal 1=1"""
         }
         "fail if less entries pass test"  {
            shouldThrow<AssertionError> {
               map.forExactly(5) {
                  it shouldNotBe mapOf(1 to "1").entries.first()
               }
            }.message shouldBe """4 elements passed but expected 5

The following elements passed:
2=2
3=3
4=4
5=5

The following elements failed:
1=1 => 1=1 should not equal 1=1"""
         }
         "fail if no entries pass test"  {
            shouldThrow<AssertionError> {
               map.forExactly(1) {
                  it shouldBe mapOf(10 to "10").entries.first()
               }
            }.message shouldBe """0 elements passed but expected 1

The following elements passed:
--none--

The following elements failed:
1=1 => expected:<10=10> but was:<1=1>
2=2 => expected:<10=10> but was:<2=2>
3=3 => expected:<10=10> but was:<3=3>
4=4 => expected:<10=10> but was:<4=4>
5=5 => expected:<10=10> but was:<5=5>"""
         }
      }

      "forAtMostOnce" should {
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
         "work inside assertSoftly block (for map)" {
            assertSoftly(map) {
               forAtMostOne {
                  it.key shouldBe 1
                  it.value shouldBe "1"
               }
            }
         }
      }

      "forAtLeastOne" should {
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
         "work inside assertSoftly block (for map)" {
            assertSoftly(map) {
               forAtLeastOne {
                  it.key shouldBe 1
                  it.value shouldBe "1"
               }
            }
         }
      }
   }
}
