package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.equals.Equality
import io.kotest.assertions.equals.EqualityResult
import io.kotest.assertions.equals.SimpleEqualityResult
import io.kotest.assertions.equals.SimpleEqualityResultDetail
import io.kotest.assertions.equals.byObjectEquality
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.contain
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldContainInOrder
import io.kotest.matchers.throwable.shouldHaveMessage

class ShouldContainTest : WordSpec({
   "contain" should {
      "test that a collection contains element x"  {
         val col = listOf(1, 2, 3)
         shouldThrow<AssertionError> {
            col should contain(4)
         }
         shouldThrow<AssertionError> {
            col.shouldContain(4)
         }
         col should contain(2)
         col should contain(2.0)
      }

      "test that a collection contains element with a custom verifier"  {
         val col = listOf(1, 2, 3)
         val verifier = Equality.byObjectEquality<Number>(strictNumberEquality = true)

         shouldThrow<AssertionError> {
            col.shouldContain(2.0, verifier)
         }
         col should contain(2, verifier)
      }

      "support infix shouldContain" {
         val col = listOf(1, 2, 3)
         col shouldContain (2)
      }

      "find similar element" {
         shouldThrowAny {
            listOf(sweetGreenApple, sweetGreenPear) shouldContain (sweetRedApple)
         }.shouldHaveMessage(
            """
            |Collection should contain element Fruit(name=apple, color=red, taste=sweet) based on object equality; but the collection is [Fruit(name=apple, color=green, taste=sweet), Fruit(name=pear, color=green, taste=sweet)]
            |PossibleMatches:
            | expected: Fruit(name=apple, color=red, taste=sweet),
            |  but was: Fruit(name=apple, color=green, taste=sweet),
            |  The following fields did not match:
            |    "color" expected: <"red">, but was: <"green">
    """.trimMargin()
         )
      }

      "find similar element in List<String>" {
         val thrown = shouldThrowAny {
            listOf("sweet green apple", "sweet red plu") shouldContain ("sweet green pear")
         }
         thrown.message.shouldContainInOrder(
            "PossibleMatches:",
            "Match[0]: part of slice with indexes [0..11] matched actual[0..11]",
            """Line[0] ="sweet green apple"""",
            """Match[0]= ++++++++++++-----""",
         )
      }

      "add nothing to output if no similar elements found" {
         shouldThrowAny {
            listOf(sweetGreenApple, sweetGreenPear) should contain (sourYellowLemon)
         }.shouldHaveMessage(
            """
            |Collection should contain element Fruit(name=lemon, color=yellow, taste=sour) based on object equality; but the collection is [Fruit(name=apple, color=green, taste=sweet), Fruit(name=pear, color=green, taste=sweet)]
    """.trimMargin()
         )
      }

      "add nothing to output if custom comparator is used" {
         shouldThrowAny {
            listOf(sweetGreenApple, sweetGreenPear).shouldContain(
               sourYellowLemon,
               comparator = FruitEquality
            )
         }.shouldHaveMessage(
            """
            |Collection should contain element Fruit(name=lemon, color=yellow, taste=sour) based on fruit equality; but the collection is [Fruit(name=apple, color=green, taste=sweet), Fruit(name=pear, color=green, taste=sweet)]
    """.trimMargin()
         )
      }

      "support type inference for subtypes of collection" {
         val tests = listOf(
            TestSealed.Test1("test1"),
            TestSealed.Test2(2)
         )
         tests should contain(TestSealed.Test1("test1"))
         tests.shouldContain(TestSealed.Test2(2))
      }

      "print errors unambiguously"  {
         shouldThrow<AssertionError> {
            listOf<Any>(1, 2).shouldContain(listOf<Any>(1L, 2L))
         }.shouldHaveMessage("Collection should contain element [1L, 2L] based on object equality; but the collection is [1, 2]")
      }

      "print errors unambiguously for long lists"  {
         shouldThrow<AssertionError> {
            listOf<Any>(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21).shouldContain(listOf<Any>(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L))
         }.shouldHaveMessage("Collection should contain element [1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, ...and 1 more (set 'kotest.assertions.collection.print.size' to see more / less items)] based on object equality; but the collection is [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, ...and 1 more (set 'kotest.assertions.collection.print.size' to see more / less items)]")
      }

      "print indexes of matching elements" {
         val message = shouldThrow<AssertionError> {
            listOf(1, 2, 3, 2, 4, 5).shouldNotContain(2)
         }.message
         message.shouldContain("but it did at index(es):[1, 3]")
      }

      "print index of matching element with custom verifier" {
         val caseInsensitiveStringEquality: Equality<String> = object : Equality<String> {
            override fun name() = "Case Insensitive String Matcher"

            override fun verify(actual: String, expected: String): EqualityResult {
               return if (actual.uppercase() == expected.uppercase())
                  EqualityResult.equal(actual, expected, this)
               else
                  EqualityResult.notEqual(actual, expected, this)
            }
         }
         val message = shouldThrow<AssertionError> {
            listOf("apple", "orange", "lemon").shouldNotContain("Orange", caseInsensitiveStringEquality)
         }.message
         message.shouldContain("but it did at index(es):[1]")
      }

      "return diff formatting" {
         val message = shouldThrow<AssertionError> {
            "qeqweew" shouldContain "ooo"
         }.message
         message.shouldContain("""qeqweew" should include substring "ooo"
expected:<ooo> but was:<qeqweew>""")
      }

      "support primitive BooleanArray" {
         booleanArrayOf(true, false) shouldContain true
         booleanArrayOf(true) shouldNotContain false
         shouldThrow<AssertionError> { booleanArrayOf(true) shouldContain false }
         shouldThrow<AssertionError> { booleanArrayOf(true, false) shouldNotContain true }
      }

      "support primitive ByteArray" {
         byteArrayOf(1, 2, 3) shouldContain 2.toByte()
         byteArrayOf(1, 2, 3) shouldNotContain 5.toByte()
         shouldThrow<AssertionError> { byteArrayOf(1, 2, 3) shouldContain 5.toByte() }
         shouldThrow<AssertionError> { byteArrayOf(1, 2, 3) shouldNotContain 2.toByte() }
      }

      "support primitive ShortArray" {
         shortArrayOf(1, 2, 3) shouldContain 2.toShort()
         shortArrayOf(1, 2, 3) shouldNotContain 5.toShort()
         shouldThrow<AssertionError> { shortArrayOf(1, 2, 3) shouldContain 5.toShort() }
         shouldThrow<AssertionError> { shortArrayOf(1, 2, 3) shouldNotContain 2.toShort() }
      }

      "support primitive CharArray" {
         charArrayOf('a', 'b', 'c') shouldContain 'b'
         charArrayOf('a', 'b', 'c') shouldNotContain 'z'
         shouldThrow<AssertionError> { charArrayOf('a', 'b', 'c') shouldContain 'z' }
         shouldThrow<AssertionError> { charArrayOf('a', 'b', 'c') shouldNotContain 'b' }
      }

      "support primitive IntArray" {
         intArrayOf(1, 2, 3) shouldContain 2
         intArrayOf(1, 2, 3) shouldNotContain 5
         shouldThrow<AssertionError> { intArrayOf(1, 2, 3) shouldContain 5 }
         shouldThrow<AssertionError> { intArrayOf(1, 2, 3) shouldNotContain 2 }
      }

      "support primitive LongArray" {
         longArrayOf(1L, 2L, 3L) shouldContain 2L
         longArrayOf(1L, 2L, 3L) shouldNotContain 5L
         shouldThrow<AssertionError> { longArrayOf(1L, 2L, 3L) shouldContain 5L }
         shouldThrow<AssertionError> { longArrayOf(1L, 2L, 3L) shouldNotContain 2L }
      }

      "support primitive FloatArray" {
         floatArrayOf(1f, 2f, 3f) shouldContain 2f
         floatArrayOf(1f, 2f, 3f) shouldNotContain 5f
         shouldThrow<AssertionError> { floatArrayOf(1f, 2f, 3f) shouldContain 5f }
         shouldThrow<AssertionError> { floatArrayOf(1f, 2f, 3f) shouldNotContain 2f }
      }

      "support primitive DoubleArray" {
         doubleArrayOf(1.0, 2.0, 3.0) shouldContain 2.0
         doubleArrayOf(1.0, 2.0, 3.0) shouldNotContain 5.0
         shouldThrow<AssertionError> { doubleArrayOf(1.0, 2.0, 3.0) shouldContain 5.0 }
         shouldThrow<AssertionError> { doubleArrayOf(1.0, 2.0, 3.0) shouldNotContain 2.0 }
      }
   }
})

private object FruitEquality: Equality<Fruit> {
   override fun name() = "fruit equality"
   override fun verify(actual: Fruit, expected: Fruit): EqualityResult =
      SimpleEqualityResult(actual == expected, SimpleEqualityResultDetail { "Some Mesasge" })
}
