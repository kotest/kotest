package io.kotlintest.properties

import io.kotlintest.specs.StringSpec

class PropertyTestingTest : StringSpec() {
  init {

    "startsWith" {
      forAll(Gen.string(), Gen.string(), { a, b ->
        (a + b).startsWith(a)
      })
    }

    "size" {
      forAll { a: String, b: String ->
        (a + b).length == a.length + b.length
      }
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
  }
}