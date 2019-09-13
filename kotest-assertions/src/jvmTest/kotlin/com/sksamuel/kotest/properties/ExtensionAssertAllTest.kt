package com.sksamuel.kotest.properties

import io.kotest.matchers.comparables.lt
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldHaveSameLengthAs
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
        input.shouldHaveSameLengthAs("qwqew")
      }
    }.message shouldBe "Property failed for\n" +
        "Arg 0: <empty string>\n" +
        "after 1 attempts\n" +
        "Caused by: <empty string> should have the same length as qwqew"
  }

  "concat should have consistent lengths" {
    ::concat.assertAll { a, b, output ->
      output.shouldHaveLength(a.length + b.length)
    }
  }
  "KFunction2 should report errors" {
    shouldThrow<AssertionError> {
      ::concat.assertAll { _, _, output ->
        output.length shouldBe lt(5)
      }
    }.message shouldBe "Property failed for\n" +
        "Arg 0: <empty string>\n" +
        "Arg 1: aaaaa (shrunk from \n" +
        "abc\n" +
        "123\n" +
        ")\n" +
        "after 3 attempts\n" +
        "Caused by: 9 should be < 5"
  }
})
