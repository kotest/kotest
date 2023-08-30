package com.sksamuel.kotest.engine.spec.dsl.callbackorder

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FunSpecNestedBeforeAfterAnyTest : FunSpec({
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

    context("foo") {
        a shouldBe "beforeAnyRoot"

        beforeAny {
            a shouldBe "beforeAnyRoot"
            a = "beforeAnyFoo"
        }

        afterAny {
            a = "afterAnyFoo"
        }

        test("b") {
            a shouldBe "beforeAnyFoo"
            a = "testB"
        }

        test("e") {
            a shouldBe "beforeAnyFoo"
            a = "testE"
        }

        context("bar") {
            a shouldBe "beforeAnyFoo"

            test("f") {
                a shouldBe "beforeAnyFoo"
                a = "testF"
            }

            test("g") {
                a shouldBe "beforeAnyFoo"
                a = "testG"
            }
        }

        test("h") {
            a shouldBe "beforeAnyFoo"
            a = "testH"
        }
    }

    afterSpec {
        a shouldBe "afterAnyRoot"
    }
})
