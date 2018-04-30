package com.sksamuel.kotlintest.runner.junit4

import io.kotlintest.matchers.numerics.shouldBeLessThan
import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.specs.FreeSpec

class HelloWorldTest : FreeSpec() {
  init {
    "first test ()" {
      1.shouldBeLessThan(2)
    }
    "string tests .@#@$#(!)@#" {
      "substring" {
        "helloworld".shouldContain("world")
      }
      "startsWith" {
        "hello".shouldStartWith("he")
      }
    }
  }
}