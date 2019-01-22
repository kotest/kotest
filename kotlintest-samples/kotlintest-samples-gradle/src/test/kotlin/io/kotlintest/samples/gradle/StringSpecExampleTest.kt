package io.kotlintest.samples.gradle

import io.kotlintest.matchers.haveLength
import io.kotlintest.should
import io.kotlintest.specs.StringSpec

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