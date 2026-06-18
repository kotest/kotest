package com.sksamuel.kotest.matchers.optional

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.optional.beEmpty
import io.kotest.matchers.optional.bePresent
import io.kotest.matchers.optional.shouldBeEmpty
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.optional.shouldNotBeEmpty
import io.kotest.matchers.optional.shouldNotBePresent
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.throwable.shouldHaveMessage
import java.util.Optional

class OptionalMatchersTest : ShouldSpec({
    context("Empty optional") {
        val optional = Optional.empty<Any>()

        should("Be empty") {
            optional.shouldBeEmpty()
            optional should beEmpty()
        }

        should("Not be present") {
            optional.shouldNotBePresent()
            optional shouldNot bePresent()
        }

        should("Fail to be notEmpty") {
            shouldThrow<AssertionError> { optional.shouldNotBeEmpty() }
            shouldThrow<AssertionError> { optional shouldNot beEmpty() }
        }

        should("Fail to be present") {
            shouldThrow<AssertionError> { optional.shouldBePresent() }
            shouldThrow<AssertionError> { optional should bePresent() }
        }
    }

    context("Present optional") {
        val optional = Optional.of("A")

        should("Be present") {
            optional.shouldBePresent()
            optional should bePresent()
        }

        should("Return the present value for usage in more assertions") {
            optional.shouldBePresent() shouldBe "A"
        }

        should("Allow matchers with present value as a receiver") {
            optional shouldBePresent {
                this shouldBe "A"
            }
        }

        should("Allow matchers with present value as parameter") {
            optional shouldBePresent {
                it shouldBe "A"
            }
        }

        should("Execute code inside the receiver") {
            shouldThrow<RuntimeException> {
                optional shouldBePresent {
                    throw RuntimeException("Ensuring the block is actually executed")
                }
            } shouldHaveMessage "Ensuring the block is actually executed"

            shouldThrow<AssertionError> {
                optional shouldBePresent {
                    this shouldBe "B"
                }
            }
        }
    }
})
