package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class FreeSpecLambdaTest : FreeSpec({

  var name: String? = null

  "context 1" - {
    name = "foo"
    "now the name should be set to foo" {
      name shouldBe "foo"
    }
    "it should still be foo" {
      name shouldBe "foo"
    }
    "here is a context after a test" - {
      name = "wibble"
      "now the name should be set to wibble" {
        name shouldBe "wibble"
      }
      "it should still be wibble" {
        name shouldBe "wibble"
      }
    }
  }

  "context 2 should run after context 1" - {
    "name should still be left as wibble " {
      name shouldBe "wibble"
    }
  }

  "context 3 should run after context 2" - {
    name = null
    "name should now be null " {
      name shouldBe null
    }
    "here is another context changing the name" - {
      name = "wobble"
      "finally the name should be wobble" {
        name shouldBe "wobble"
      }
    }
  }
})