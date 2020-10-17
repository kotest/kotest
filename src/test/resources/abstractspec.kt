package io.kotest.plugin

import io.kotest.core.spec.style.FunSpec

abstract class MyParentSpec : FunSpec()

class FunSpecExampleTest : MyParentSpec() {
   init {
      test("foo") {

      }
   }
}
