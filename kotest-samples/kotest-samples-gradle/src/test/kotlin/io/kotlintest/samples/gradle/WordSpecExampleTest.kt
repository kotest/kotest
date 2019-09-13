package io.kotest.samples.gradle

import io.kotest.matchers.haveLength
import io.kotest.specs.WordSpec

class WordSpecExampleTest : WordSpec() {

  init {
    "A String" should {
      "Report correct length" {
        "wibble" should haveLength(6)
      }
    }
    "given a potato" `when` {
      "planted it" should {
        "grow" {

        }
      }
    }
  }
}