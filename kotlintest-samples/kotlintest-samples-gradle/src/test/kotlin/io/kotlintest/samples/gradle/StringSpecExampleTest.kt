package io.kotlintest.samples.gradle

import io.kotlintest.matchers.haveLength
import io.kotlintest.should
import io.kotlintest.specs.StringSpec

class StringSpecExampleTest : StringSpec() {

  init {
    "A string should report correct length" {
      "wibble" should haveLength(6)
    }
  }
}