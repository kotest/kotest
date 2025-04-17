package com.sksamuel.kotest.engine.extensions.test

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class BeforeContainerDescribeSpecTest : DescribeSpec() {
   var a = ""

   init {

      beforeContainer {
         a += "1"
      }

      describe("container1") {
         it("a") {}
      }

      describe("container2") {
         it("a") {}
      }

      afterProject {
         a shouldBe "11"
      }
   }
}
