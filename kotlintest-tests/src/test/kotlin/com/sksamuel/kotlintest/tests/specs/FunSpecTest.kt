package com.sksamuel.kotlintest.tests.specs

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class FunSpecTest : FunSpec() {

  var count = 0

  init {

    test("test without config") {
      "hello world".shouldStartWith("hello")
    }

    test("FunSpec should support config syntax").config(invocations = 5) {
      count += 1
    }
  }

  override fun afterSpec(description: Description, spec: Spec) {
    count.shouldBe(5)
  }
}