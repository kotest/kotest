package com.sksamuel.kotest.specs.behavior

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class BehaviorSpecNestedBeforeAfterContainerTest: BehaviorSpec({
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

    given("foo") {
        a shouldBe "beforeFooContainer"

        beforeContainer {
            a = "beforeBarContainer"
        }

        afterContainer {
            a = "afterBarContainer"
        }

        then("b") {
            a shouldBe "beforeFooContainer"
            a = "testB"
        }

        then("e") {
            a shouldBe "testB"
            a = "testE"
        }

        `when`("bar") {
            a shouldBe "beforeBarContainer"

            then("f") {
                a shouldBe "beforeBarContainer"
                a = "testF"
            }

            then("g") {
                a shouldBe "testF"
                a = "testG"
            }
        }

        then("h") {
            a shouldBe "afterBarContainer"
            a = "testH"
        }
    }

    afterSpec {
        a shouldBe "afterFooContainer"
    }
})
