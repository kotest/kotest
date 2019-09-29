package com.sksamuel.kotest.properties

import io.kotest.assertions.shouldFail
import io.kotest.matchers.comparables.gt
import io.kotest.matchers.comparables.gte
import io.kotest.properties.Gen
import io.kotest.properties.PropertyTesting
import io.kotest.properties.double
import io.kotest.properties.forAll
import io.kotest.properties.int
import io.kotest.properties.long
import io.kotest.properties.positiveIntegers
import io.kotest.properties.string
import io.kotest.shouldBe
import io.kotest.shouldThrow
import io.kotest.specs.StringSpec
import kotlin.math.abs

class PropertyForAllTest : StringSpec() {
  init {

     beforeSpec {
        PropertyTesting.shouldPrintShrinkSteps = false
     }

     afterSpec {
        PropertyTesting.shouldPrintShrinkSteps = true
     }

    "startsWith" {
      var actualAttempts = 0
      forAll(30, Gen.string(), Gen.string()) { a, b ->
        actualAttempts++
        (a + b).startsWith(a)
      }
      actualAttempts shouldBe 30
    }

    "endsWith" {
      var actualAttempts = 0
      forAll(30, Gen.string(), Gen.string()) { a, b ->
        actualAttempts++
        (a + b).endsWith(b)
      }
      actualAttempts shouldBe 30
    }

    "size" {
      var actualAttempts = 0
      forAll { a: String, b: String ->
        actualAttempts++
        (a + b).length == a.length + b.length
      }
      actualAttempts shouldBe 1000
    }

    "explicitGenerators" {
      forAll(Gen.string(), Gen.string(), Gen.string()) { a, b, c ->
        (a + b + c).contains(b)
      }
    }

    "inferredGenerators" {
      forAll { a: String, b: String, c: String, d: String ->
        (a + b + c + d).contains(a)
        (a + b + c + d).contains(b)
        (a + b + c + d).contains(c)
        (a + b + c + d).contains(d)
      }
    }

    "forAll single explicit generator default attempts" {
      var attempts = 0
      forAll(Gen.int()) { a ->
        attempts++
        abs((a / 2)) >= 0
      }
      attempts shouldBe gt(900)
    }

    "forAll single explicit generator 20 attempts" {
      var attempts = 0
      forAll(20, Gen.int()) { a ->
        attempts++
        a * 2 == 2 * a
      }
      attempts shouldBe 20
    }

    "forAll single explicit generator 500 atempts" {
      var attempts = 0
      forAll(500, Gen.positiveIntegers()) { a ->
        attempts++
        if (a == Integer.MAX_VALUE) true
        else a + 2 > a
      }
      attempts shouldBe 500
    }

    "forAll one explicit generator: test fails after second attempt" {
      shouldThrow<AssertionError> {
        forAll(Gen.double()) {
          attempts() < 2
        }
      }
    }

    "forAll one explicit generator: test fails after 300 attempts" {
      shouldThrow<AssertionError> {
        forAll(Gen.string()) {
          attempts() < 300
        }
      }
    }

    "forAll one explicit generator with 0 attempts" {
      val exception = shouldThrow<IllegalArgumentException> {
        forAll(0, Gen.long()) { a ->
          a > a - 1
        }
      }
      exception.message shouldBe "Iterations should be a positive number"
    }

    "forAll one generator default attempts" {
      var attempts = 0
      forAll { a: Int ->
        attempts++
        2 * a % 2 == 0
      }
      attempts shouldBe gte(1000)
    }

    "forAll: one generator explicit 200 attempts" {
      var attempts = 0
      forAll(200) { a: Int ->
        attempts++
        2 * a % 2 == 0
      }
      attempts shouldBe 200
    }

    "forAll: one explicit generator with one value and default attempts" {
      var attempts = 0
      Gen.int().forAll { a ->
        attempts++
        2 * a % 2 == 0
      }
      attempts shouldBe 1000
    }

    "forAll: one explicit generator with two values and default attempts" {
      var attempts = 0
      Gen.int().forAll { a, b ->
        attempts++
        a + b == b + a
      }
      attempts shouldBe 1000
    }

    "forAll: one explicit generator with two values and 100 attempts" {
      var attempts = 0
      Gen.int().forAll(100) { a, b ->
        attempts++
        a + b == b + a
      }
      attempts shouldBe 100
    }

    "forAll: two implicit generators 30 attempts" {
      var attempts = 0
      forAll(25) { a: String, b: String ->
        attempts++
        (a + b).startsWith(a)
      }
      attempts shouldBe 25
    }

    "forAll: Two implicit generators default attempts" {
      var attempts = 0
      forAll { a: Int, b: Int ->
        attempts++
        a * b == b * a
      }
      attempts shouldBe gte(900)
    }

    "pad" {
      forAll { a: Int, b: String ->
        a <= 0 || a > 100 || b.padStart(a, ' ').length >= a
      }
    }

    "double should fail comparing NaN" {
      shouldFail {
        forAll { a: Double, b: Double ->
          a < b || a >= b
        }
      }
    }

    "forAll: Three explicit generators default attempts" {
      var attempts = 0
      forAll(Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
        attempts++
        (a + b) + c == a + (b + c)
      }
      attempts shouldBe 1000
    }

    "forAll: Three explicit generators 30 attempts" {
      // 30 should be ignored as we have many always cases
      var attempts = 0
      forAll(30, Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
        attempts++
        (a * b) * c == a * (b * c)
      }
      attempts shouldBe 30
    }

    "forAll: Three explicit generators failure" {
      shouldThrow<AssertionError> {
        forAll(Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
          a == 0 && b == 1 && c == 2
        }
      }
    }

    "forAll : Three implicit generators 1000 attempts" {
      var attempts = 0
      forAll(1000) { a: Int, b: Int, c: Int ->
        attempts++
        a + b + c == (a + b) + c
      }
      attempts shouldBe 1000
    }

    "forAll: Three implicit generators with default attempts" {
      var attempts = 0
      forAll { a: Int, b: Int, c: Int ->
        attempts++
        a + b * c == (b * c) + a
      }
      attempts shouldBe 1000
    }

    "forAll: Four explicit generators success with default attempts" {
      var attempts = 0
      forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d ->
        attempts++
        a + b + c + d == d + c + b + a
      }
      attempts shouldBe 1000
    }

    "forAll: Four explicit generators success after 50 attempts" {
      var attempts = 0
      forAll(50, Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d ->
        attempts++
        a + b + c + d == d + c + b + a
      }
      attempts shouldBe 81
    }

    "forAll: Four explicit generators failed after 4 attempts" {
      var attempts = 0
      shouldThrow<AssertionError> {
        forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _ ->
          attempts++
          attempts < 4
        }
      }
    }

    "forAll: Four explicit generators failed after 50 attempts" {
      var attempts = 0
      shouldThrow<AssertionError> {
        forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _ ->
          attempts++
          attempts < 50
        }
      }
    }

    "forAll: four implicit generators with default attempts" {
      var attempts = 0
      forAll(1000) { _: Int, _: Int, _: Int, _: Int ->
        attempts++
        true
      }
      attempts shouldBe 1000
    }

    "forAll: four implicit generators with 92 attempts" {
      var attempts = 0
      forAll(92) { _: Int, _: Int, _: Int, _: Int ->
        attempts++
        true
      }
      attempts shouldBe 92
    }

    "forAll: four implicit generators with 250 attempts" {
      var attempts = 0
      forAll(250) { _: Int, _: Int, _: Int, _: Int ->
        attempts++
        true
      }
      attempts shouldBe 250
    }

    "forAll: five explicit generators with 999 attempts" {
      var attempts = 0
      forAll(999, Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _ ->
        attempts++
        true
      }
      attempts shouldBe 999
    }

    "forAll: five explicit generators with default attempts" {
      var attempts = 0
      forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _ ->
        attempts++
        true
      }
      attempts shouldBe 1000
    }

    "forAll: five explicit generators failure" {
      shouldThrow<AssertionError> {
        forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d, e ->
          a == 0 && b == 0 && c == 0 && d == 0 && e == 1
        }
      }
    }

    "forAll five implicit generators with 7000 attempts" {
      var attempts = 0
      forAll(7000) { _: Int, _: Int, _: Int, _: Int, _: Int ->
        attempts++
        true
      }
      attempts shouldBe 7000
    }

    "forAll five implicit generators with default attempts" {
      var attempts = 0
      forAll { _: Int, _: Int, _: Int, _: Int, _: Int ->
        attempts++
        true
      }
      attempts shouldBe 1000
    }

    "forAll five implicit generators with 9999 attempts" {
      var attempts = 0
      forAll(9999) { _: Int, _: Int, _: Int, _: Int, _: Int ->
        attempts++
        true
      }
      attempts shouldBe 9999
    }

    "forAll six explicit arguments with default attempts" {
      var attempts = 0
      forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _, _ ->
        attempts++
        true
      }
      attempts shouldBe 1000
    }

    "forAll six explicit arguments with 50 attempts" {
      var attempts = 0
      forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _, _ ->
        attempts++
        true
      }
      attempts shouldBe 1000
    }

    "forAll six explicit arguments failing at 40 attempts" {
      shouldThrow<AssertionError> {
        forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _, _ ->
          attempts() < 40
        }
      }
    }

    "forAll six explicit arguments failing at 500 attempts" {
      shouldThrow<AssertionError> {
        forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _, _ ->
          attempts() < 500
        }
      }
    }

    "forAll six explicit arguments with 0 attempts" {
      val exception = shouldThrow<IllegalArgumentException> {
        forAll(0, Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _, _ ->
          true
        }
      }
      exception.message shouldBe "Iterations should be a positive number"
    }

    "forAll six explicit arguments with -300 attempts" {
      val exception = shouldThrow<IllegalArgumentException> {
        forAll(-300, Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _, _ ->
          true
        }
      }
      exception.message shouldBe "Iterations should be a positive number"
    }

    "forAll: six implicit arguments 32045 attempts" {
      var attempts = 0
      forAll(32045) { _: Int, _: Double, _: String, _: Long, _: Float, _: Int ->
        attempts++
        true
      }
      attempts shouldBe 32045
    }

    "forAll: six implicit arguments default attempts" {
      var attempts = 0
      forAll { _: Int, _: Double, _: String, _: Long, _: Float, _: Int ->
        attempts++
        true
      }
      attempts shouldBe 3888
    }

    "sets" {
      forAll { a: Set<String>, b: Set<Int>, c: Set<Double>, d: Set<Boolean> ->
        a.plus(b).plus(c).plus(d).size == a.size + b.size + c.size + d.size
      }
    }

    "lists" {
      forAll { a: List<String>, b: List<Int>, c: List<Double>, d: List<Boolean> ->
        a.plus(b).plus(c).plus(d).size == a.size + b.size + c.size + d.size
      }
    }

    "pairs" {
      forAll { (f, s): Pair<String, String> ->
        (f + s).contains(f)
        (f + s).contains(s)
      }
    }

    "maps" {
      forAll { a: Map<Int, String>, b: Map<out Double, Boolean> ->
        a.keys.plus(a.values.toSet()).plus(b.keys).plus(b.values.toSet()).size ==
            a.keys.size + a.values.toSet().size + b.keys.size + b.values.toSet().size
      }
    }

  }
}
