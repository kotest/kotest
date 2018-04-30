package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.shouldBe
import io.kotlintest.specs.ExpectSpec

class ExpectSpecLambdaTest : ExpectSpec({

  var name: String? = null

  context("context 1") {
    expect("the name should start off null") {
      name.shouldBe(null)
    }
    name = "foo"
    context("the name should be foo in this context") {
      name.shouldBe("foo")
      expect("should still be foo for this nested test") {
        name.shouldBe("foo")
      }
      name = "boo"
      expect("now the name should be boo") {
        name.shouldBe("boo")
      }
    }
    expect("it should still be boo as this test should run after all the above") {
      name.shouldBe("boo")
    }
    name = "koo"
    expect("now the name should be set to koo") {
      name.shouldBe("koo")
    }
  }

  context("context 2 should run after context 1") {
    expect("name should still be the last value which was koo") {
      name shouldBe "koo"
    }
  }
})