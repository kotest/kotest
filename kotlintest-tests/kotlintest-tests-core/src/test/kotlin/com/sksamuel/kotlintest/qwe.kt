package com.sksamuel.kotlintest

import io.kotlintest.specs.FunSpec

class Foo : FunSpec() {

  init {
    test("Throw") {
      throw RuntimeException()
    }

    test("Throw 2") {
      throw RuntimeException()
    }

    test("Throw 3") {
    }
  }
}