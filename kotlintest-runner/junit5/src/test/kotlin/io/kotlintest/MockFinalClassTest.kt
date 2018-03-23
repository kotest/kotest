package io.kotlintest

import io.kotlintest.specs.AbstractWordSpec

class MockFinalClassTest: AbstractWordSpec() {

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