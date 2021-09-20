package com.sksamuel.kotest.property

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class CheckAllTest : FunSpec() {
   init {

      test("checkAll should setup a property context") {
         var count = 0
         checkAll(100) {
            val a = Arb.string().value()
            val b = Arb.string().value()
            (a + b).length shouldBe b.length + a.length
            count++
         }
         count shouldBe 100
      }

      test("checkAll should use a repeatable seed") {
         checkAll(1, 52104482139021L) {
            val a = Arb.string().value()
            a shouldBe "cUj%x=.f6ktw\"icenM(AEw&5CP3q+8FxU*xi<p!Jbd"
         }
      }

      test("auto labelling") {
         val context = checkAll(100, 98173L) {
            Arb.string().value()
         }
         context.autoclassifications()["1"] shouldBe mapOf(
            "MAX LENGTH" to 3,
            "ANY LENGTH LETTER OR DIGITS" to 3
         )
      }
   }
}
