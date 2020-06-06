package com.sksamuel.kotest.property.shrinking

import io.kotest.core.spec.style.FunSpec
import io.kotest.property.forAll

class ShrinkingTest : FunSpec() {
   init {
      test("shrinking should show the exception raised by a shrunk value") {
         forAll<String, String> { a, b ->
            (a + b).length == a.length * 2
         }
      }
   }
}
