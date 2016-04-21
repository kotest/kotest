package io.kotlintest.properties

class PropSpecTest : PropSpec() {
  init {

    property("startsWith").forAll(Generator.string(), Generator.string(), { a, b ->
      (a + b).startsWith(a)
    })

    property("size").forAll({ a: String, b: String ->
      (a + b).length == a.length + b.length
    })

    property("explicitGenerators").forAll(Generator.string(), Generator.string(), Generator.string(), { a, b, c ->
      (a + b + c).contains(b)
    })

    property("inferredGenerators").forAll { a: String, b: String, c: String, d: String ->
      (a + b + c + d).contains(a)
      (a + b + c + d).contains(b)
      (a + b + c + d).contains(c)
      (a + b + c + d).contains(d)
    }
  }
}