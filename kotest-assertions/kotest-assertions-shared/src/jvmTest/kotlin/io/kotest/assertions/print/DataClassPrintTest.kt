package io.kotest.assertions.print

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DataClassPrintTest : FunSpec() {
   init {

      test("!Detect show for data class") {
         data class Starship(
            val name: String,
            val pennnant: String,
            val displacement: Long,
            val `class`: String,
            val leadShip: Boolean
         )
         Starship("HMS Queen Elizabeth", "R08", 65000, "Queen Elizbeth", true).print() shouldBe """Starship(
- class: Queen Elizbeth
- displacement: 65000
- leadShip: true
- name: HMS Queen Elizabeth
- pennnant: R08
)"""
      }
   }
}
