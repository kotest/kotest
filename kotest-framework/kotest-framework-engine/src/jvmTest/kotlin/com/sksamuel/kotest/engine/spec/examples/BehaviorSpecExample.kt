package com.sksamuel.kotest.engine.spec.examples

import io.kotest.assertions.AssertionErrorBuilder
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
             AssertionErrorBuilder.fail("boom")
           }
           xThen("capitalized disabled then") {
             AssertionErrorBuilder.fail("boom")
           }
        }
        xWhen("capitalized disabled when") {
           Then("to keep context happy") {}
          AssertionErrorBuilder.fail("Boom")
        }
        and("another given scope") {
           Then("to keep context happy") {}
        }
        xand("uncapitalized disabled and") {
           Then("to keep context happy") {}
          AssertionErrorBuilder.fail("Boom")
        }
        xAnd("capitalized disabled and") {
           Then("to keep context happy") {}
          AssertionErrorBuilder.fail("Boom")
        }
     }
     xgiven("disabled given") {
        `when`("should be ignored") {
           Then("to keep context happy") {}
          AssertionErrorBuilder.fail("boom")
        }
     }
     xGiven("a capitalized disabled given") {
        Then("to keep context happy") {}
       AssertionErrorBuilder.fail("boom")
     }
     Given("a capital given") {
        When("this when uses capitals to avoid backticks") {
           Then("Then also comes in captitals") {
              // test here
           }
           Then("a captial Then with config").config(enabled = false) {
              // test here
           }
           xThen("a captialized disabled Then") {
              error("boom")
           }
           xthen("an uncaptialized disabled Then") {
              error("boom")
           }
           xThen("a captialized disabled Then with config").config(invocations = 3) {
              error("boom")
           }
           xthen("an uncaptialized disabled Then with config").config(invocations = 3) {
              error("boom")
           }
        }
        xwhen("an xdisabled when") {
           Then("to keep context happy") {}
          AssertionErrorBuilder.fail("boom")
        }
        And("an and scope") {
           xthen("xdisabled test") {
             AssertionErrorBuilder.fail("boom")
           }
        }
     }
  }
}
