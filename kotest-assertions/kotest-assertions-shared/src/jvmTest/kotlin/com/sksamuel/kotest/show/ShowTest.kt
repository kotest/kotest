package com.sksamuel.kotest.show

import io.kotest.assertions.show.Printed
import io.kotest.assertions.show.Show
import io.kotest.assertions.show.printed
import io.kotest.assertions.show.show
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.File
import java.nio.file.Paths

class ShowTest : FunSpec() {
   init {

      test("Detect show for null") {
         null.show().value shouldBe "<null>"
      }

      test("Detect show for string") {
         "my string".show().value shouldBe "\"my string\""
         "".show().value shouldBe "<empty string>"
         "      ".show().value shouldBe "\"\\s\\s\\s\\s\\s\\s\""
      }

      test("detect show for char") {
         'a'.show().value shouldBe "'a'"
      }

      test("detect show for float") {
         14.3F.show().value shouldBe "14.3f"
      }

      test("detect show for long") {
         14L.show().value shouldBe "14L"
      }

      test("Detect show for any") {
         13.show().value shouldBe "13"
         true.show().value shouldBe "true"
         File("/a/b/c").show().value shouldBe "/a/b/c"
         Paths.get("/a/b/c").show().value shouldBe "/a/b/c"
      }

      test("detect show for boolean") {
         true.show().value shouldBe "true"
         false.show().value shouldBe "false"
      }

      test("detect show for Array<String>") {
         arrayOf("asd", "gsd", "fjfh").show().value shouldBe """["asd", "gsd", "fjfh"]"""
      }

      test("detect show for BooleanArray") {
         booleanArrayOf(true, false, true).show().value shouldBe "[true, false, true]"
      }

      test("detect show for char array") {
         charArrayOf('a', 'g').show().value shouldBe "['a', 'g']"
      }

      test("detect show for IntArray") {
         intArrayOf(2, 4).show().value shouldBe "[2, 4]"
      }

      test("detect show for DoubleArray") {
         doubleArrayOf(1.2, 3.4).show().value shouldBe "[1.2, 3.4]"
      }

      test("!Detect show for data class") {
         data class Starship(
            val name: String,
            val pennnant: String,
            val displacement: Long,
            val `class`: String,
            val leadShip: Boolean
         )
         Starship("HMS Queen Elizabeth", "R08", 65000, "Queen Elizbeth", true).show() shouldBe """Starship(
- class: Queen Elizbeth
- displacement: 65000
- leadShip: true
- name: HMS Queen Elizabeth
- pennnant: R08
)"""
      }

      test("collection show should limit items") {
         List(1000) { it }.show().value shouldBe "[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, ...] and 980 more"
      }
   }
}

data class WibbleWobble(val a: String, val b: Int)

class WibbleWobbleShow : Show<WibbleWobble> {
   override fun show(a: WibbleWobble): Printed = "wibble ${a.a} wobble ${a.b}".printed()
}
