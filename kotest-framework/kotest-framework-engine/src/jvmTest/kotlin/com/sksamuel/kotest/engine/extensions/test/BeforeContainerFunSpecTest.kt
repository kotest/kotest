package com.sksamuel.kotest.engine.extensions.test

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxCondition::class)
class BeforeContainerFunSpecTest : FunSpec() {
   var a = ""

   init {

      beforeContainer {
         a += "1"
      }

      context("container1") {
         test("a") {}
      }

      context("container2") {
         test("a") {}
      }

      afterProject {
         a shouldBe "11"
      }
   }
}

