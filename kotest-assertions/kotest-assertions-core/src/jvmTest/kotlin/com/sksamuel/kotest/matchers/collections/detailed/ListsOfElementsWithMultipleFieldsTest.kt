package com.sksamuel.kotest.matchers.collections.detailed

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.detailed.matchList
import io.kotest.matchers.collections.detailed.shouldMatchList
import io.kotest.matchers.shouldBe

class ListsOfElementsWithMultipleFieldsTest: StringSpec() {
    init {
        "find exact match" {
           val expected = listOf(sweetGreenApple, sweetRedApple, sweetGreenPear)
           val actual = listOf(sweetGreenPear, sweetGreenApple, sweetRedApple)
           shouldThrowAny {
              actual shouldMatchList expected
           }.message shouldBe """
Mismatch:
actual[0] = Fruit(name=pear, color=green, taste=sweet)

Match:
expected[0] == actual[1]: Fruit(name=apple, color=green, taste=sweet)
expected[1] == actual[2]: Fruit(name=apple, color=red, taste=sweet)

Mismatch:
expected[2] = Fruit(name=pear, color=green, taste=sweet)

Possible matches:
actual[0] == expected[2], is: Fruit(name=pear, color=green, taste=sweet)"""
        }

        "find partial matches" {
            val expected = listOf(sweetGreenApple, sweetRedApple, sweetGreenPear)
            val actual = listOf(sweetGreenPear.copy(name = "onion"), sweetGreenApple, sweetRedApple)
            shouldThrowAny {
                actual shouldMatchList expected
            }.message shouldBe """

               Mismatch:
               actual[0] = Fruit(name=onion, color=green, taste=sweet)

               Match:
               expected[0] == actual[1]: Fruit(name=apple, color=green, taste=sweet)
               expected[1] == actual[2]: Fruit(name=apple, color=red, taste=sweet)

               Mismatch:
               expected[2] = Fruit(name=pear, color=green, taste=sweet)

               Possible matches:
               actual[0] = Fruit(name=onion, color=green, taste=sweet) is similar to
               expected[0] = Fruit(name=apple, color=green, taste=sweet)

               "name" expected: apple,
                but was: onion
               "color" = green
               "taste" = sweet

               actual[0] = Fruit(name=onion, color=green, taste=sweet) is similar to
               expected[2] = Fruit(name=pear, color=green, taste=sweet)

               "name" expected: pear,
                but was: onion
               "color" = green
               "taste" = sweet
            """.trimIndent()
        }

        "find partial match, one element" {
            val expected = listOf(sweetGreenApple)
            val actual = listOf(sweetRedApple)
            shouldThrowAny {
                actual shouldMatchList expected
            }.message shouldBe """

               Mismatch:
               expected[0] = Fruit(name=apple, color=green, taste=sweet)
               actual[0] = Fruit(name=apple, color=red, taste=sweet)

               Possible matches:
               actual[0] = Fruit(name=apple, color=red, taste=sweet) is similar to
               expected[0] = Fruit(name=apple, color=green, taste=sweet)

               "name" = apple
               "color" expected: green,
                but was: red
               "taste" = sweet
            """.trimIndent()
        }

        "find no matches" {
            val expected = listOf(sweetGreenApple, sweetRedApple, sweetGreenPear)
            val actual = listOf(sourYellowLemon, sweetGreenApple, sweetRedApple)
            shouldThrowAny {
                actual shouldMatchList expected
             }.message shouldBe """

                Mismatch:
                actual[0] = Fruit(name=lemon, color=yellow, taste=sour)

                Match:
                expected[0] == actual[1]: Fruit(name=apple, color=green, taste=sweet)
                expected[1] == actual[2]: Fruit(name=apple, color=red, taste=sweet)

                Mismatch:
                expected[2] = Fruit(name=pear, color=green, taste=sweet)

             """.trimIndent()
        }
    }

    data class Fruit(
        val name: String,
        val color: String,
        val taste: String
    )

    val sweetGreenApple = Fruit("apple", "green", "sweet")
    val sweetRedApple = Fruit("apple", "red", "sweet")
    val sweetGreenPear = Fruit("pear", "green", "sweet")
    val sourYellowLemon = Fruit("lemon", "yellow", "sour")
    val tartRedCherry = Fruit("cherry", "red", "tart")

}
