package com.sksamuel.kotest.specs.describe

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class DescribeSpecNestedBeforeAfterContainerTest : DescribeSpec({
    var a = ""

    beforeSpec {
        a shouldBe ""
        a = "beforeSpec"
    }

    beforeContainer {
        a = "beforeFooContainer"
    }

    afterContainer {
        a = "afterFooContainer"
    }

    describe("foo") {
        a shouldBe "beforeFooContainer"

        beforeContainer {
            a = "beforeBarContainer"
        }

        afterContainer {
            a = "afterBarContainer"
        }

        it("b") {
            a shouldBe "beforeFooContainer"
            a = "testB"
        }

        it("e") {
            a shouldBe "testB"
            a = "testE"
        }

        describe("bar") {
            a shouldBe "beforeBarContainer"

            it("f") {
                a shouldBe "beforeBarContainer"
                a = "testF"
            }

            it("g") {
                a shouldBe "testF"
                a = "testG"
            }
        }

        it("h") {
            a shouldBe "afterBarContainer"
            a = "testH"
        }
    }

    afterSpec {
        a shouldBe "afterFooContainer"
    }
})
