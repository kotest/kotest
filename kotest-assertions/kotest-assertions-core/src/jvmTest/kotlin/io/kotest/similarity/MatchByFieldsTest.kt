package io.kotest.similarity

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

@EnabledIf(LinuxCondition::class)
class MatchByFieldsTest : StringSpec() {
   init {
      "some fields match" {
         val actual = matchByFields("thing", redCircle, blueCircle)
         actual shouldBe
            MismatchByField(
               field = "thing",
               expected = redCircle,
               actual = blueCircle,
               comparisonResults = listOf(
                  AtomicMismatch(
                     field = "color",
                     expected = "red",
                     actual = "blue",
                     distance = Distance(BigDecimal("0.00"))
                  ),
                  Match(field = "shape", value = "circle")
               ),
               distance = Distance(BigDecimal("0.5"))
            )
      }

      "all fields different" {
         val actual = matchByFields("thing", redCircle, blueTriangle)
         actual shouldBe
            MismatchByField(
               field = "thing",
               expected = redCircle,
               actual = blueTriangle,
               comparisonResults = listOf(
                  AtomicMismatch(
                     field = "color",
                     expected = "red",
                     actual = "blue",
                     distance = Distance(BigDecimal("0.00"))
                  ),
                  AtomicMismatch(
                     field = "shape",
                     expected = "circle",
                     actual = "triangle",
                     distance = Distance(BigDecimal("0.25"))
                  ),
               ),
               distance = Distance(BigDecimal.ZERO)
            )
      }
   }
}
