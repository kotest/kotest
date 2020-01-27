package com.sksamuel.kotest.specs.funspec

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FunSpecInstancePerTestTest : FunSpec() {

   override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

   init {
      var count = 0
      test("be 0") {
         count shouldBe 0
         count = 100
      }
      test("be 0 part 2") {
         count shouldBe 0
         count = 100
      }
      test("be 0 part 3") {
         count shouldBe 0
         count = 100
      }
      test("still be 0") {
         count shouldBe 0
      }
   }
}
