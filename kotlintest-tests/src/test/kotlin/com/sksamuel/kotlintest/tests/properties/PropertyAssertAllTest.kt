package com.sksamuel.kotlintest.tests.properties

import io.kotlintest.matchers.endWith
import io.kotlintest.matchers.gt
import io.kotlintest.matchers.gte
import io.kotlintest.matchers.haveLength
import io.kotlintest.matchers.lt
import io.kotlintest.matchers.startWith
import io.kotlintest.properties.Gen
import io.kotlintest.properties.assertAll
import io.kotlintest.properties.forAll
import io.kotlintest.should
import io.kotlintest.specs.StringSpec
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow

class PropertyAssertAllTest : StringSpec({

  "startsWith" {
    var actualAttempts = 0
    assertAll(30, Gen.string(), Gen.string(), { a, b ->
      actualAttempts++
      (a + b) should startWith(a)
    })
    actualAttempts shouldBe 30
  }

  "endsWith" {
    var actualAttempts = 0
    assertAll(30, Gen.string(), Gen.string()) { a, b ->
      actualAttempts++
      (a + b) should endWith(b)
    }
    actualAttempts shouldBe 30
  }

  "size" {
    var actualAttempts = 0
    assertAll { a: String, b: String ->
      actualAttempts++
      (a + b) should haveLength(a.length + b.length)
    }
    actualAttempts shouldBe 1000
  }

  "failure for null strings" {
    val exception = shouldThrow<AssertionError> {
      assertAll(Gen.string(), Gen.string()) { a, b ->
        a shouldBe ""
        b shouldBe ""
      }
    }
    exception.message shouldBe "Property failed for\n0: <empty string>\n1: \\n <whitespace only>\nafter 1 attempts"
  }

  "explicitGenerators" {
    assertAll(Gen.string(), Gen.string(), Gen.string(), { a, b, c ->
      (a + b + c).contains(b)
    })
  }

  "inferredGenerators" {
    assertAll { a: String, b: String, c: String, d: String ->
      (a + b + c + d).contains(a)
      (a + b + c + d).contains(b)
      (a + b + c + d).contains(c)
      (a + b + c + d).contains(d)
    }
  }

  "assertAll single explicit generator default attempts" {
    var attempts = 0
    assertAll(Gen.int()) { a ->
      attempts++
      Math.abs((a / 2)) shouldBe gte(0)
    }
    attempts shouldBe gt(900)
  }

  "assertAll single explicit generator 20 attempts" {
    var attempts = 0
    assertAll(20, Gen.int()) { a ->
      attempts++
      a * 2 shouldBe 2 * a
    }
    attempts shouldBe 20
  }

  "assertAll single explicit generator 500 atempts" {
    var attempts = 0
    assertAll(500, Gen.positiveIntegers()) { a ->
      attempts++
      if (a != Integer.MAX_VALUE) a + 2 shouldBe gt(a)
    }
    attempts shouldBe 500
  }

  "assertAll one explicit generator: test fails after second attempt" {
    var attempts = 0
    var element = 0.0
    val exception = shouldThrow<AssertionError> {
      assertAll(Gen.double()) { a ->
        attempts++
        element = a
        attempts shouldBe lt(2)
      }
    }
    exception.message shouldBe "Property failed for\n0: $element\nafter 2 attempts"
  }

  "assertAll one explicit generator: test fails after 300 attempts" {
    var attempts = 0
    val exception = shouldThrow<AssertionError> {
      assertAll(Gen.string()) {
        attempts++
        attempts shouldBe lt(300)
      }
    }

    exception.message shouldBe "Property failed for\n0: <empty string>\nafter 300 attempts"
  }

  "assertAll one explicit generator with 0 attempts" {
    val exception = shouldThrow<IllegalArgumentException> {
      assertAll(0, Gen.long()) { a ->
        a shouldBe gt(a - 1)
      }
    }
    exception.message shouldBe "Iterations should be a positive number"
  }

  "assertAll one generator default attempts" {
    var attempts = 0
    assertAll { a: Int ->
      attempts++
      2 * a % 2 shouldBe 0
    }
    attempts shouldBe gte(1000)
  }

  "assertAll: one generator explicit 200 attempts" {
    var attempts = 0
    assertAll(200, { a: Int ->
      attempts++
      2 * a % 2 shouldBe 0
    })
    attempts shouldBe 200
  }

  "assertAll: two implicit generators 30 attempts" {
    var attempts = 0
    assertAll(25, { a: String, b: String ->
      attempts++
      (a + b) should startWith(a)
    })
    attempts shouldBe 25
  }

  "assertAll: Two implicit generators default attempts" {
    var attempts = 0
    assertAll { a: Int, b: Int ->
      attempts++
      a * b shouldBe b * a
    }
    attempts shouldBe gte(900)
  }

  "pad" {
    assertAll { a: Int, b: String ->
      a <= 0 || a > 100 || b.padStart(a, ' ').length >= a
    }
  }

  "assertAll: Three explicit generators default attempts" {
    var attempts = 0
    assertAll(Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
      attempts++
      (a + b) + c shouldBe a + (b + c)
    }
    attempts shouldBe 1000
  }

  "assertAll: Three explicit generators 30 attempts" {
    // 30 should be ignored as we have many always cases
    var attempts = 0
    assertAll(30, Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
      attempts++
      (a * b) * c shouldBe a * (b * c)
    }
    attempts shouldBe 30
  }

  "assertAll: Three explicit generators failure at the third attempt" {
    var attempts = 0
    val exception = shouldThrow<AssertionError> {
      assertAll(Gen.int(), Gen.int(), Gen.int()) { _, _, _ ->
        attempts++
        attempts shouldBe lt(3)
      }
    }

    exception.message shouldBe "Property failed for\n0: 0\n1: 0\n2: 0\nafter 3 attempts"
  }

  "assertAll : Three explicit generators failure at the 26th attempt" {
    var attempts = 0
    val exception = shouldThrow<AssertionError> {
      assertAll(Gen.int(), Gen.int(), Gen.int()) { _, _, _ ->
        attempts++
        attempts shouldBe lt(26)
      }
    }

    exception.message shouldBe "Property failed for\n0: 0\n1: 0\n2: 0\nafter 26 attempts"
  }

  "assertAll : Three implicit generators 1000 attempts" {
    var attempts = 0
    assertAll(1000, { a: Int, b: Int, c: Int ->
      attempts++
      a + b + c shouldBe (a + b) + c
    })
    attempts shouldBe 1000
  }

  "assertAll: Three implicit generators with default attempts" {
    var attempts = 0
    assertAll { a: Int, b: Int, c: Int ->
      attempts++
      a + b * c shouldBe (b * c) + a
    }
    attempts shouldBe 1000
  }

  "assertAll: Four explicit generators success with default attempts" {
    var attempts = 0
    assertAll(Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d ->
      attempts++
      a + b + c + d shouldBe d + c + b + a
    }
    attempts shouldBe 1000
  }

  "assertAll: Four explicit generators success after 88 attempts" {
    var attempts = 0
    assertAll(88, Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d ->
      attempts++
      a + b + c + d shouldBe d + c + b + a
    }
    attempts shouldBe 88
  }

  "assertAll: Four explicit generators failed after 4 attempts" {
    var attempts = 0
    val exception = shouldThrow<AssertionError> {
      assertAll(Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _ ->
        attempts++
        attempts shouldBe lt(4)
      }
    }
    exception.message shouldBe "Property failed for\n0: 0\n1: 0\n2: 0\n3: 0\nafter 4 attempts"
  }

  "assertAll: Four explicit generators failed after 50 attempts" {
    var attempts = 0
    val exception = shouldThrow<AssertionError> {
      assertAll(Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _ ->
        attempts++
        attempts shouldBe lt(50)
      }
    }
    exception.message shouldBe "Property failed for\n0: 0\n1: 0\n2: 0\n3: 0\nafter 50 attempts"
  }

  "assertAll: four implicit generators with default attempts" {
    var attempts = 0
    assertAll(1000) { _: Int, _: Int, _: Int, _: Int ->
      attempts++
    }
    attempts shouldBe 1000
  }

  "assertAll: four implicit generators with 98 attempts" {
    var attempts = 0
    assertAll(98) { _: Int, _: Int, _: Int, _: Int ->
      attempts++
    }
    attempts shouldBe 98
  }

  "assertAll: four implicit generators with 250 attempts" {
    var attempts = 0
    assertAll(250) { _: Int, _: Int, _: Int, _: Int ->
      attempts++
    }
    attempts shouldBe 250
  }

  "assertAll: five explicit generators with 999 attempts" {
    var attempts = 0
    assertAll(999, Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _ ->
      attempts++
    }
    attempts shouldBe 999
  }

  "assertAll: five explicit generators with default attempts" {
    var attempts = 0
    assertAll(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _ ->
      attempts++
    }
    attempts shouldBe 1000
  }

  "assertAll: five explicit generators failed after 10 attempts" {
    val exception = shouldThrow<AssertionError> {
      assertAll(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _ ->
        attempts() shouldBe lt(10)
      }
    }

    exception.message shouldBe "Property failed for\n0: 0\n1: 0\n2: 0\n3: 0\n4: 0\nafter 10 attempts"
  }

  "assertAll: five explicit generators failed after 50 attempts" {
    val exception = shouldThrow<AssertionError> {
      assertAll(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _ ->
        attempts() shouldBe lt(50)
      }
    }

    exception.message shouldBe "Property failed for\n0: 0\n1: 0\n2: 0\n3: 0\n4: 0\nafter 50 attempts"
  }

  "assertAll five implicit generators with 7000 attempts" {
    var attempts = 0
    assertAll(7000, { _: Int, _: Int, _: Int, _: Int, _: Int ->
      attempts++
    })
    attempts shouldBe 7000
  }

  "assertAll five implicit generators with default attempts" {
    var attempts = 0
    assertAll { _: Int, _: Int, _: Int, _: Int, _: Int ->
      attempts++
    }
    attempts shouldBe 1000
  }

  "assertAll five implicit generators with 9999 attempts" {
    var attempts = 0
    assertAll(9999) { _: Int, _: Int, _: Int, _: Int, _: Int ->
      attempts++
    }
    attempts shouldBe 9999
  }

  "assertAll six explicit arguments with default attempts" {
    var attempts = 0
    assertAll(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _, _ ->
      attempts++
    }
    attempts shouldBe 1000
  }

  "assertAll six explicit arguments with 999 attempts" {
    var attempts = 0
    assertAll(999, Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _, _ ->
      attempts++
    }
    attempts shouldBe 999
  }

  "assertAll six explicit arguments failing at 40 attempts" {
    var attempts = 0
    var elementA = 0
    var elementB = 0
    var elementC = 0
    var elementD = 0
    var elementE = 0
    var elementF = 0
    val exception = shouldThrow<AssertionError> {
      assertAll(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d, e, f ->
        elementA = a
        elementB = b
        elementC = c
        elementD = d
        elementE = e
        elementF = f
        attempts++
        attempts shouldBe lt(40)
      }
    }
    exception.message shouldBe
        "Property failed for\n0: $elementA\n1: $elementB\n2: $elementC\n3: $elementD\n4: $elementE\n5: $elementF\nafter 40 attempts"
  }

  "assertAll six explicit arguments failing at 500 attempts" {
    var attempts = 0
    var elementA = 0
    var elementB = 0
    var elementC = 0
    var elementD = 0
    var elementE = 0
    var elementF = 0
    val exception = shouldThrow<AssertionError> {
      assertAll(Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d, e, f ->
        elementA = a
        elementB = b
        elementC = c
        elementD = d
        elementE = e
        elementF = f
        attempts++
        attempts shouldBe lt(500)
      }
    }
    exception.message shouldBe
        "Property failed for\n0: $elementA\n1: $elementB\n2: $elementC\n3: $elementD\n4: $elementE\n5: $elementF\nafter 500 attempts"
  }

  "assertAll six explicit arguments with 0 attempts" {
    val exception = shouldThrow<IllegalArgumentException> {
      assertAll(0, Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _, _ ->
      }
    }
    exception.message shouldBe "Iterations should be a positive number"
  }

  "assertAll six explicit arguments with -300 attempts" {
    val exception = shouldThrow<IllegalArgumentException> {
      assertAll(-300, Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int(), Gen.int()) { _, _, _, _, _, _ ->
      }
    }
    exception.message shouldBe "Iterations should be a positive number"
  }

  "assertAll: six implicit arguments 30000 attempts" {
    var attempts = 0
    assertAll(30000) { _: Int, _: Double, _: String, _: Long, _: Float, _: Int ->
      attempts++
    }
    attempts shouldBe 30000
  }

  "assertAll: six implicit arguments 24567 attempts" {
    var attempts = 0
    assertAll(24567) { _: Int, _: Double, _: String, _: Long, _: Float, _: Int ->
      attempts++
    }
    attempts shouldBe 24567
  }

  "assertAll: six implicit arguments default attempts" {
    var attempts = 0
    assertAll { _: Int, _: Double, _: String, _: Long, _: Float, _: Int ->
      attempts++
    }
    attempts shouldBe 1350
  }

  "sets" {
    assertAll { a: Set<String>, b: Set<Int>, c: Set<Double>, d: Set<Boolean> ->
      a.plus(b).plus(c).plus(d).size shouldBe a.size + b.size + c.size + d.size
    }
  }

  "lists" {
    assertAll { a: List<String>, b: List<Int>, c: List<Double>, d: List<Boolean> ->
      a.plus(b).plus(c).plus(d).size shouldBe a.size + b.size + c.size + d.size
    }
  }

  "pairs" {
    forAll { (f, s): Pair<String, String> ->
      (f + s).contains(f)
      (f + s).contains(s)
    }
  }

  "maps" {
    assertAll { a: Map<Int, String>, b: Map<out Double, Boolean> ->
      a.keys.plus(a.values.toSet()).plus(b.keys).plus(b.values.toSet()).size shouldBe
          a.keys.size + a.values.toSet().size + b.keys.size + b.values.toSet().size
    }
  }

})