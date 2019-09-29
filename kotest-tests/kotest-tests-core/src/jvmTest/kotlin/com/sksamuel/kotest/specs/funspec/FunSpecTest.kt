package com.sksamuel.kotest.specs.funspec

import io.kotest.Spec
import io.kotest.assertSoftly
import io.kotest.matchers.file.shouldNotExist
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldNotBeBlank
import io.kotest.matchers.string.shouldStartWith
import io.kotest.shouldBe
import io.kotest.specs.FunSpec
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
