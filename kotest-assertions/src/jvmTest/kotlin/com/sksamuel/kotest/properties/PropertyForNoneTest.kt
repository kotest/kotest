package com.sksamuel.kotest.properties

import io.kotest.matchers.comparables.gt
import io.kotest.properties.Gen
import io.kotest.properties.PropertyTesting
import io.kotest.properties.double
import io.kotest.properties.forNone
import io.kotest.properties.int
import io.kotest.properties.list
import io.kotest.properties.long
import io.kotest.properties.negativeIntegers
import io.kotest.properties.positiveIntegers
import io.kotest.properties.string
import io.kotest.shouldBe
import io.kotest.shouldThrow
import io.kotest.specs.StringSpec

class PropertyForNoneTest : StringSpec() {
  init {

     beforeSpec {
        PropertyTesting.shouldPrintShrinkSteps = false
     }

     afterSpec {
        PropertyTesting.shouldPrintShrinkSteps = true
     }

    "forNoneTestStrings" {
      forNone { a: String, b: String ->
        a + 1 == b + 2
      }
    }

    "forNoneTestBooleanDouble" {
      forNone { a: Boolean, b: Double ->
        a.toString() == b.toString()
      }
    }

    "forNone: one explicit argument with 1000 attempts" {
      var attempts = 0
      forNone(1000, Gen.string()) { a ->
        attempts++
        (a + "hi").endsWith("Bye")
      }
      attempts shouldBe 1000
    }

    "forNone: one explicit argument with 300 attempts" {
      var attempts = 0
      forNone(300, Gen.string()) { a ->
        attempts++
        (a + "hi").endsWith("Bye")
      }
      attempts shouldBe 300
    }

    "forNone: one explicit argument fails after 10 attempts" {
      var attempts = 0
      var elementA = ""
      val exception = shouldThrow<AssertionError> {
        forNone(10, Gen.string()) { a ->
          elementA = a
          attempts++
          attempts >= 10
        }
      }
      exception.message shouldBe "Property passed for\n$elementA\nafter 10 attempts"
    }

    "forNone: one explicit argument fails after 50 attempts" {
      var attempts = 0
      var elementA = 0
      val exception = shouldThrow<AssertionError> {
        forNone(50, Gen.int()) { a ->
          elementA = a
          attempts++
          attempts >= 50
        }
      }
      exception.message shouldBe "Property passed for\n$elementA\nafter 50 attempts"
    }

    "forNone: one explicit argument with 0 attempts" {
      val exception = shouldThrow<IllegalArgumentException> {
        forNone(0, Gen.int()) {
          false
        }
      }
      exception.message shouldBe "Iterations should be a positive number"
    }

    "forNone: one explicit argument with -100 attempts" {
      val exception = shouldThrow<IllegalArgumentException> {
        forNone(-100, Gen.int()) {
          false
        }
      }
      exception.message shouldBe "Iterations should be a positive number"
    }

    "forNone: one implicit argument with 1000 attempts" {
      var attempts = 0
      forNone { _: String ->
        attempts++
        false
      }
      attempts shouldBe 1000
    }

    "forNone: one implicit argument with 20 attempts" {
      var attempts = 0
      forNone(20) { _: Double ->
        attempts++
        false
      }
      attempts shouldBe 20
    }

    "forNone: one implicit argument with 100 attempts" {
      var attempts = 0
      forNone(100) { _: Double ->
        attempts++
        false
      }
      attempts shouldBe 100
    }

    "forNone: one explicit generator with one value and default attempts" {
        // 30 should be ignored as we have many always cases
        var attempts = 0
        Gen.int().forNone { _ ->
          attempts++
          false
        }
        attempts shouldBe 1000
      }

    "forNone: one explicit generator with two values and default attempts" {
        // 30 should be ignored as we have many always cases
        var attempts = 0
        Gen.int().forNone { a, b ->
          attempts++
          a + b != b + a
        }
        attempts shouldBe 1000
      }

    "forNone: one explicit generator with two values and 100 attempts" {
        // 30 should be ignored as we have many always cases
        var attempts = 0
        Gen.int().forNone(100) { a, b ->
          attempts++
          a + b != b + a
        }
        attempts shouldBe 100
      }

    "forNone: two explicit arguments with 1000 attempts" {
      var attempts = 0
      forNone(Gen.int(), Gen.int()) { a, b ->
        attempts++
        a + b != b + a
      }
      attempts shouldBe 1000
    }

    "forNone: two explicit arguments with 360 attempts" {
      var attempts = 0
      forNone(360, Gen.int(), Gen.int()) { a, b ->
        attempts++
        a * b != b * a
      }
      attempts shouldBe 360
    }

    "forNone: two explicit arguments 1492 attempts" {
      var attempts = 0
      forNone(1492, Gen.int(), Gen.int()) { a, b ->
        attempts++
        -a * b == a + b + 5
      }
      attempts shouldBe 1492
    }

    "forNone: two explicit arguments failing after 26 attempts" {
      var attempts = 0
      var string = ""
      var double = 0.0
      val exception = shouldThrow<AssertionError> {
        forNone(Gen.string(), Gen.double()) { a, b ->
          string = a
          double = b
          attempts++
          attempts >= 26
        }
      }
      exception.message shouldBe "Property passed for\n$string\n$double\nafter $attempts attempts"
    }

    "forNone: two explicit arguments failing after 56 attempts" {
      var attempts = 0
      var positive = 0
      var negative = 0
      val exception = shouldThrow<AssertionError> {
        forNone(Gen.positiveIntegers(), Gen.negativeIntegers()) { p, n ->
          attempts++
          positive = p
          negative = n
          attempts >= 56
        }
      }
      exception.message shouldBe "Property passed for\n$positive\n$negative\nafter $attempts attempts"
    }

    "forNone: two implicit generators default attempts" {
      var attempts = 0
      forNone { a: Int, b: String ->
        attempts++
        a.toString() == b
      }
      attempts shouldBe 1000
    }

    "forNone: two implicit generators 1066 attempts" {
      var attempts = 0
      forNone(1066) { a: Int, b: Int ->
        attempts++
        a == b && b != a
      }
      attempts shouldBe 1066
    }

    "forNone: two implicit generators 300 attempts" {
      var attempts = 0
      forNone(300) { a: String, b: String ->
        attempts++
        a == b && a != b
      }
      attempts shouldBe 300
    }

    "forNone: three explicit generators 1000 attempts" {
      var attempts = 0
      forNone(Gen.int(), Gen.int(), Gen.int()) { _, _, _ ->
        attempts++
        false
      }
      attempts shouldBe 1000
    }

    "forNone: three explicit generators 860 attempts" {
      var attempts = 0
      forNone(860, Gen.int(), Gen.int(), Gen.int()) { _, _, _ ->
        attempts++
        false
      }

      attempts shouldBe 860
    }

    "forNone: three explicit generators fails after 20 attempts" {
      var attempts = 0
      var elementA = 0
      var elementB = 0
      var elementC = 0
      val exception = shouldThrow<AssertionError> {
        forNone(1000, Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
          attempts++
          elementA = a
          elementB = b
          elementC = c
          attempts >= 20
        }
      }
      exception.message shouldBe "Property passed for\n$elementA\n$elementB\n$elementC\nafter 20 attempts"
    }

    "forNone: three explicit generators fails after 350 attempts" {
      var attempts = 0
      var elementA = 0
      var elementB = 0
      var elementC = 0
      val exception = shouldThrow<AssertionError> {
        forNone(350, Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
          attempts++
          elementA = a
          elementB = b
          elementC = c
          attempts >= 350
        }
      }

      exception.message shouldBe "Property passed for\n$elementA\n$elementB\n$elementC\nafter 350 attempts"
    }

    "forNone: three explicit generators 0 attempts" {
      val exception = shouldThrow<IllegalArgumentException> {
        forNone(0, Gen.int(), Gen.int(), Gen.int()) { _, _, _ ->
          false
        }
      }

      exception.message shouldBe "Iterations should be a positive number"
    }

    "forNone: three implicit generators 1000 attempts" {
      var attempts = 0
      forNone { _: Int, _: Int, _: Int ->
        attempts++
        false
      }
      attempts shouldBe 1000
    }

    "forNone: three implicit generators 700 attempts" {
      var attempts = 0
      forNone(700) { _: Int, _: Int, _: Int ->
        attempts++
        false
      }
      attempts shouldBe 700
    }

    "forNone: three implicit generators 1200 attempts" {
      var attempts = 0
      forNone(1200) { _: Int, _: Int, _: Int ->
        attempts++
        false
      }

      attempts shouldBe 1200
    }

    "forNone: four explicit generators default attempts" {
      var attempts = 0
      forNone(Gen.int(), Gen.string(), Gen.long(), Gen.double()) { _, _, _, _ ->
        attempts++
        false
      }
      attempts shouldBe gt(600)
    }

    "forNone: four explicit generators 5555 attempts" {
      var attempts = 0
      forNone(5555, Gen.int(), Gen.string(), Gen.double(), Gen.long()) { _, _, _, _ ->
        attempts++
        false
      }
      attempts shouldBe 5555
    }

    "forNone: four explicit generators failure at attempt 12" {
      var attempts = 0
      var elementA = 0
      var elementB = 0.0
      var elementC = 0L
      var elementD = emptyList<String>()
      val exception = shouldThrow<AssertionError> {
        forNone(Gen.int(), Gen.double(), Gen.long(), Gen.list(Gen.string())) { a, b, c, d ->
          elementA = a
          elementB = b
          elementC = c
          elementD = d
          attempts++
          attempts >= 12
        }
      }
      exception.message shouldBe "Property passed for\n$elementA\n$elementB\n$elementC\n$elementD\nafter $attempts attempts"
    }

    "forNone: four explicit generators failure at attempt 44" {
      var attempts = 0
      var elementA = 0
      var elementB = ""
      var elementC = 0L
      var elementD = 0.0
      val exception = shouldThrow<AssertionError> {
        forNone(Gen.int(), Gen.string(), Gen.long(), Gen.double()) { a, b, c, d ->
          attempts++
          elementA = a
          elementB = b
          elementC = c
          elementD = d
          attempts >= 44
        }
      }
      exception.message shouldBe "Property passed for\n$elementA\n$elementB\n$elementC\n$elementD\nafter $attempts attempts"
    }

    "forNone: four implicit generators default attempts" {
      var attempts = 0
      forNone { _: Int, _: String, _: String, _: Long ->
        attempts++
        false
      }
      attempts shouldBe 1000
    }

    "forNone: four implicit generators 1444 attempts" {
      var attempts = 0
      forNone(1444) { _: Long, _: Int, _: Double, _: String ->
        attempts++
        false
      }
      attempts shouldBe 1444
    }

    "forNone: five explicit generators default attempts" {
      var attempts = 0
      forNone(Gen.string(), Gen.int(), Gen.long(), Gen.double(), Gen.string()) { _, _, _, _, _ ->
        attempts++
        false
      }
      attempts shouldBe 1000
    }

    "forNone: five explicit generators 3411 attempts" {
      var attempts = 0
      forNone(3411, Gen.int(), Gen.string(), Gen.double(), Gen.long(), Gen.int()) { _, _, _, _, _ ->
        attempts++
        false
      }
      attempts shouldBe 3411
    }

    "forNone: five explicit generators fails after 2 attempts" {
      var attempts = 0
      var elementA = 0
      var elementB = 0
      var elementC = 0
      var elementD = 0
      var elementE = 0
      val exception = shouldThrow<AssertionError> {
        forNone(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d, e ->
          attempts++
          elementA = a
          elementB = b
          elementC = c
          elementD = d
          elementE = e
          attempts >= 2
        }
      }

      exception.message shouldBe
          "Property passed for\n$elementA\n$elementB\n$elementC\n$elementD\n$elementE\nafter $attempts attempts"
    }

    "forNone: five explicit generators fails after 2300 attempts" {
      var attempts = 0
      var elementA = 0
      var elementB = 0
      var elementC = 0
      var elementD = 0
      var elementE = 0
      val exception = shouldThrow<AssertionError> {
        forNone(2300, Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d, e ->
          attempts++
          elementA = a
          elementB = b
          elementC = c
          elementD = d
          elementE = e
          attempts >= 2300
        }
      }

      exception.message shouldBe
          "Property passed for\n$elementA\n$elementB\n$elementC\n$elementD\n$elementE\nafter $attempts attempts"
    }

    "forNone: five explicit generators 0 attempts" {
      val exception = shouldThrow<IllegalArgumentException> {
        forNone(0, Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _ ->
          false
        }
      }

      exception.message shouldBe "Iterations should be a positive number"
    }

    "forNone: five explicit generators -300 attempts" {
      val exception = shouldThrow<IllegalArgumentException> {
        forNone(-300, Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _ ->
          false
        }
      }

      exception.message shouldBe "Iterations should be a positive number"
    }

    "forNone: five implicit generators default attempts" {
      var attempts = 0
      forNone { _: Int, _: Int, _: Int, _: Int, _: Int ->
        attempts++
        false
      }
      attempts shouldBe 1000
    }

    "forNone: five implicit generators 4144 attempts" {
      var attempts = 0
      forNone(4144) { _: Int, _: Int, _: Int, _: Int, _: Int ->
        attempts++
        false
      }
      attempts shouldBe 4144
    }

    "forNone: six explicit generators default attempts" {
      var attempts = 0
      forNone(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _, _ ->
        attempts++
        false
      }
      attempts shouldBe 1000
    }

    "forNone: six explicit generators 17897 attempts" {
      var attempts = 0
      forNone(17897, Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _, _ ->
        attempts++
        false
      }
      attempts shouldBe 17897
    }

    "forNone: six explicit generators failing after 320 attempts" {
      var attempts = 0
      var elementA = 0
      var elementB = 0
      var elementC = 0
      var elementD = 1
      var elementE = 0
      var elementF = 0
      val exception = shouldThrow<AssertionError> {
        forNone(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d, e, f ->
          attempts++
          elementA = a
          elementB = b
          elementC = c
          elementD = d
          elementE = e
          elementF = f
          attempts >= 320
        }
      }
      exception.message shouldBe
          "Property passed for\n$elementA\n$elementB\n$elementC\n$elementD\n$elementE\n$elementF\nafter $attempts attempts"
    }

    "forNone: six explicit generators failing after 15 attempts" {
      var attempts = 0
      var elementA = 0
      var elementB = 0
      var elementC = 0
      var elementD = 1
      var elementE = 0
      var elementF = 0
      val exception = shouldThrow<AssertionError> {
        forNone(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d, e, f ->
          attempts++
          elementA = a
          elementB = b
          elementC = c
          elementD = d
          elementE = e
          elementF = f
          attempts >= 15
        }
      }
      exception.message shouldBe
          "Property passed for\n$elementA\n$elementB\n$elementC\n$elementD\n$elementE\n$elementF\nafter $attempts attempts"
    }

    "forNone: six implicit generators default attempts" {
      var attempts = 0
      forNone { _: Int, _: Int, _: Int, _: Int, _: Int, _: Int ->
        attempts++
        false
      }
      attempts shouldBe 1000
    }

    "forNone: six implicit generators 30000 attempts" {
      var attempts = 0
      forNone(30000) { _: Int, _: Int, _: Int, _: Int, _: Int, _: Int ->
        attempts++
        false
      }
      attempts shouldBe 30000
    }

  }
}
