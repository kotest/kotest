package io.kotest.assertions.print

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.File
import java.nio.file.Paths

private val sep = File.separator

class PrimitivePrintsTest : FunSpec() {
   init {

      test("Detect show for string") {
         "my string".print().value shouldBe "\"my string\""
         "".print().value shouldBe "<empty string>"
         "      ".print().value shouldBe "\"\\s\\s\\s\\s\\s\\s\""
      }

      test("detect show for char") {
         'a'.print().value shouldBe "'a'"
      }

      test("detect show for float") {
         14.3F.print().value shouldBe "14.3f"
      }

      test("detect show for long") {
         14L.print().value shouldBe "14L"
      }

      test("Detect show for any") {
         13.print().value shouldBe "13"
         true.print().value shouldBe "true"
         File("/a/b/c").print().value shouldBe "${sep}a${sep}b${sep}c"
         Paths.get("/a/b/c").print().value shouldBe "${sep}a${sep}b${sep}c"
      }

      test("detect show for boolean") {
         true.print().value shouldBe "true"
         false.print().value shouldBe "false"
      }

      test("BooleanPrint.print") {
         BooleanPrint.print(true, 0).value shouldBe "true"
         BooleanPrint.print(false, 0).value shouldBe "false"
      }

      test("CharPrint.char") {
         CharPrint.print('a', 0).value shouldBe "'a'"
         CharPrint.print('w', 0).value shouldBe "'w'"
      }

      test("detect show for BooleanArray") {
         booleanArrayOf(true, false, true).print().value shouldBe "[true, false, true]"
      }

      test("detect show for char array") {
         charArrayOf('a', 'g').print().value shouldBe "['a', 'g']"
      }

      test("detect show for unsigned integer types") {
         42.toUByte().print().value shouldBe "42 (UByte)"
         42.toUShort().print().value shouldBe "42 (UShort)"
         42.toUInt().print().value shouldBe "42 (UInt)"
         42.toULong().print().value shouldBe "42 (ULong)"
      }

   }
}
