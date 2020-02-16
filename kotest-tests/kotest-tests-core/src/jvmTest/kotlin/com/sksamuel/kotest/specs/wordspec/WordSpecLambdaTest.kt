package com.sksamuel.kotest.specs.wordspec

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class WordSpecLambdaTest : WordSpec({

  var name: String? = null

  "the name" should {
    "start off null" {
      name.shouldBe(null)
    }
    name = "foo"
    "now be foo" {
      name.shouldBe("foo")
    }
    "it should still be foo" {
      name.shouldBe("foo")
    }
    name = "koo"
    "now be koo" {
      name.shouldBe("koo")
    }
  }

  "the second context" should {
    "inherit the state from context 1" {
      name shouldBe "koo"
    }
    name = "roo"
    "allow the name to be changed" {
      name shouldBe "roo"
    }
  }
})
