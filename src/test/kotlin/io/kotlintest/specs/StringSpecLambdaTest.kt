package io.kotlintest.specs

import io.kotlintest.matchers.shouldBe

class StringSpecLambdaTest : StringSpec({
    "strings.length should return size of string" {
        "hello".length shouldBe 5
    }
})