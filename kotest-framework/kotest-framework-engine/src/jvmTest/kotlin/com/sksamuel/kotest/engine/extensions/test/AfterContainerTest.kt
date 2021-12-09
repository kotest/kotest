package com.sksamuel.kotest.engine.extensions.test

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

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
