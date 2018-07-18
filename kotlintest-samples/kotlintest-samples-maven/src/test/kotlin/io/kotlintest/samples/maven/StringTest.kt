package io.kotlintest.samples.maven

import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.matchers.string.shouldHaveLength
import io.kotlintest.specs.WordSpec

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