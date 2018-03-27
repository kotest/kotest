package io.kotlintest.samples.maven

import io.kotlintest.matchers.haveLength
import io.kotlintest.matchers.include
import io.kotlintest.should
import io.kotlintest.specs.WordSpec

class StringTest : WordSpec() {
  init {
    "A String" should {
      "Report correct length" {
        "wibble" should haveLength(6)
      }
      "include substring" {
        "wibble" should include("wib")
      }
    }
  }
}