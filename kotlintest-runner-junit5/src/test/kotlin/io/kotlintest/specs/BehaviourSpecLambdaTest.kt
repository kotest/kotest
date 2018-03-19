package io.kotlintest.specs

import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec

class BehaviourSpecLambdaTest : BehaviorSpec({
    given("string.length") {
        `when`("using foobar") {
            val subject = "foobar"
            then("length is 6") {
                subject.length shouldBe 6
            }
        }
    }
})