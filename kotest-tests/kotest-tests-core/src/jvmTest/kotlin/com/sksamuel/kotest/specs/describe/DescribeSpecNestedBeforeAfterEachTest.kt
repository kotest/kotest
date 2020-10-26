package com.sksamuel.kotest.specs.describe

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class DescribeSpecNestedBeforeAfterEachTest : DescribeSpec({
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

    describe("foo") {
        a shouldBe "beforeSpec"

        beforeEach {
            a shouldBe "beforeEachRoot"
            a = "beforeEachFoo"
        }

        afterEach {
            a = "afterEachFoo"
        }

        it("b") {
            a shouldBe "beforeEachFoo"
            a = "testB"
        }

        it("e") {
            a shouldBe "beforeEachFoo"
            a = "testE"
        }

        describe("bar") {
            a shouldBe "afterEachFoo"

            it("f") {
                a shouldBe "beforeEachFoo"
                a = "testF"
            }

            it("g") {
                a shouldBe "beforeEachFoo"
                a = "testG"
            }
        }

        it("h") {
            a shouldBe "beforeEachFoo"
            a = "testH"
        }
    }

    afterSpec {
        a shouldBe "afterEachFoo"
    }
})
