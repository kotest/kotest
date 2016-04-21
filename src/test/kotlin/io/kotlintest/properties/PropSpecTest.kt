package io.kotlintest.properties

class PropSpecTest : PropSpec() {
  init {

    property("startsWith") {
      forAll(Generator.string(), Generator.string(), { a, b ->
        (a + b).startsWith(a)
      })
    }

    property("size") {
      forAll(Generator.string(), Generator.string(), { a, b ->
        (a + b).length == a.length + b.length
      })
    }

    property("contains") {
      forAll(Generator.string(), Generator.string(), Generator.string(), { a, b, c ->
        (a + b + c).contains(b)
      })
    }
  }
}