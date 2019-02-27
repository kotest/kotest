package com.sksamuel.kotlintest.specs.funspec

import io.kotlintest.Spec
import io.kotlintest.assertSoftly
import io.kotlintest.matchers.file.shouldNotExist
import io.kotlintest.matchers.string.shouldHaveLength
import io.kotlintest.matchers.string.shouldNotBeBlank
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import java.nio.file.Paths

class FunSpecTest : FunSpec() {

  var count = 0

  init {

    test("test without config") {
      "hello world".shouldStartWith("hello")
    }

    test("test with config").config(enabled = true) {
      assertSoftly {
        val file = Paths.get(".password")
        file.shouldNotExist()
      }
    }

    test("test with config and multiple invocations").config(invocations = 5) {
      count += 1
    }

    context("a context can hold tests") {
      test("foo") {
        "a".shouldNotBeBlank()
      }
      context("and even other contexts!") {
        test("wibble") {
          "hello".shouldHaveLength(5)
        }
      }
    }
  }

  override fun afterSpec(spec: Spec) {
    count.shouldBe(5)
  }
}