package com.sksamuel.kotest.properties

import io.kotest.properties.Gen
import io.kotest.properties.multiples
import io.kotest.shouldBe
import io.kotest.specs.FunSpec

class GenMultipleTest : FunSpec({
   test("multiples of k") {
      Gen.multiples(3, 99999).random().take(100).forEach { it % 3 shouldBe 0 }
   }
})
