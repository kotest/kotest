package io.kotest.samples.gradle

import io.kotest.specs.ShouldSpec

class ShouldSpecExampleTest : ShouldSpec() {
  init {

    "context parent" {

      should("without config") {

      }
      should("with config").config(enabled = true) {
      }
    }

    should("without config") {
    }
    should("with config") {

    }
  }
}