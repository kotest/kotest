package com.sksamuel.kotest.specs.behavior

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class BehaviorSpecTest : BehaviorSpec() {

   private val counter = AtomicInteger(0)

   init {
      Given("The string foo") {
         val foo = "foo"

         And("The string jar") {
            val jar = "jar"

            When("I add them together") {
               val together = foo + jar

               Then("It should be foojar") {
                  together shouldBe "foojar"
               }
            }
         }

         And("The string bar") {
            val bar = "bar"

            And("The string fuz") {
               val fuz = "fuz"

               When("I add them together") {
                  val together = foo + bar + fuz

                  And("Count their length") {
                     val length = together.length

                     And("Sum one to it") {
                        val sum = length + 1

                        Then("It should be 10") {
                           sum shouldBe 10
                        }
                     }

                     Then("It should be 9") {
                        length shouldBe 9
                     }
                  }

                  Then("It should be foobarfuz") {
                     together shouldBe "foobarfuz"
                  }

                  And("I make it uppercase") {
                     val upper = together.toUpperCase()

                     Then("It should be FOOBARFUZ") {
                        upper shouldBe "FOOBARFUZ"
                     }

                     Then("It should start with F") {
                        upper shouldStartWith "F"
                     }
                  }
               }
            }
         }

         Then("It should be foo") {
            foo shouldBe "foo"
         }
      }


      given("a") {
         `when`("b") {
            then("c") {
               1.shouldBeLessThan(2)
            }
            val counter = AtomicInteger(0)
            then("with config").config(enabled = true) {
               counter.incrementAndGet()
            }
            counter.get() shouldBe 1
         }
      }

      given("A") {

         counter.incrementAndGet()

         and("b") {

            counter.incrementAndGet()

            and("c") {

               counter.incrementAndGet()

               `when`("d") {

                  counter.incrementAndGet()

                  and("e") {

                     counter.incrementAndGet()

                     then("f") {

                        counter.incrementAndGet()
                     }
                  }
               }
            }
         }
      }
   }

   override fun afterSpec(spec: Spec) {
      counter.get() shouldBe 6
   }
}
