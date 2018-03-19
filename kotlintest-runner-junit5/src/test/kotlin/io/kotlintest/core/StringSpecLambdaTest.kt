package io.kotlintest.core

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class StringSpecLambdaTest : StringSpec({
    "strings.length should return size of string" {
        "hello".length shouldBe 5
    }
})