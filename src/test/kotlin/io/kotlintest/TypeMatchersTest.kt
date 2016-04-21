package io.kotlintest

import io.kotlintest.matchers.be
import io.kotlintest.specs.WordSpec

class TypeMatchersTest : WordSpec() {

  init {

    "TypeMatchers.theSameInstanceAs" should {
      "should test that references are equal" {
        val b = listOf(1, 2, 3)
        val a = b
        val c = listOf(1, 2, 3)

        a should be theSameInstanceAs b
        expecting(TestFailedException::class) {
          a should be theSameInstanceAs c
        }
      }
    }
  }
}