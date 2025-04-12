package com.sksamuel.kotest.inspectors

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.WordSpec
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forAllKeys
import io.kotest.inspectors.forAllValues
import io.kotest.inspectors.forAny
import io.kotest.inspectors.forAnyKey
import io.kotest.inspectors.forAnyValue
import io.kotest.inspectors.forAtLeastOne
import io.kotest.inspectors.forAtLeastOneKey
import io.kotest.inspectors.forAtLeastOneValue
import io.kotest.inspectors.forAtMostOne
import io.kotest.inspectors.forAtMostOneKey
import io.kotest.inspectors.forAtMostOneValue
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
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

@Suppress("ConstantConditionIf")
@EnabledIf(LinuxOnlyGithubCondition::class)
class MapInspectorsTest : WordSpec() {
   private val map = mapOf(1 to "1", 2 to "2", 3 to "3", 4 to "4", 5 to "5")

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
            }.forAllValues {
               it.toInt().shouldBeGreaterThan(0)
            }
         }
      }

      "forAll" should {
         "pass if all entries of a map pass" {
            map.forAll {
               it.key.shouldBe(it.value.toInt())
            }
         }
         "return itself" {
            map.forAll {
               it.key.shouldBe(it.value.toInt())
            }.forAll {
               it.key.shouldBe(it.value.toInt())
            }
         }
         "fail when an exception is thrown inside a map" {
            shouldThrowAny {
               map.forAll {
                  if (true) throw NullPointerException()
               }
            }.message shouldBe """0 elements passed but expected 5

The following elements passed:
  --none--

The following elements failed:
  [0] 1=1 => java.lang.NullPointerException
  [1] 2=2 => java.lang.NullPointerException
  [2] 3=3 => java.lang.NullPointerException
  [3] 4=4 => java.lang.NullPointerException
  [4] 5=5 => java.lang.NullPointerException
"""
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
            map.forNone {
               it shouldBe mapOf(10 to "10").entries.first()
            }.forNone {
               it shouldBe mapOf(10 to "10").entries.first()
            }
         }
         "fail if one entry passes fn test" {
            shouldThrow<AssertionError> {
               map.forNone {
                  it shouldBe mapOf(4 to "4").entries.first()
               }
            }.message shouldBe """1 elements passed but expected 0

The following elements passed:
  [3] 4=4

The following elements failed:
  [0] 1=1 => expected:<4=4> but was:<1=1>
  [1] 2=2 => expected:<4=4> but was:<2=2>
  [2] 3=3 => expected:<4=4> but was:<3=3>
  [4] 5=5 => expected:<4=4> but was:<5=5>
"""
         }
         "fail if all entries pass fn test" {
            shouldThrow<AssertionError> {
               map.forNone {
                  it.key shouldBe it.value.toInt()
               }
            }.message shouldBe """5 elements passed but expected 0

The following elements passed:
  [0] 1=1
  [1] 2=2
  [2] 3=3
  [3] 4=4
  [4] 5=5

The following elements failed:
  --none--
"""
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
         "pass if one entry pass test"  {
            map.forSome {
               it shouldBe mapOf(1 to "1").entries.first()
            }
         }
         "return itself" {
            map.forSome {
               it shouldBe mapOf(1 to "1").entries.first()
            }.forSome {
               it shouldBe mapOf(1 to "1").entries.first()
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
  [0] 1=1 => expected:<0=0> but was:<1=1>
  [1] 2=2 => expected:<0=0> but was:<2=2>
  [2] 3=3 => expected:<0=0> but was:<3=3>
  [3] 4=4 => expected:<0=0> but was:<4=4>
  [4] 5=5 => expected:<0=0> but was:<5=5>
"""
         }
         "fail if all entries pass test"  {
            shouldThrow<AssertionError> {
               map.forSome {
                  it.key shouldBe it.value.toInt()
               }
            }.message shouldBe """All elements passed but expected < 5

The following elements passed:
  [0] 1=1
  [1] 2=2
  [2] 3=3
  [3] 4=4
  [4] 5=5

The following elements failed:
  --none--
"""
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
         "pass if one entry pass test"  {
            map.forOne {
               it shouldBe mapOf(1 to "1").entries.first()
            }
         }
         "return itself" {
            map.forOne {
               it shouldBe mapOf(1 to "1").entries.first()
            }.forOne {
               it shouldBe mapOf(1 to "1").entries.first()
            }
         }
         "fail if > 1 entries pass test"  {
            shouldThrow<AssertionError> {
               map.forOne {
                  mapOf(3 to "3", 4 to "4", 5 to "5").shouldContain(it.toPair())
               }
            }.message shouldBe """3 elements passed but expected 1

The following elements passed:
  [2] 3=3
  [3] 4=4
  [4] 5=5

The following elements failed:
  [0] 1=1 => Map should contain mapping 1=1 but key was not in the map
  [1] 2=2 => Map should contain mapping 2=2 but key was not in the map
"""
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
  [0] 1=1 => expected:<22=22> but was:<1=1>
  [1] 2=2 => expected:<22=22> but was:<2=2>
  [2] 3=3 => expected:<22=22> but was:<3=3>
  [3] 4=4 => expected:<22=22> but was:<4=4>
  [4] 5=5 => expected:<22=22> but was:<5=5>
"""
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
         "pass if any entries pass test"  {
            map.forAny {
               mapOf(1 to "1", 2 to "2").shouldContain(it.toPair())
            }
         }
         "return itself" {
            map.forAny {
               mapOf(1 to "1", 2 to "2").shouldContain(it.toPair())
            }.forAny {
               mapOf(1 to "1", 2 to "2").shouldContain(it.toPair())
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
  [0] 1=1 => expected:<6=6> but was:<1=1>
  [1] 2=2 => expected:<6=6> but was:<2=2>
  [2] 3=3 => expected:<6=6> but was:<3=3>
  [3] 4=4 => expected:<6=6> but was:<4=4>
  [4] 5=5 => expected:<6=6> but was:<5=5>
"""
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
         "pass if exactly k entries pass"  {
            map.forExactly(4) {
               it shouldNotBe mapOf(1 to "1").entries.first()
            }
         }
         "fail if more entries pass test"  {
            shouldThrow<AssertionError> {
               map.forExactly(3) {
                  it shouldNotBe mapOf(1 to "1").entries.first()
               }
            }.message shouldBe """4 elements passed but expected 3

The following elements passed:
  [1] 2=2
  [2] 3=3
  [3] 4=4
  [4] 5=5

The following elements failed:
  [0] 1=1 => 1=1 should not equal 1=1
"""
         }
         "fail if less entries pass test"  {
            shouldThrow<AssertionError> {
               map.forExactly(5) {
                  it shouldNotBe mapOf(1 to "1").entries.first()
               }
            }.message shouldBe """4 elements passed but expected 5

The following elements passed:
  [1] 2=2
  [2] 3=3
  [3] 4=4
  [4] 5=5

The following elements failed:
  [0] 1=1 => 1=1 should not equal 1=1
"""
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
  [0] 1=1 => expected:<10=10> but was:<1=1>
  [1] 2=2 => expected:<10=10> but was:<2=2>
  [2] 3=3 => expected:<10=10> but was:<3=3>
  [3] 4=4 => expected:<10=10> but was:<4=4>
  [4] 5=5 => expected:<10=10> but was:<5=5>
"""
         }
      }

      "forAtMostOneKey" should {
         "pass if one key pass fn test for a map"  {
            map.forAtMostOneKey {
               it shouldBe 1
            }
         }
         "return itself" {
            map.forAtMostOneKey {
               it shouldBe 1
            }.forAtMostOneKey {
               it shouldBe 1
            }
         }
      }

      "forAtMostOneValue" should {
         "pass if one value pass fn test for a map"  {
            map.forAtMostOneValue {
               it.toInt() shouldBe 1
            }
         }
         "return itself" {
            map.forAtMostOneValue {
               it.toInt() shouldBe 1
            }.forAtMostOneValue {
               it.toInt() shouldBe 1
            }
         }
      }

      "forAtMostOne" should {
         "pass if one elements pass test"  {
            map.forAtMostOne {
               it shouldBe mapOf(3 to "3")
            }
         }
         "fail if 2 elements pass test"  {
            shouldThrow<AssertionError> {
               map.forAtMostOne {
                  mapOf(4 to "4", 5 to "5").shouldContain(it.toPair())
               }
            }.message shouldBe """2 elements passed but expected at most 1

The following elements passed:
  [3] 4=4
  [4] 5=5

The following elements failed:
  [0] 1=1 => Map should contain mapping 1=1 but key was not in the map
  [1] 2=2 => Map should contain mapping 2=2 but key was not in the map
  [2] 3=3 => Map should contain mapping 3=3 but key was not in the map
"""
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

      "forAtLeastOneKey" should {
         "pass if one key pass fn test for a map"  {
            map.forAtLeastOneKey {
               it shouldBe 1
            }
         }
         "return itself" {
            map.forAtLeastOneKey {
               it shouldBe 1
            }.forAtLeastOneKey {
               it shouldBe 1
            }
         }
      }

      "forAtLeastOneValue" should {
         "pass if one value pass fn test for a map"  {
            map.forAtLeastOneValue {
               it.toInt() shouldBe 1
            }
         }
         "return itself" {
            map.forAtLeastOneValue {
               it.toInt() shouldBe 1
            }.forAtLeastOneValue {
               it.toInt() shouldBe 1
            }
         }
      }


      "forAtLeastOne" should {
         "pass if one elements pass test"  {
            map.forAtLeastOne {
               it shouldBe mapOf(3 to "3").entries.first()
            }
         }
         "fail if no elements pass test"  {
            shouldThrow<AssertionError> {
               map.forAtLeastOne {
                  it shouldBe mapOf(22 to "22").entries.first()
               }
            }.message shouldBe """0 elements passed but expected at least 1

The following elements passed:
  --none--

The following elements failed:
  [0] 1=1 => expected:<22=22> but was:<1=1>
  [1] 2=2 => expected:<22=22> but was:<2=2>
  [2] 3=3 => expected:<22=22> but was:<3=3>
  [3] 4=4 => expected:<22=22> but was:<4=4>
  [4] 5=5 => expected:<22=22> but was:<5=5>
"""
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
