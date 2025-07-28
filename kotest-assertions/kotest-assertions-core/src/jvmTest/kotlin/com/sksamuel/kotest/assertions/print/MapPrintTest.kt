package com.sksamuel.kotest.assertions.print

import io.kotest.assertions.print.MapPrint
import io.kotest.assertions.print.Printed
import io.kotest.assertions.print.print
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MapPrintTest : FunSpec() {
   init {

      test("MapPrint should handle maps") {
         MapPrint.print(mapOf("foo" to "a", "bar" to 33L), 0) shouldBe Printed("""[("foo", "a"), ("bar", 33L)]""")
      }

      test("detect should handle maps") {
         mapOf("foo" to 'c', "bar" to true).print() shouldBe Printed("""[("foo", 'c'), ("bar", true)]""")
      }
   }
}
