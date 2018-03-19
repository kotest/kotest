package io.kotlintest.core

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec

class ShouldSpecLambdaTest : ShouldSpec({
    should("return the length of the string") {
        "sammy".length shouldBe 5
        "".length shouldBe 0
    }
})