package io.kotest.property

import io.kotest.core.spec.style.FunSpec
import io.kotest.property.exhaustive.int
import kotlin.random.Random

class GenFunctionsTest : FunSpec() {
   init {
      test("orNull should include null in the generated values") {
         //Exhaustive.int(0..100).orNull().generate(Random.Default).toList().map { it.value }.shouldContain(null)
      }
   }
}
