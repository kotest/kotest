package com.sksamuel.kotest.engine.spec.dsl.callbackorder

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class BehaviorSpecNestedBeforeAfterAnyTest : BehaviorSpec({
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

    given("foo") {
        a shouldBe "beforeAnyRoot"

        beforeAny {
            a shouldBe "beforeAnyRoot"
            a = "beforeAnyFoo"
        }

        afterAny {
            a = "afterAnyFoo"
        }

        then("b") {
            a shouldBe "beforeAnyFoo"
            a = "testB"
        }

        then("e") {
            a shouldBe "beforeAnyFoo"
            a = "testE"
        }

        `when`("bar") {
            a shouldBe "beforeAnyFoo"

            then("f") {
                a shouldBe "beforeAnyFoo"
                a = "testF"
            }

            then("g") {
                a shouldBe "beforeAnyFoo"
                a = "testG"
            }
        }

        then("h") {
            a shouldBe "beforeAnyFoo"
            a = "testH"
        }
    }

    afterSpec {
        a shouldBe "afterAnyRoot"
    }
})
