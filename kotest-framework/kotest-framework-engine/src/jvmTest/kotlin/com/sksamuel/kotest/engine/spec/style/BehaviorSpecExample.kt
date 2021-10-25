package com.sksamuel.kotest.engine.spec.style

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
           Then("to keep context happy") {}
           fail("Boom")
        }
        and("another given scope") {
           Then("to keep context happy") {}
        }
        xAnd("capitalized disabled and") {
           Then("to keep context happy") {}
           fail("Boom")
        }
     }
     xgiven("disabled given") {
        `when`("should be ignored") {
           Then("to keep context happy") {}
           fail("boom")
        }
     }
     xGiven("a capitalized disabled given") {
        Then("to keep context happy") {}
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
           Then("to keep context happy") {}
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
