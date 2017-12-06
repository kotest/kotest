package io.kotlintest.specs

import io.kotlintest.matchers.shouldBe

class WordSpecLambdaTest : WordSpec({
    "String.length" should {
        "return the length of the string" {
            "sammy".length shouldBe 5
            "".length shouldBe 0
        }
    }
})