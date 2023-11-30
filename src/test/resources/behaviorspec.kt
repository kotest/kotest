package com.sksamuel.kotest.specs.behavior

import io.kotest.core.spec.style.BehaviorSpec

class BehaviorSpecExample : BehaviorSpec() {

   init {
      given("a given") {
         `when`("a when") {
            then("a test") {
            }
            then("another test") {
            }
            xthen("a disabled then") {
            }
         }
         xwhen("disabled when") {
            then("this then should be disabled from its parent") {
            }
            then("this then should be disabled with config").config(invocations = 3) {
            }
         }
         and("an and") {
            `when`("a when") {
               then("a test") {
               }
            }
            and("an and in an and") {
               then("a test") {
               }
            }
            xWhen("disabled when") {
               then("this then should be disabled by nesting") {
               }
               xThen("an xdisabled then") {
               }
            }
         }
         xand("disabled and") {
            `when`("a nested when") {
               then("a test") {
               }
            }
         }
      }
      xgiven("disabled given") {
         When("disabled when") {
            then("a disabled then") {
            }
         }
         and("disabled and") {
            then("a test") {
            }
         }
      }
      xGiven("disabled given") {
         then("a nested then") {
      }
   }
   context("a context") {
      given("a nested given") {
         `when`("a when") {
            then("a test") {
            }
         }
      }
      xgiven("disabled given") {
         When("a disabled when") {
            then("a disabled test") {
            }
         }
      }
   }
}
