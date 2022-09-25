package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.collections.shouldNotContainOnly
import io.kotest.matchers.throwable.shouldHaveMessage

class ShouldContainOnlyTest : WordSpec() {

    init {
        "containOnly" should {
            "test that an array contains given elements only" {
                val actual = arrayOf(1, 1, 2, 2, 3)
                actual.shouldContainOnly(1, 2, 3)
                actual shouldContainOnly arrayOf(1, 2, 3)
                actual.shouldNotContainOnly(3, 2)
                actual shouldNotContainOnly arrayOf(3)

                shouldThrow<AssertionError> {
                    actual.shouldContainOnly(3, 2)
                }
                shouldThrow<AssertionError> {
                    actual shouldContainOnly arrayOf(3, 2)
                }
                shouldThrow<AssertionError> {
                    actual.shouldNotContainOnly(1, 2, 3)
                }
                shouldThrow<AssertionError> {
                    actual shouldNotContainOnly arrayOf(1, 2, 3)
                }

                val actualNull: Array<Int>? = null
                shouldThrow<AssertionError> {
                    actualNull.shouldContainOnly(1, 2, 3)
                }.shouldHaveMessage("Expecting actual not to be null")
                shouldThrow<AssertionError> {
                    actualNull.shouldNotContainOnly()
                }.shouldHaveMessage("Expecting actual not to be null")
            }
        }
    }
}
