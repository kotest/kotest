package com.sksamuel.kotest.engine.extensions.test

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

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
