package com.sksamuel.kotest.engine.spec

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class InstancePerLeafIsolationTest : DescribeSpec() {

   override fun isolationMode() = IsolationMode.InstancePerLeaf

   init {
      var i = 0

      describe("test") {
         i += 1

         describe("foo") {
            i += 1

            it("a") {
               i shouldBe 2
            }

            it("b") {
               i shouldBe 2
            }

            describe("THIS TEST NAME IS DUPLICATED") {
               i += 1

               it("a") {
                  i shouldBe 3
               }

               it("b") {
                  i shouldBe 3
               }
            }

            describe("bar") {
               i += 1

               it("a") {
                  i shouldBe 3
               }

               it("b") {
                  i shouldBe 3
               }

               describe("THIS TEST NAME IS DUPLICATED") {
                  i += 1

                  it("a") {
                     i shouldBe 4
                  }

                  it("b") {
                     i shouldBe 4
                  }
               }
            }
         }
      }
   }
}
