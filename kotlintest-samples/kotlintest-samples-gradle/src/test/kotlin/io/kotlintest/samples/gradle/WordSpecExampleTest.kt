package io.kotlintest.samples.gradle

import io.kotlintest.matchers.haveLength
import io.kotlintest.specs.WordSpec

class WordSpecExampleTest : WordSpec() {

  init {
    "A String" should {
      "Report correct length" {
        "wibble" should haveLength(6)
      }
    }
  }
}