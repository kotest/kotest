package io.kotest.similarity

import io.kotest.assertions.assertSoftly
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

//@EnabledIf(LinuxCondition::class)
class ClosestMatchesTest : WordSpec() {
   init {
      "closestMatches" should {
         "find no matches" {
            closestMatches(setOf(sweetGreenApple, sweetRedApple), sourYellowLemon).isEmpty() shouldBe true
         }

         "find one match for data classes" {
            val actual = closestMatches(setOf(sweetGreenApple, sweetRedApple), sweetGreenPear)
            assertSoftly {
               actual.size shouldBe 1
               actual[0].value shouldBe sweetGreenPear
               actual[0].possibleMatch shouldBe sweetGreenApple
               actual[0].comparisonResult.distance.distance shouldBe BigDecimal("0.67")
            }
         }

         "find one match for strings" {
            val actual = closestMatches(setOf("sweet green apple", "sweet red apple"), "sweet green pear")
            assertSoftly {
               actual.size shouldBe 1
               actual[0].value shouldBe "sweet green pear"
               actual[0].possibleMatch shouldBe "sweet green apple"
               actual[0].comparisonResult.distance.distance shouldBe BigDecimal("0.71")
            }
         }

         "find no matches if similarity below threshold for data class" {
            closestMatches(setOf(sweetGreenApple, sweetRedApple), tartRedCherry).shouldBeEmpty()
         }

         "find no matches if similarity below threshold for String" {
            closestMatches(setOf("sweet green apple", "sweet red apple"), "sweet yellow peach").shouldBeEmpty()
         }

         "find two matches with same distance for data classes" {
            val sweetBlueApple = Fruit("apple", "blue", "sweet")
            val actual = closestMatches(setOf(sweetGreenApple, sweetRedApple), sweetBlueApple)
            assertSoftly {
               actual.size shouldBe 2
               actual.map { it.value }.distinct() shouldBe listOf(sweetBlueApple)
               actual.map { it.possibleMatch } shouldContainExactlyInAnyOrder listOf(sweetGreenApple, sweetRedApple)
               actual.map { it.comparisonResult.distance.distance }.distinct() shouldBe listOf(BigDecimal("0.67"))
            }
         }

         "find two matches with same distance for String" {
            val actual = closestMatches(setOf("sweet red pear", "sweet red plum"), "sweet red lime")
            assertSoftly {
               actual.size shouldBe 2
               actual.map { it.value }.distinct() shouldBe listOf("sweet red lime")
               actual.map { it.possibleMatch } shouldContainExactlyInAnyOrder listOf("sweet red pear", "sweet red plum")
               actual.map { it.comparisonResult.distance.distance }.distinct() shouldBe listOf(BigDecimal("0.71"))
            }
         }
      }
   }
}
