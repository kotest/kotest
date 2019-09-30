package com.sksamuel.kotest.show

import io.kotest.assertions.show.Show
import io.kotest.assertions.show.show
import io.kotest.shouldBe
import io.kotest.specs.FunSpec

class ShowTest : FunSpec() {
  init {
    test("Detect show for null") {
      null.show() shouldBe "<null>"
    }
    test("Detect show for string") {
      "my string".show() shouldBe "my string"
      "".show() shouldBe "<empty string>"
      "      ".show() shouldBe "\\s\\s\\s\\s\\s\\s"
    }
    test("Detect show for any") {
      13.show() shouldBe "13"
      true.show() shouldBe "true"
    }
    test("!Detect show for data class") {
      data class Starship(val name: String,
                          val pennnant: String,
                          val displacement: Long,
                          val `class`: String,
                          val leadShip: Boolean)
      Starship("HMS Queen Elizabeth", "R08", 65000, "Queen Elizbeth", true).show() shouldBe """Starship(
- class: Queen Elizbeth
- displacement: 65000
- leadShip: true
- name: HMS Queen Elizabeth
- pennnant: R08
)"""
    }
  }
}

data class WibbleWobble(val a: String, val b: Int)

class WibbleWobbleShow : Show<WibbleWobble> {
  override fun show(a: WibbleWobble): String = "wibble ${a.a} wobble ${a.b}"
}
