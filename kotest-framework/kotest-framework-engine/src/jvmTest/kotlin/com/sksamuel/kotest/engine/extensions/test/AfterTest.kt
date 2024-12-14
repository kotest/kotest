package com.sksamuel.kotest.engine.extensions.test

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AfterFunSpecTest : FunSpec() {
   var a = ""

   init {

      context("container1") {

         afterScope {
            a += "1"
         }

         test("a") {}

         test("b") {}

         context("c") {

            test("a") {}

            test("b") {}
         }
      }

      context("container2") {

         afterScope {
            a += "2"
         }

         test("a") {}

         test("b") {}

         context("c") {

            test("a") {}

            test("b") {}
         }
      }

      afterProject {
         a shouldBe "12"
      }
   }
}

class AfterDescribeSpecTest : DescribeSpec() {
   var a = ""

   init {

      describe("container1") {

         afterScope {
            a += "1"
         }

         it("a") {}

         it("b") {}

         describe("c") {

            it("a") {}

            it("b") {}
         }
      }

      describe("container2") {

         afterScope {
            a += "2"
         }

         it("a") {}

         it("b") {}

         describe("c") {

            it("a") {}

            it("b") {}
         }
      }
      afterProject {
         a shouldBe "12"
      }
   }
}
