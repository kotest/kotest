package io.kotlintest.samples.gradle

import io.kotlintest.specs.ShouldSpec

class ShouldSpecExampleTest : ShouldSpec() {

  init {
      should("qweqwe").config(enabled=true) {
      }
  }
}