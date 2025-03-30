package io.kotest.similarity

import io.kotest.assertions.assertSoftly
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equality.shouldBeEqualUsingFields
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

@EnabledIf(NotMacOnGithubCondition::class)
class FindBestMatchesTest : StringSpec() {
   init {
      "find one perfect match" {
         findBestMatches(oneApple, listOf(oneApple, twoApples, oneOrange, twoOranges)) shouldBe listOf(
            IndexedComparisonResult(0, Match("", oneApple))
         )
      }

      "find two perfect matches" {
         findBestMatches(oneApple, listOf(twoApples, oneOrange, oneApple, twoOranges, oneApple)) shouldBe listOf(
            IndexedComparisonResult(2, Match("", oneApple)),
            IndexedComparisonResult(4, Match("", oneApple))
         )
      }

      "find two closest matches" {
         val actual = findBestMatches(oneApple, listOf(twoApples, twoOranges, oneOrange))
         assertSoftly {
            actual.size shouldBe 2
            actual[0] shouldBeEqualUsingFields IndexedComparisonResult(
               0,
               MismatchByField(
                  field = "",
                  expected = CountedName(name = "apple", count = 2),
                  actual = CountedName(name = "apple", count = 1),
                  comparisonResults = listOf(
                     Match(field = "name", value = "apple"),
                     AtomicMismatch(
                        field = "count", expected = 2, actual = 1,
                        distance = Distance(BigDecimal.ZERO)
                     )
                  ),
                  distance = Distance(BigDecimal("0.5"))
               )
            )
            actual[1] shouldBeEqualUsingFields IndexedComparisonResult(
               2,
               MismatchByField(
                  field = "",
                  expected = CountedName(name = "orange", count = 1),
                  actual = CountedName(name = "apple", count = 1),
                  comparisonResults = listOf(
                     AtomicMismatch(
                        field = "name", expected = "orange", actual = "apple",
                        distance = Distance(BigDecimal.ZERO)
                     ),
                     Match(field = "count", value = 1),
                  ),
                  distance = Distance(BigDecimal("0.5"))
               )
            )
         }
      }

      "find nothing if complete mismatch" {
         findBestMatches(oneApple, listOf(twoOranges, threeLemons)) shouldBe listOf()
      }
   }
}
