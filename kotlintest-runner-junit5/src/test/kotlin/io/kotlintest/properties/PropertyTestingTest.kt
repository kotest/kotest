package io.kotlintest.properties

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec

class PropertyTestingTest : StringSpec() {
  init {

    "startsWith" {
      var actualAttempts = 0
      forAll(Gen.string(), Gen.string(), { a, b ->
        actualAttempts++
        (a + b).startsWith(a)
      })
      actualAttempts shouldBe 1000
    }

    "endsWith a hundred times" {
      var actualAttempts = 0
      forAll(Gen.string(), Gen.string()) { a, b ->
        actualAttempts++
        (a + b).endsWith(b)
      }
      actualAttempts shouldBe 100
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
      val exception =
          shouldThrow<AssertionError> {
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
      val exception =
          shouldThrow<AssertionError> {
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

    "forAll single generator explicit 1000 attempts" {
      var attempts = 0
      forAll(Gen.int()) { a ->
        attempts++
        Math.abs(a) > 0
      }

      attempts shouldBe 1000
    }

    "forAll single generator explicit 20 attempts" {
      var attempts = 0
      forAll(Gen.int()) { a ->
        attempts++
        a * 2 == 2 * a
      }
      attempts shouldBe 20
    }

    "forAll single gnerator explicit 500 atempts" {
      var attempts = 0
      forAll(Gen.positiveIntegers()) { a ->
        attempts++
        a + 2 > a
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

    "forALl one explicit generator with 0 attempts" {
      val exception = shouldThrow<IllegalArgumentException> {
        forAll(Gen.long()) { a ->
          a > a - 1
        }
      }
      exception.message shouldBe "Attempts should be a positive number"
    }

    "forAll one generator implicit 10 attempts" {
      var attempts = 0
      forAll { a: Int ->
        attempts++
        2 * a % 2 == 0
      }
      attempts shouldBe 10
    }

    "forAll: one generator implicit 200 attempts" {
      var attempts = 0
      forAll{ a: Int ->
        attempts++
        2 * a % 2 == 0
      }
      attempts shouldBe 200
    }

    "forAll: two generators implicit 30 attempts" {
      var attempts = 0
      forAll { a: String, b: String ->
        attempts++
        (a + b).startsWith(a)
      }
      attempts shouldBe 30
    }

    "forAll: Two implicit generators 500 attempts" {
      var attempts = 0
      forAll { a: Int, b: Int ->
        attempts++
        a * b == b * a
      }
      attempts shouldBe 500
    }

    "pad" {
      forAll { a: Int, b: String ->
        a <= 0 || a > 100 || b.padStart(a, ' ').length >= a
      }
    }

    "double" {
      forAll { a: Double, b: Double ->
        a < b || a >= b
      }
    }

    "forAll: Three explicit generators 1000 attempts" {
      var attempts = 0
      forAll(Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
        attempts++
        (a + b) + c == a + (b + c)
      }
      attempts shouldBe 1000
    }

    "forAll: Three explicit generators 30 attempts" {
      var attempts = 0
      forAll(Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
        attempts++
        (a * b) * c == a * (b * c)
      }
      attempts shouldBe 30
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
      forAll { a: Int, b: Int, c: Int ->
        attempts++
        a + b + c == (a + b) + c
      }
      attempts shouldBe 1000
    }

    "forAll: Three implicit generators 26 attempts" {
      var attempts = 0
      forAll { a: Int, b: Int, c: Int ->
        attempts++
        a + b * c == (b * c) + a
      }
      attempts shouldBe 26
    }

    "forAll: Four explicit generators success after 1000 attempts" {
      var attempts = 0
      forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d ->
        attempts++
        a + b + c + d == d + c + b + a
      }
      attempts shouldBe 1000
    }

    "forAll: Four explicit generators success after 50 attempts" {
      var attempts = 0
      forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d ->
        attempts++
        a + b + c + d == d + c + b + a
      }
      attempts shouldBe 50
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
      exception.message shouldBe "Property failed for \n$elementA\n$elementB\n$elementC\n$elementD\nafter $attempts attempts"
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
      exception.message shouldBe "Property failed for \n$elementA\n$elementB\n$elementC\n$elementD\nafter $attempts attempts"
    }

    "forAll: four explicit generators with 0 attempts" {
      shouldThrow<IllegalArgumentException> {
        forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _ ->
          true
        }
      }
    }

    "forAll: four implicit generators with 1000 attempts" {
      var attempts = 0
      forAll { _: Int, _: Int, _: Int, _: Int ->
        attempts++
        true
      }
      attempts shouldBe 1000
    }

    "forAll: four implicit generators with 20 attempts" {
      var attempts = 0
      forAll { _: Int, _: Int, _: Int, _: Int ->
        attempts++
        true
      }
      attempts shouldBe 20
    }

    "forAll: four implicit generators with 250 attempts" {
      var attempts = 0
      forAll { _: Int, _: Int, _: Int, _: Int ->
        attempts++
        true
      }
      attempts shouldBe 250
    }

    "forAll: five explicit generators with 1000 attempts" {
      var attempts = 0
      forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _ ->
        attempts++
        true
      }
      attempts shouldBe 1000
    }

    "forAll: five explicit generators with 200 attempts" {
      var attempts = 0
      forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _ ->
        attempts++
        true
      }
      attempts shouldBe 200
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

      exception.message shouldBe "Property failed for \n$elementA\n$elementB\n$elementC\n$elementD\n$elementE\nafter $attempts attempts"
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

      exception.message shouldBe "Property failed for \n$elementA\n$elementB\n$elementC\n$elementD\n$elementE\nafter $attempts attempts"
    }

    "forAll: five explicit generators with 0 attempts" {
      val exception = shouldThrow<IllegalArgumentException> {
        forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _ ->
          true
        }
      }
      exception.message shouldBe "Attempts should be a positive number"
    }

    "forAll five implicit generators with 1000 attempts" {
      var attempts = 0
      forAll { _: Int, _: Int, _: Int, _: Int, _: Int ->
        attempts++
        true
      }
      attempts shouldBe 1000
    }

    "forAll five implicit generators with 20 attempts" {
      var attempts = 0
      forAll { _: Int, _: Int, _: Int, _: Int, _: Int ->
        attempts++
        true
      }
      attempts shouldBe 20
    }

    "forAll five implicit generators with 500 attempts" {
      var attempts = 0
      forAll { _: Int, _: Int, _: Int, _: Int, _: Int ->
        attempts++
        true
      }
      attempts shouldBe 500
    }

    "forAll six explicit arguments with 1000 attempts" {
      var attempts = 0
      forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _, _ ->
        attempts++
        true
      }
      attempts shouldBe 1000
    }

    "forAll six explicit arguments with 50 attempts" {
      var attempts = 0
      forAll( Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _, _ ->
        attempts++
        true
      }
      attempts shouldBe 50
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
        forAll( Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d, e, f ->
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
          "Property failed for \n$elementA\n$elementB\n$elementC\n$elementD\n$elementE\n$elementF\nafter $attempts attempts"
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
        forAll( Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d, e, f ->
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
          "Property failed for \n$elementA\n$elementB\n$elementC\n$elementD\n$elementE\n$elementF\nafter $attempts attempts"
    }

    "forAll six explicit arguments with 0 attempts" {
      val exception = shouldThrow<IllegalArgumentException> {
        forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _, _ ->
          true
        }
      }
      exception.message shouldBe "Attempts should be a positive number"
    }

    "forAll six explicit arguments with -300 attempts" {
      val exception = shouldThrow<IllegalArgumentException> {
        forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _, _ ->
          true
        }
      }
      exception.message shouldBe "Attempts should be a positive number"
    }

    "forAll: six implicit arguments 1000 attempts" {
      var attempts = 0
      forAll { _: Int, _: Double, _: String, _: Long, _: Float, _: Int ->
        attempts++
        true
      }
      attempts shouldBe 1000
    }

    "forAll: six implicit arguments 500 attempts" {
      var attempts = 0
      forAll { _: Int, _: Double, _: String, _: Long, _: Float, _: Int ->
        attempts++
        true
      }
      attempts shouldBe 500
    }

    "forAll: six implicit arguments 20 attempts" {
      var attempts = 0
      forAll { _: Int, _: Double, _: String, _: Long, _: Float, _: Int ->
        attempts++
        true
      }
      attempts shouldBe 20
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
      forNone(Gen.string()) { a ->
        attempts++
        (a + "hi").endsWith("Bye")
      }
      attempts shouldBe 1000
    }

    "forNone: one explicit argument with 300 attempts" {
      var attempts = 0
      forNone(Gen.string()) { a ->
        attempts++
        (a + "hi").endsWith("Bye")
      }
      attempts shouldBe 300
    }

    "forNone: one explicit argument fails after 10 attempts" {
      var attempts = 0
      var elementA = ""
      val exception = shouldThrow<AssertionError> {
        forNone(Gen.string()) { a ->
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
        forNone(Gen.int()) { a ->
          elementA = a
          attempts++
          attempts >= 50
        }
      }
      exception.message shouldBe "Property passed for\n$elementA\nafter 50 attempts"
    }

    "forNone: one explicit argument with 0 attempts" {
      val exception = shouldThrow<IllegalArgumentException> {
        forNone(Gen.int()) { _ ->
          false
        }
      }
      exception.message shouldBe "Attempts should be a positive number"
    }

    "forNone: one explicit argument with -100 attempts" {
      val exception = shouldThrow<IllegalArgumentException> {
        forNone(Gen.int()) { _ ->
          false
        }
      }
      exception.message shouldBe "Attempts should be a positive number"
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
      forNone { _: Double ->
        attempts++
        false
      }
      attempts shouldBe 20
    }

    "forNone: one implicit argument with 100 attempts" {
      var attempts = 0
      forNone { _: Double ->
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
      forNone( Gen.int(), Gen.int()) { a, b ->
        attempts++
        a * b != b * a
      }
      attempts shouldBe 360
    }

    "forNone: two explicit arguments 1492 attempts" {
      var attempts = 0
      forNone( Gen.int(), Gen.int()) { a, b ->
        attempts++
        a * b == a + b
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

    "forNone: two implicit generators 1000 attempts" {
      var attempts = 0
      forNone { a: Int, b: String ->
        attempts++
        a.toString() == b
      }
      attempts shouldBe 1000
    }

    "forNone: two implicit generators 1066 attempts" {
      var attempts = 0
      forNone { a: Int, b: Int ->
        attempts++
        a == b && b != a
      }
      attempts shouldBe 1066
    }

    "forNone: two implicit generators 300 attempts" {
      var attempts = 0
      forNone { a: String, b: String ->
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
      forNone(Gen.int(), Gen.int(), Gen.int()) { _, _, _ ->
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
        forNone(Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
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
        forNone(Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
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
        forNone(Gen.int(), Gen.int(), Gen.int()) { _, _, _ ->
          false
        }
      }

      exception.message shouldBe "Attempts should be a positive number"
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
      forNone { _: Int, _: Int, _: Int ->
        attempts++
        false
      }
      attempts shouldBe 700
    }

    "forNone: three implicit generators 1200 attempts" {
      var attempts = 0
      forNone { _: Int, _: Int, _: Int ->
        attempts++
        false
      }

      attempts shouldBe 1200
    }

    "forNone: four explicit generators 1000 attempts" {
      var attempts = 0
      forNone(Gen.int(), Gen.string(), Gen.long(), Gen.double()) { _, _, _, _ ->
        attempts++
        false
      }
      attempts shouldBe 1000
    }

    "forNone: four explicit generators 300 attempts" {
      var attempts = 0
      forNone(Gen.int(), Gen.string(), Gen.double(), Gen.long()) { _, _, _, _ ->
        attempts++
        false
      }
      attempts shouldBe 300
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
      exception.message shouldBe "Property passed for \n$elementA\n$elementB\n$elementC\n$elementD\nafter $attempts attempts"
    }

    "forNone: four explicit generators failure at attempt 3245" {
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
          attempts >= 3245
        }
      }
      exception.message shouldBe "Property passed for \n$elementA\n$elementB\n$elementC\n$elementD\nafter $attempts attempts"
    }

    "forNone: four implicit generators 1000 attempts" {
      var attempts = 0
      forNone { _: Int, _: String, _: String, _: Long ->
        attempts++
        false
      }
      attempts shouldBe 1000
    }

    "forNone: four implicit generators 30 attempts" {
      var attempts = 0
      forNone{ _: Long, _: Int, _: Double, _: String ->
        attempts++
        false
      }
      attempts shouldBe 30
    }

    "forNone: four implicit generators 620 attempts" {
      var attempts = 0
      forNone{ _: String, _: Int, _: Double, _: Long ->
        attempts++
        false
      }
      attempts shouldBe 620
    }

    "forNone: five explicit generators 1000 attempts" {
      var attempts = 0
      forNone(Gen.string(), Gen.int(), Gen.long(), Gen.double(), Gen.string()) { _, _, _, _, _ ->
        attempts++
        false
      }
      attempts shouldBe 1000
    }

    "forNone: five explicit generators 26 attempts" {
      var attempts = 0
      forNone(Gen.int(), Gen.string(), Gen.double(), Gen.long(), Gen.int()) { _, _, _, _, _ ->
        attempts++
        false
      }
      attempts shouldBe 26
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
          "Property passed for \n$elementA\n$elementB\n$elementC\n$elementD\n$elementE\nafter $attempts attempts"
    }

    "forNone: five explicit generators fails after 2300 attempts" {
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
          attempts >= 2300
        }
      }

      exception.message shouldBe
          "Property passed for \n$elementA\n$elementB\n$elementC\n$elementD\n$elementE\nafter $attempts attempts"
    }

    "forNone: five explicit generators 0 attempts" {
      val exception = shouldThrow<IllegalArgumentException> {
        forNone(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _ ->
          false
        }
      }

      exception.message shouldBe "Attempts should be a positive number"
    }

    "forNone: five explicit generators -300 attempts" {
      val exception = shouldThrow<IllegalArgumentException> {
        forNone(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _ ->
          false
        }
      }

      exception.message shouldBe "Attempts should be a positive number"
    }

    "forNone: five implicit generators 1000 attempts" {
      var attempts = 0
      forNone { _: Int, _: Int, _: Int, _: Int, _: Int ->
        attempts++
        false
      }
      attempts shouldBe 1000
    }

    "forNone: five implicit generators 427 attempts" {
      var attempts = 0
      forNone { _: Int, _: Int, _: Int, _: Int, _: Int ->
        attempts++
        false
      }
      attempts shouldBe 427
    }

    "forNone: five implicit generators 2380 attempts" {
      var attempts = 0
      forNone { _: Int, _: Int, _: Int, _: Int, _: Int ->
        attempts++
        false
      }
      attempts shouldBe 2380
    }

    "forNone: six explicit generators 1000 attempts" {
      var attempts = 0
      forNone(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _, _ ->
        attempts++
        false
      }
      attempts shouldBe 1000
    }

    "forNone: six explicit generators 300 attempts" {
      var attempts = 0
      forNone(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _, _ ->
        attempts++
        false
      }
      attempts shouldBe 300
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
          "Property passed for \n$elementA\n$elementB\n$elementC\n$elementD\n$elementE\n$elementF\nafter $attempts attempts"
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
          "Property passed for \n$elementA\n$elementB\n$elementC\n$elementD\n$elementE\n$elementF\nafter $attempts attempts"
    }

    "forNone: six implicit generators 1000 attempts" {
      var attempts = 0
      forNone { _: Int, _: Int, _: Int, _: Int, _: Int, _: Int ->
        attempts++
        false
      }
      attempts shouldBe 1000
    }

    "forNone: six implicit generators 26 attempts" {
      var attempts = 0
      forNone { _: Int, _: Int, _: Int, _: Int, _: Int, _: Int ->
        attempts++
        false
      }
      attempts shouldBe 26
    }

    "forNone: six implicit generators 3678 attempts" {
      var attempts = 0
      forNone { _: Int, _: Int, _: Int, _: Int, _: Int, _: Int ->
        attempts++
        false
      }
      attempts shouldBe 3678
    }
  }
}