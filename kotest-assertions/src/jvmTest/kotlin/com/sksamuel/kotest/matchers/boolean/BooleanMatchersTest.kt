package com.sksamuel.kotest.matchers.boolean

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.booleans.shouldNotBeFalse
import io.kotest.matchers.booleans.shouldNotBeTrue
import io.kotest.shouldBe
import io.kotest.shouldThrow
import io.kotest.specs.FreeSpec
import org.opentest4j.AssertionFailedError

@Suppress("SimplifyBooleanWithConstants")
class BooleanMatchersTest : FreeSpec() {


    init {
        "Boolean shouldBeTrue should not fail for true booleans" {
            true.shouldBeTrue()
            (3 + 3 == 6).shouldBeTrue()
        }

        "Boolean shouldBeTrue should fail for false booleans" - {
            val thrownException = shouldThrow<AssertionFailedError> { false.shouldBeTrue() }

            "Failure exception should expect true" {
                thrownException.expected.value shouldBe "true"
            }

            "Failure exception should have actual false" {
                thrownException.actual.value shouldBe "false"
            }
        }

        "Boolean shouldBeFalse should not fail for false booleans" {
            false.shouldBeFalse()
            (3 + 3 == 42).shouldBeFalse()
        }

        "Boolean shouldBeFalse should fail for true booleans" - {
            val thrownException = shouldThrow<AssertionFailedError> { true.shouldBeFalse() }

            "Failure exception should expect false" {
                thrownException.expected.value shouldBe "false"
            }

            "Failure exception should have actual true" {
                thrownException.actual.value shouldBe "true"
            }
        }

        "Boolean shouldNotBeFalse should not fail for true booleans" {
            true.shouldNotBeFalse()
            (3 + 3 == 6).shouldNotBeFalse()
        }

        "Boolean shouldNotBeFalse should fail for false booleans" {
            shouldThrow<AssertionError> {
                false.shouldNotBeFalse()
            }.message shouldBe "false should not equal false"

            shouldThrow<AssertionError> {
                (3 + 3 == 7).shouldNotBeFalse()
            }.message shouldBe "false should not equal false"
        }

        "Boolean shouldNotBeTrue should not fail for false booleans" {
            false.shouldNotBeTrue()
            (3 + 3 == 7).shouldNotBeTrue()
        }

        "Boolean shouldNotBeTrue should fail for true booleans" {
            shouldThrow<AssertionError> {
                true.shouldNotBeTrue()
            }.message shouldBe "true should not equal true"

            shouldThrow<AssertionError> {
                (3 + 3 == 6).shouldNotBeTrue()
            }.message shouldBe "true should not equal true"
        }
    }
}
