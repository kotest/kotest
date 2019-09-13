package io.kotest.samples.gradle

import io.kotest.matchers.haveLength
import io.kotest.should
import io.kotest.specs.StringSpec

class StringSpecExampleTest : StringSpec() {

  init {
    "A string should report correct length" {
      "wibble" should haveLength(6)
    }
    "A string should part 2" {
      "wibble" should haveLength(6)
    }
  }
}