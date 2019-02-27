package io.kotlintest.samples.gradle

import io.kotlintest.specs.ShouldSpec
import org.junit.platform.commons.annotation.Testable

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