package com.sksamuel.kotest.property

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.property.exhaustive.Exhaustive
import io.kotest.property.exhaustive.andNull
import io.kotest.property.exhaustive.ints

class ExhaustiveTest : FunSpec() {
   init {
      test("andNull should include null in the generated values") {
         Exhaustive.ints(0..100).andNull().values.map { it }.shouldContain(null)
      }
   }
}
