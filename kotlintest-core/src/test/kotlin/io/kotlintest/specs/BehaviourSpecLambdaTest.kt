package io.kotlintest.specs

import io.kotlintest.matchers.shouldBe

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