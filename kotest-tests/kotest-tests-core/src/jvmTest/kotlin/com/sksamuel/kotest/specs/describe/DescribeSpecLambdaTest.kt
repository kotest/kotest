package com.sksamuel.kotest.specs.describe

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class DescribeSpecLambdaTest : DescribeSpec({

   var name: String? = null

   describe("context 1") {
      it("the name should start off null") {
         name.shouldBe(null)
      }
      name = "foo"
      describe("the name should be foo in this context") {
         name.shouldBe("foo")
         it("should still be foo for this nested test") {
            name.shouldBe("foo")
         }
         name = "boo"
         it("now the name should be boo") {
            name.shouldBe("boo")
         }
      }
      it("it should still be boo as this test should run after all the above") {
         name.shouldBe("boo")
      }
      name = "koo"
      it("now the name should be set to koo") {
         name.shouldBe("koo")
      }
   }

   describe("context 2 should run after context 1") {
      it("name should still be the last value which was koo") {
         name shouldBe "koo"
      }

   }
   describe("Should allow nested describe scope") {
      describe("Nested") {
         it("runs") {

         }

         describe("Runs describe") {
            it("Runs") {

            }
         }
      }
   }
})
