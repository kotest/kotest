package com.sksamuel.kotest.properties

import io.kotest.matchers.comparables.gt
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldHaveSameLengthAs
import io.kotest.matchers.string.shouldMatch
import io.kotest.properties.assertAll
import io.kotest.shouldBe
import io.kotest.shouldThrow
import io.kotest.specs.StringSpec

fun reverse(a: String): String = a.reversed()
fun concat(a: String, b: String): String = a + b

class ExtensionAssertAllTest : StringSpec({

  "reverse should maintain string length" {
    ::reverse.assertAll { input, output ->
      input.shouldHaveSameLengthAs(output)
    }
  }

  "KFunction1 should report errors" {
    shouldThrow<AssertionError> {
      ::reverse.assertAll { input, _ ->
        input.shouldHaveSameLengthAs(input + input)
      }
    }.message!!.split("\n").run {
      this[0] shouldMatch "Property failed for"
      this[1] shouldMatch "Arg 0: . \\(shrunk from .*\\)"
      this[2] shouldMatch "after 1 attempts"
      this[3] shouldMatch "Caused by: \\S+ should have the same length as \\S+"
    }
  }

  "concat should have consistent lengths" {
    ::concat.assertAll { a, b, output ->
      output.shouldHaveLength(a.length + b.length)
    }
  }
  "KFunction2 should report errors" {
    shouldThrow<AssertionError> {
      ::concat.assertAll { _, _, output ->
        output.length shouldBe gt(output.length + 5)
      }
    }.message!!.split("\n").run {
      this[0] shouldMatch "Property failed for"
      this[1] shouldMatch "Arg 0: <empty string>.*"
      this[2] shouldMatch "Arg 1: <empty string>.*"
      this[3] shouldMatch "after \\d+ attempts"
      this[4] shouldMatch "Caused by: \\d+ should be > \\d+"
    }
  }
})
