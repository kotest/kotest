package com.sksamuel.kotest.runner.junit4

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import io.kotest.runner.junit4.KotestTestRunner
import org.junit.runner.RunWith

@RunWith(KotestTestRunner::class)
class HelloWorldTest : FreeSpec() {

    init {
        "First Test" {
            1.shouldBeLessThan(2)
        }

        "String tests #@!*!$" - {
            "substring" {
                "helloworld".shouldContain("world")
            }

            "startwith" {
                "hello".shouldStartWith("he")
            }
        }
    }

}
