package com.sksamuel.kotest.config.classname

import io.kotest.core.annotation.Description
import io.kotest.core.spec.style.FunSpec

@Description("Testing that the project config is only loaded once")
class ProjectConfigSingleInstance1Test : FunSpec() {
   init {
      test("foo") {
      }
   }
}

@Description("Testing that the project config is only loaded once")
class ProjectConfigSingleInstance2Test : FunSpec() {
   init {
      test("foo") {
      }
   }
}
