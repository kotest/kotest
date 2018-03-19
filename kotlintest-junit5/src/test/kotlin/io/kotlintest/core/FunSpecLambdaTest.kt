package io.kotlintest.core

import io.kotlintest.matchers.shouldBe

class FunSpecLambdaTest : FunSpec({
    test("String.length should return the length of the string") {
        "sammy".length shouldBe 5
        "".length shouldBe 0
    }
})