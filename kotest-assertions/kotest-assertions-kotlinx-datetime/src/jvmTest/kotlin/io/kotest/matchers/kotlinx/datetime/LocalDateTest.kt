package io.kotest.matchers.kotlinx.datetime

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate

class LocalDateTest : StringSpec({

    "between() matcher should pass for in-range values and fail otherwise (pins the corrected KDoc examples)" {
        LocalDate(2019, 2, 16) shouldBe between(LocalDate(2019, 2, 15), LocalDate(2019, 2, 17))
        shouldFail {
            LocalDate(2019, 2, 15) shouldBe between(LocalDate(2019, 2, 16), LocalDate(2019, 2, 17))
        }
    }

    "between() matcher treats the start and end of the range as exclusive" {
        // value equal to the start of the range is not between
        shouldFail {
            LocalDate(2019, 2, 15) shouldBe between(LocalDate(2019, 2, 15), LocalDate(2019, 2, 17))
        }
        // value equal to the end of the range is not between
        shouldFail {
            LocalDate(2019, 2, 17) shouldBe between(LocalDate(2019, 2, 15), LocalDate(2019, 2, 17))
        }
        // values immediately inside the range are between
        LocalDate(2019, 2, 16) shouldBe between(LocalDate(2019, 2, 15), LocalDate(2019, 2, 17))
    }
})
