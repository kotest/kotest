package io.kotest.samples.gradle

import io.kotest.specs.FeatureSpec

class FeatureSpecExampleTest : FeatureSpec() {

  init {
    feature("a non empty stack") {
      and("popping an element") {
        scenario("the top most element should be removed and returned") {
          // test here
        }
      }
      and("pushing an element") {
        scenario("the new element should be added to the top of the stack") {
          // test here
        }
      }
    }
  }
}