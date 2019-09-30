package com.sksamuel.kotest.runner.junit4

import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import io.kotest.specs.FreeSpec

class HelloWorldTest : FreeSpec() {
  init {
    "first test ()" {
      1.shouldBeLessThan(2)
    }
    "string tests .@#@$#(!)@#" - {
      "substring" {
        "helloworld".shouldContain("world")
      }
      "startsWith" {
        "hello".shouldStartWith("he")
      }
    }
  }
}
