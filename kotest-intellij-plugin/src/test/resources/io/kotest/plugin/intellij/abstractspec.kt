package io.kotest.plugin.intellij

import io.kotest.core.spec.style.FunSpec

abstract class MyParentSpec : FunSpec()

class FunSpecExampleTest : MyParentSpec() {
   init {
      test("foo") {

      }
   }
}
