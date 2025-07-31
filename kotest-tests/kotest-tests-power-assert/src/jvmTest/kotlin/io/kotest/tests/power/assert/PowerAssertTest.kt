package io.kotest.tests.power.assert

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PowerAssertTest : FunSpec() {
   init {

      test("power assert should work with basic chains") {
         val hello = "Hello"
         val world = "world!"
         val error = shouldFail {
            hello.substring(1, 3) shouldBe world.substring(1, 4)
         }
         error.message!!.trim() shouldBe """
hello.substring(1, 3) shouldBe world.substring(1, 4)
|     |                        |     |
|     |                        |     orl
|     |                        world!
|     el
Hello

expected:<"orl"> but was:<"el">""".trim()
      }
   }
}

data class Person(val name: String, val address: Address)
data class Address(val street: String, val city: String, val zip: String)
