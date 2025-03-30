package com.sksamuel.kotest.engine.extensions.test

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class AfterContainerDescribeSpecTest : DescribeSpec() {
   var a = ""

   init {

      afterContainer {
         a += "2"
      }

      describe("container1") {
         it("a") {}
      }

      describe("container2") {
         it("a")
      }

      afterProject {
         a shouldBe "22"
      }
   }
}
