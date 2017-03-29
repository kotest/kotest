package io.kotlintest

import io.kotlintest.matchers.shouldBe
import io.kotlintest.mock.`when`
import io.kotlintest.mock.mock
import io.kotlintest.specs.WordSpec

class MockFinalClassTest: WordSpec() {

    init {
        "kotlintest.mock" should {
            "mock final classes and methods" {
                val mockFinalClass = mock<FinalClass>()
                `when`(mockFinalClass.finalMethod()).thenReturn("mocked result")
                mockFinalClass.finalMethod() shouldBe "mocked result"
            }
        }
    }
}

private class FinalClass {
    fun finalMethod(): String = "Result"
}