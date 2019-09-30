package io.kotest.samples.maven

import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.specs.WordSpec

class StringTest : WordSpec() {
  init {
    "A String" should {
      "Report correct length" {
        "wibble".shouldHaveLength(6)
      }
      "include substring" {
        "wibble".shouldContain("wib")
      }
    }
  }
}