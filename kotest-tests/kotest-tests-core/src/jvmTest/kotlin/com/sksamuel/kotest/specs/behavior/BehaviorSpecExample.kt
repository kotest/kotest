package com.sksamuel.kotest.specs.behavior

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
        }
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
     }
  }
}
