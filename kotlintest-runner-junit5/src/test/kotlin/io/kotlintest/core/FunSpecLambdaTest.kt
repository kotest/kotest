package io.kotlintest.core

import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class FunSpecLambdaTest : FunSpec({
    test("String.length should return the length of the string") {
        "sammy".length shouldBe 5
        "".length shouldBe 0
    }
})