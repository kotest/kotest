package com.sksamuel.kotlintest.properties

import io.kotlintest.properties.Gen
import io.kotlintest.properties.multiples
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class GenMultipleTest : FunSpec({
   test("multiples of k") {
      Gen.multiples(3, 99999).random().take(100).forEach { it % 3 shouldBe 0 }
   }
})
