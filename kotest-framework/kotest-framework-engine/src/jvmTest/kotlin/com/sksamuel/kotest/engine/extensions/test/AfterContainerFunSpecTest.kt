package com.sksamuel.kotest.engine.extensions.test

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxCondition::class)
class AfterContainerFunSpecTest : FunSpec() {
   var a = ""

   init {

      afterContainer {
         a += "2"
      }

      context("container1") {
         test("a") {}
      }

      context("container2") {
         test("a") {}
      }

      afterProject {
         a shouldBe "22"
      }
   }
}

