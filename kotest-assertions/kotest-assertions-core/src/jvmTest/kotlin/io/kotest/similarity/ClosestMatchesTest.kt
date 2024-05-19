package io.kotest.similarity

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.WordSpec
import io.kotest.similarity.closestMatches
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class ClosestMatchesTest: WordSpec() {
   init {
      "closestMatches" should {
         "find no matches" {
            closestMatches(setOf(sweetGreenApple, sweetRedApple), sourYellowLemon).isEmpty() shouldBe true
         }

         "find one match" {
            val actual = closestMatches(setOf(sweetGreenApple, sweetRedApple), tartRedCherry)
            assertSoftly {
               actual.size shouldBe 1
               actual[0].value shouldBe tartRedCherry
               actual[0].possibleMatch shouldBe sweetRedApple
               actual[0].comparisonResult.distance.distance shouldBe BigDecimal("0.33")
            }
         }

         "find two matches with same distance" {
            val sweetBlueApple = Fruit("apple", "blue", "sweet")
            val actual = closestMatches(setOf(sweetGreenApple, sweetRedApple), sweetBlueApple)
            assertSoftly {
               actual.size shouldBe 2
               actual.map { it.value }.distinct() shouldBe listOf(sweetBlueApple)
               actual.map { it.possibleMatch } shouldContainExactlyInAnyOrder listOf(sweetGreenApple, sweetRedApple)
               actual.map { it.comparisonResult.distance.distance }.distinct() shouldBe listOf(BigDecimal("0.67"))
            }
         }
      }
   }
}
