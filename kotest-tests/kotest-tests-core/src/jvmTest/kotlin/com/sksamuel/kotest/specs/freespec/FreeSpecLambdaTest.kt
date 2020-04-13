package com.sksamuel.kotest.specs.freespec

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FreeSpecLambdaTest : FreeSpec({

  var name: String? = null

  "context 1" - {
    "the name should start off null" {
      name.shouldBe(null)
    }
    name = "foo"
    "now the name should be set to foo" - {
      name.shouldBe("foo")
      "should still be foo for this nested test" {
        name.shouldBe("foo")
      }
      name = "boo"
      "now the name should be boo" {
        name.shouldBe("boo")
      }
    }
    "it should still be boo as this test should run after all the above" {
      name.shouldBe("boo")
    }
    name = "koo"
    "now the name should be set to koo" {
      name.shouldBe("koo")
    }
  }

  "context 2 should run after context 1" - {
    "name should still be the last value which was koo" {
      name shouldBe "koo"
    }
  }
})
