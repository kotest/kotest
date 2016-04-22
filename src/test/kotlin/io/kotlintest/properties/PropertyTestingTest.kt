package io.kotlintest.properties

class PropertyTestingTest : PropertyTesting() {
  init {

    property("startsWith").forAll(Gen.string(), Gen.string(), { a, b ->
      (a + b).startsWith(a)
    })

    property("size").forAll({ a: String, b: String ->
      (a + b).length == a.length + b.length
    })

    property("explicitGenerators").forAll(Gen.string(), Gen.string(), Gen.string(), { a, b, c ->
      (a + b + c).contains(b)
    })

    property("inferredGenerators").forAll { a: String, b: String, c: String, d: String ->
      (a + b + c + d).contains(a)
      (a + b + c + d).contains(b)
      (a + b + c + d).contains(c)
      (a + b + c + d).contains(d)
    }

    property("pad").forAll { a: Int, b: String ->
      a <= 0 || a > 100 || b.padStart(a, ' ').length >= a
    }

    property("double").forAll { a: Double, b: Double ->
      a < b || a >= b
    }

    property("forNoneTestStrings").forNone { a: String, b: String ->
      a + 1 == b + 2
    }

    property("forNoneTestBooleanDouble").forNone { a: Boolean, b: Double ->
      a.toString() == b.toString()
    }
  }
}