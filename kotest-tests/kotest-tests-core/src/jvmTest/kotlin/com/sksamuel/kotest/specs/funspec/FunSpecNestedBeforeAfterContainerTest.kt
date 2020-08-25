package com.sksamuel.kotest.specs.funspec

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FunSpecNestedBeforeAfterContainerTest : FunSpec({
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

    context("foo") {
        a shouldBe "beforeFooContainer"

        beforeContainer {
            a = "beforeBarContainer"
        }

        afterContainer {
            a = "afterBarContainer"
        }

        test("b") {
            a shouldBe "beforeFooContainer"
            a = "testB"
        }

        test("e") {
            a shouldBe "testB"
            a = "testE"
        }

        context("bar") {
            a shouldBe "beforeBarContainer"

            test("f") {
                a shouldBe "beforeBarContainer"
                a = "testF"
            }

            test("g") {
                a shouldBe "testF"
                a = "testG"
            }
        }

        test("h") {
            a shouldBe "afterBarContainer"
            a = "testH"
        }
    }

    afterSpec {
        a shouldBe "afterFooContainer"
    }
})
