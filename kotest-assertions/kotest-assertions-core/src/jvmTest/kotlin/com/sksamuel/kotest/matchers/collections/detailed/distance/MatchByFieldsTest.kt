package com.sksamuel.kotest.matchers.collections.detailed.distance

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.detailed.distance.AtomicMismatch
import io.kotest.matchers.collections.detailed.distance.Distance
import io.kotest.matchers.collections.detailed.distance.Match
import io.kotest.matchers.collections.detailed.distance.MismatchByField
import io.kotest.matchers.collections.detailed.distance.matchByFields
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class MatchByFieldsTest: StringSpec() {
    init {
        "some fields match" {
            matchByFields("thing", redCircle, blueCircle) shouldBe
                    MismatchByField(
                        field = "thing",
                        expected = redCircle,
                        actual = blueCircle,
                        comparisonResults = listOf(
                            AtomicMismatch(field="color", expected="red", actual="blue", distance=Distance(BigDecimal.ZERO)),
                            Match(field="shape", value="circle")
                        ),
                        distance = Distance(BigDecimal("0.5"))
                    )
        }

        "all fields different" {
            matchByFields("thing", redCircle, blueTriangle) shouldBe
                    MismatchByField(
                        field = "thing",
                        expected = redCircle,
                        actual = blueTriangle,
                        comparisonResults = listOf(
                            AtomicMismatch(
                                field="color",
                                expected="red",
                                actual="blue",
                                distance=Distance(BigDecimal.ZERO)
                            ),
                            AtomicMismatch(
                                field="shape",
                                expected="circle",
                                actual="triangle",
                                distance=Distance(BigDecimal.ZERO)
                            ),
                        ),
                        distance = Distance(BigDecimal.ZERO))
        }
    }
}
