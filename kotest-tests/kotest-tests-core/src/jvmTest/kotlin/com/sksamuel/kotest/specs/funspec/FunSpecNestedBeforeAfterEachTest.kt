package com.sksamuel.kotest.specs.funspec

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FunSpecNestedBeforeAfterEachTest : FunSpec({
    var a = ""

    beforeSpec {
        a shouldBe ""
        a = "beforeSpec"
    }

    beforeEach {
        a = "beforeEachRoot"
    }

    afterEach {
        a = "afterEachRoot"
    }

    context("foo") {
        a shouldBe "beforeSpec"

        beforeEach {
            a shouldBe "beforeEachRoot"
            a = "beforeEachFoo"
        }

        afterEach {
            a = "afterEachFoo"
        }

        test("b") {
            a shouldBe "beforeEachFoo"
            a = "testB"
        }

        test("e") {
            a shouldBe "beforeEachFoo"
            a = "testE"
        }

        context("bar") {
            a shouldBe "afterEachFoo"

            test("f") {
                a shouldBe "beforeEachFoo"
                a = "testF"
            }

            test("g") {
                a shouldBe "beforeEachFoo"
                a = "testG"
            }
        }

        test("h") {
            a shouldBe "beforeEachFoo"
            a = "testH"
        }
    }

    afterSpec {
        a shouldBe "afterEachFoo"
    }
})
