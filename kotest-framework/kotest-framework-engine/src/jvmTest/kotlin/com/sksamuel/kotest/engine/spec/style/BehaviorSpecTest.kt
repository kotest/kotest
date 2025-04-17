package com.sksamuel.kotest.engine.spec.style

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

               xwhen("should be ignored") {
                  error("boom")
               }

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
                     val upper = together.uppercase()

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
         xthen("should be ignored") {
            error("boom")
         }
      }

      Context("context wrapper can be added to given scope") {
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

                  xwhen("should be ignored") {
                     error("boom")
                  }

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
                        val upper = together.uppercase()

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
            xthen("should be ignored") {
               error("boom")
            }
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

      context("context a") {
         given("b") {
            `when`("c") {
               then("d") {
                  1.shouldBeLessThan(2)
               }
               val counter = AtomicInteger(0)
               then("with config").config(enabled = true) {
                  counter.incrementAndGet()
               }
               counter.get() shouldBe 1
            }
         }
         xgiven("c") {
            error("boom")
         }
         xGiven("d") {
            error("boom")
         }
      }

      xGiven("should be ignored 1") {
         error("boom")
      }

      xgiven("should be ignored 2") {
         error("boom")
      }

      xgiven("should be ignored 3") {
         `when`("should be ignored") {
            error("boom")
         }
      }

      xgiven("should be ignored 4") {
         `when`("should be ignored a") {
            then("should be ignored b") {
               error("boom")
            }
         }
      }

      xcontext("should be ignored 5") {
         given("should be ignored") {
            error("boom")
         }
      }

      xcontext("should be ignored 6") {
         given("should be ignored") {
            `when`("should be ignored") {
               error("boom")
            }
         }
      }

      xcontext("should be ignored 7") {
         given("should be ignored") {
            `when`("should be ignored a") {
               then("should be ignored b") {
                  error("boom")
               }
            }
         }
      }

      context("A") {

         counter.incrementAndGet()

         given("b") {

            counter.incrementAndGet()

            and("c") {

               counter.incrementAndGet()

               and("d") {

                  counter.incrementAndGet()

                  `when`("e") {

                     counter.incrementAndGet()

                     and("f") {
                        counter.incrementAndGet()
                        then("g") {
                           counter.incrementAndGet()
                        }
                        xthen("should be ignored") {
                           error("boom")
                        }
                     }
                  }

                  xwhen("should be ignored") {
                     error("boom")
                  }
               }
            }
         }
      }

      given("something delay in given scope") {
         launch { delay(1) }
         `when`("something delay in when scope") {
            launch { delay(1) }
            and("something delay in when scope provided by and") {
               Then("to keep context happy") {}
               launch { delay(1) }
            }
            then("one should be one") {
               1 shouldBe 1
            }
         }
      }

      context("something delay in context scope") {
         launch { delay(1) }
         given("something delay in given scope") {
            launch { delay(1) }
            `when`("something delay in when scope") {
               launch { delay(1) }
               and("something delay in when scope provided by and") {
                  Then("to keep context happy") {}
                  launch { delay(1) }
               }
               then("one should be one") {
                  1 shouldBe 1
               }
            }
         }
      }
   }

   override suspend fun afterSpec(spec: Spec) {
      counter.get() shouldBe 7
   }
}
