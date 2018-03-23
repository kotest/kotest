package io.kotlintest.properties

import io.kotlintest.matchers.gt
import io.kotlintest.matchers.gte
import io.kotlintest.runner.junit5.specs.StringSpec
import io.kotlintest.shouldBe
import io.kotlintest.shouldFail
import io.kotlintest.shouldThrow

class PropertyForAllTest : StringSpec() {
  init {

    "startsWith" {
      var actualAttempts = 0
      forAll(30, Gen.string(), Gen.string(), { a, b ->
        actualAttempts++
        (a + b).startsWith(a)
      })
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

    "failure after 4 attempts" {
      var element1 = ""
      var element2 = ""
      val exception = shouldThrow<AssertionError> {
        var attempts = 0
        forAll(Gen.string(), Gen.string()) { a, b ->
          element1 = a
          element2 = b
          attempts++
          attempts < 4
        }
      }
      exception.message shouldBe "Property failed for\n$element1\n$element2\nafter 4 attempts"
    }

    "failure after 50 attempts" {
      var element1 = ""
      var element2 = ""
      val exception = shouldThrow<AssertionError> {
        var attempts = 0
        forAll(Gen.string(), Gen.string()) { a, b ->
          element1 = a
          element2 = b
          attempts++
          attempts < 50
        }
      }
      exception.message shouldBe "Property failed for\n$element1\n$element2\nafter 50 attempts"
    }

    "explicitGenerators" {
      forAll(Gen.string(), Gen.string(), Gen.string(), { a, b, c ->
        (a + b + c).contains(b)
      })
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
        Math.abs((a / 2)) >= 0
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
      var attempts = 0
      var element = 0.0
      val exception = shouldThrow<AssertionError> {
        forAll(Gen.double()) { a ->
          attempts++
          element = a
          attempts < 2
        }
      }
      exception.message shouldBe "Property failed for\n$element\nafter 2 attempts"
    }

    "forAll one explicit generator: test fails after 300 attempts" {
      var attempts = 0
      var element = ""
      val exception = shouldThrow<AssertionError> {
        forAll(Gen.string()) { s ->
          element = s
          attempts++
          attempts < 300
        }
      }

      exception.message shouldBe "Property failed for\n$element\nafter 300 attempts"
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
      forAll(200, { a: Int ->
        attempts++
        2 * a % 2 == 0
      })
      attempts shouldBe 200
    }

    "forAll: two implicit generators 30 attempts" {
      var attempts = 0
      forAll(25, { a: String, b: String ->
        attempts++
        (a + b).startsWith(a)
      })
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
      attempts shouldBe 125
    }

    "forAll: Three explicit generators failure at the third attempt" {
      var attempts = 0
      var elementA = 0
      var elementB = 0
      var elementC = 0
      val exception = shouldThrow<AssertionError> {
        forAll(Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
          elementA = a
          elementB = b
          elementC = c
          attempts++
          attempts < 3
        }
      }

      exception.message shouldBe "Property failed for\n$elementA\n$elementB\n$elementC\nafter 3 attempts"
    }

    "forAll : Three explicit generators failure at the 26th attempt" {
      var attempts = 0
      var elementA = 0
      var elementB = 0
      var elementC = 0
      val exception = shouldThrow<AssertionError> {
        forAll(Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
          elementA = a
          elementB = b
          elementC = c
          attempts++
          attempts < 26
        }
      }

      exception.message shouldBe "Property failed for\n$elementA\n$elementB\n$elementC\nafter 26 attempts"
    }

    "forAll : Three implicit generators 1000 attempts" {
      var attempts = 0
      forAll(1000, { a: Int, b: Int, c: Int ->
        attempts++
        a + b + c == (a + b) + c
      })
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
      attempts shouldBe 625
    }

    "forAll: Four explicit generators failed after 4 attempts" {
      var attempts = 0
      var elementA = 0
      var elementB = 0
      var elementC = 0
      var elementD = 0
      val exception = shouldThrow<AssertionError> {
        forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d ->
          attempts++
          elementA = a
          elementB = b
          elementC = c
          elementD = d
          attempts < 4
        }
      }
      exception.message shouldBe "Property failed for\n$elementA\n$elementB\n$elementC\n$elementD\nafter 4 attempts"
    }

    "forAll: Four explicit generators failed after 50 attempts" {
      var attempts = 0
      var elementA = 0
      var elementB = 0
      var elementC = 0
      var elementD = 0
      val exception = shouldThrow<AssertionError> {
        forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d ->
          elementA = a
          elementB = b
          elementC = c
          elementD = d
          attempts++
          attempts < 50
        }
      }
      exception.message shouldBe "Property failed for\n$elementA\n$elementB\n$elementC\n$elementD\nafter 50 attempts"
    }

    "forAll: four implicit generators with default attempts" {
      var attempts = 0
      forAll(1000) { _: Int, _: Int, _: Int, _: Int ->
        attempts++
        true
      }
      attempts shouldBe 1000
    }

    "forAll: four implicit generators with 20 attempts" {
      var attempts = 0
      forAll(20) { _: Int, _: Int, _: Int, _: Int ->
        attempts++
        true
      }
      attempts shouldBe 625
    }

    "forAll: four implicit generators with 250 attempts" {
      var attempts = 0
      forAll(250, { _: Int, _: Int, _: Int, _: Int ->
        attempts++
        true
      })
      attempts shouldBe 625
    }

    "forAll: five explicit generators with 999 attempts" {
      var attempts = 0
      forAll(999, Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _ ->
        attempts++
        true
      }
      attempts shouldBe 3125
    }

    "forAll: five explicit generators with default attempts" {
      var attempts = 0
      forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _ ->
        attempts++
        true
      }
      attempts shouldBe 3125
    }

    "forAll: five explicit generators failed after 10 attempts" {
      var attempts = 0
      var elementA = 0
      var elementB = 0
      var elementC = 0
      var elementD = 0
      var elementE = 0

      val exception = shouldThrow<AssertionError> {
        forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d, e ->
          elementA = a
          elementB = b
          elementC = c
          elementD = d
          elementE = e
          attempts++
          attempts < 10
        }
      }

      exception.message shouldBe "Property failed for\n$elementA\n$elementB\n$elementC\n$elementD\n$elementE\nafter $attempts attempts"
    }

    "forAll: five explicit generators failed after 50 attempts" {
      var attempts = 0
      var elementA = 0
      var elementB = 0
      var elementC = 0
      var elementD = 0
      var elementE = 0

      val exception = shouldThrow<AssertionError> {
        forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d, e ->
          elementA = a
          elementB = b
          elementC = c
          elementD = d
          elementE = e
          attempts++
          attempts < 50
        }
      }

      exception.message shouldBe "Property failed for\n$elementA\n$elementB\n$elementC\n$elementD\n$elementE\nafter $attempts attempts"
    }

    "forAll five implicit generators with 7000 attempts" {
      var attempts = 0
      forAll(7000, { _: Int, _: Int, _: Int, _: Int, _: Int ->
        attempts++
        true
      })
      attempts shouldBe 7000
    }

    "forAll five implicit generators with default attempts" {
      var attempts = 0
      forAll { _: Int, _: Int, _: Int, _: Int, _: Int ->
        attempts++
        true
      }
      attempts shouldBe 3125
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
      attempts shouldBe 15625
    }

    "forAll six explicit arguments with 50 attempts" {
      var attempts = 0
      forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _, _ ->
        attempts++
        true
      }
      attempts shouldBe 15625
    }

    "forAll six explicit arguments failing at 40 attempts" {
      var attempts = 0
      var elementA = 0
      var elementB = 0
      var elementC = 0
      var elementD = 0
      var elementE = 0
      var elementF = 0
      val exception = shouldThrow<AssertionError> {
        forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d, e, f ->
          elementA = a
          elementB = b
          elementC = c
          elementD = d
          elementE = e
          elementF = f
          attempts++
          attempts < 40
        }
      }
      exception.message shouldBe
          "Property failed for\n$elementA\n$elementB\n$elementC\n$elementD\n$elementE\n$elementF\nafter 40 attempts"
    }

    "forAll six explicit arguments failing at 500 attempts" {
      var attempts = 0
      var elementA = 0
      var elementB = 0
      var elementC = 0
      var elementD = 0
      var elementE = 0
      var elementF = 0
      val exception = shouldThrow<AssertionError> {
        forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d, e, f ->
          elementA = a
          elementB = b
          elementC = c
          elementD = d
          elementE = e
          elementF = f
          attempts++
          attempts < 500
        }
      }
      exception.message shouldBe
          "Property failed for\n$elementA\n$elementB\n$elementC\n$elementD\n$elementE\n$elementF\nafter 500 attempts"
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

    "forAll: six implicit arguments 30000 attempts" {
      var attempts = 0
      forAll(30000) { _: Int, _: Double, _: String, _: Long, _: Float, _: Int ->
        attempts++
        true
      }
      attempts shouldBe 30000
    }

    "forAll: six implicit arguments 24567 attempts" {
      var attempts = 0
      forAll(24567) { _: Int, _: Double, _: String, _: Long, _: Float, _: Int ->
        attempts++
        true
      }
      attempts shouldBe 24567
    }

    "forAll: six implicit arguments default attempts" {
      var attempts = 0
      forAll { _: Int, _: Double, _: String, _: Long, _: Float, _: Int ->
        attempts++
        true
      }
      attempts shouldBe 24000
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