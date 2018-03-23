package io.kotlintest.properties

import io.kotlintest.matchers.gt
import io.kotlintest.matchers.gte
import io.kotlintest.shouldBe
import io.kotlintest.shouldFail
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec

class PropertyTestingTest : StringSpec() {
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
        forNone(0, Gen.int()) { _ ->
          false
        }
      }
      exception.message shouldBe "Iterations should be a positive number"
    }

    "forNone: one explicit argument with -100 attempts" {
      val exception = shouldThrow<IllegalArgumentException> {
        forNone(-100, Gen.int()) { _ ->
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
      attempts shouldBe 1800
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
      attempts shouldBe Math.pow(5.0, 5.0).toInt()
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
      attempts shouldBe Math.pow(5.0, 6.0).toInt()
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
      attempts shouldBe Math.pow(5.0, 6.0).toInt()
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