package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.equals.Equality
import io.kotest.equals.EqualityResult
import io.kotest.equals.ReflectionUsingFieldsEquality
import io.kotest.equals.SimpleEqualityResult
import io.kotest.equals.SimpleEqualityResultDetail
import io.kotest.equals.types.byObjectEquality
import io.kotest.matchers.collections.contain
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.should
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
         }.shouldHaveMessage("Collection should contain element [1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, ...and 1 more (set the 'kotest.assertions.collection.print.size' JVM property to see more / less items)] based on object equality; but the collection is [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, ...and 1 more (set the 'kotest.assertions.collection.print.size' JVM property to see more / less items)]")
      }
   }
})

private object FruitEquality: Equality<Fruit> {
   override fun name() = "fruit equality"
   override fun verify(actual: Fruit, expected: Fruit): EqualityResult =
      SimpleEqualityResult(actual == expected, SimpleEqualityResultDetail { "Some Mesasge" })
}
