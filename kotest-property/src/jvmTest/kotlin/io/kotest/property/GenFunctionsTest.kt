package io.kotest.property

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.property.progression.int
import kotlin.random.Random

class GenFunctionsTest : FunSpec() {
   init {
      test("orNull should include null in the generated values") {
         Progression.int(0..100).orNull().generate(Random.Default).toList().map { it.value }.shouldContain(null)
      }
   }
}
