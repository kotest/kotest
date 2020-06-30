package com.sksamuel.kotest.specs.behavior

import io.kotest.assertions.fail
import io.kotest.core.spec.style.BehaviorSpec

class BehaviorSpecExample : BehaviorSpec() {

  init {
     given("a given") {
        `when`("a when must be backticked because it is a keyword in kotlin") {
           then("a then") {
           }
           then("a then with config").config(enabled = false) {
              // test here
           }
           xthen("disabled") {
              fail("boom")
           }
           xThen("capitalized disabled then") {
              fail("boom")
           }
        }
        xWhen("capitalized disabled when") {
           fail("Boom")
        }
        and("another given scope") {

        }
        xAnd("capitalized disabled and") {
           fail("Boom")
        }
     }
     xgiven("disabled given") {
        `when`("should be ignored") {
           fail("boom")
        }
     }
     xGiven("a capitalized disabled given") {
        fail("boom")
     }
     Given("a capital given") {
        When("this when uses capitals to avoid backticks") {
           Then("Then also comes in captitals") {
              // test here
           }
           Then("a captial Then with config").config(enabled = false) {
              // test here
           }
        }
        xwhen("an xdisabled when") {
           fail("boom")
        }
        And("an and scope") {
           xthen("xdisabled test") {
              fail("boom")
           }
        }
     }
  }
}
