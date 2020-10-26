package com.sksamuel.kotest.specs.behavior

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class BehaviorSpecNestedBeforeAfterEachTest : BehaviorSpec({
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

    given("foo") {
        a shouldBe "beforeSpec"

        beforeEach {
            a shouldBe "beforeEachRoot"
            a = "beforeEachFoo"
        }

        afterEach {
            a = "afterEachFoo"
        }

        then("b") {
            a shouldBe "beforeEachFoo"
            a = "testB"
        }

        then("e") {
            a shouldBe "beforeEachFoo"
            a = "testE"
        }

        `when`("bar") {
            a shouldBe "afterEachFoo"

            then("f") {
                a shouldBe "beforeEachFoo"
                a = "testF"
            }

            then("g") {
                a shouldBe "beforeEachFoo"
                a = "testG"
            }
        }

        then("h") {
            a shouldBe "beforeEachFoo"
            a = "testH"
        }
    }

    afterSpec {
        a shouldBe "afterEachFoo"
    }
})
