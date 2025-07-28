package com.sksamuel.kotest.assertions.print

import io.kotest.assertions.print.NullPrint
import io.kotest.assertions.print.Printed
import io.kotest.assertions.print.print
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NullsPrintTest : FunSpec() {
   init {
      test("Detect show for null") {
         null.print().value shouldBe "<null>"
      }
      test("NullPrint should output properly") {
         NullPrint.print(null) shouldBe Printed("<null>")
      }
   }
}
