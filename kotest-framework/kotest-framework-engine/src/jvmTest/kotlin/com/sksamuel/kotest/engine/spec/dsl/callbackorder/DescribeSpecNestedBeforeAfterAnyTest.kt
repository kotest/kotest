package com.sksamuel.kotest.engine.spec.dsl.callbackorder

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class DescribeSpecNestedBeforeAfterAnyTest : DescribeSpec({
    var a = ""

    beforeSpec {
        a shouldBe ""
        a = "beforeSpec"
    }

    beforeAny {
        a = "beforeAnyRoot"
    }

    afterAny {
        a = "afterAnyRoot"
    }

    describe("foo") {
        a shouldBe "beforeAnyRoot"

        beforeAny {
            a shouldBe "beforeAnyRoot"
            a = "beforeAnyFoo"
        }

        afterAny {
            a = "afterAnyFoo"
        }

        it("b") {
            a shouldBe "beforeAnyFoo"
            a = "testB"
        }

        it("e") {
            a shouldBe "beforeAnyFoo"
            a = "testE"
        }

        describe("bar") {
            a shouldBe "beforeAnyFoo"

            it("f") {
                a shouldBe "beforeAnyFoo"
                a = "testF"
            }

            it("g") {
                a shouldBe "beforeAnyFoo"
                a = "testG"
            }
        }

        it("h") {
            a shouldBe "beforeAnyFoo"
            a = "testH"
        }
    }

    afterSpec {
        a shouldBe "afterAnyRoot"
    }
})
