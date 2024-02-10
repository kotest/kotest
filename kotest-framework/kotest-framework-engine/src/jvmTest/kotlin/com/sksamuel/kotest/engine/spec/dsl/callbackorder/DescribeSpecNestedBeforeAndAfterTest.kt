package com.sksamuel.kotest.engine.spec.dsl.callbackorder

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class DescribeSpecNestedBeforeAndAfterTest : DescribeSpec({
   var foo = "initial"
   var counterOne = 0
   var counterTwo = 0
   var counterThree = 0

   describe("before hooks") {
      before {
         foo = "bar"
         counterOne++
      }

      it("foo should be bar") {
         foo shouldBe "bar"
         counterOne shouldBe 1
      }

      it("before should have been called only once") {
         foo shouldBe "bar"
         counterOne shouldBe 1
      }

      it("counterTwo should be 0") {
         counterTwo shouldBe 0
      }

      it("counterThree should be 0") {
         counterThree shouldBe 0
      }

      describe("nested level 1") {
         before {
            foo = "buzz"
            counterTwo++
         }

         it("foo should be buzz") {
            foo shouldBe "buzz"
         }

         it("and counterOne should be 1") {
            counterOne shouldBe 1
         }

         it("and counterTwo should be 1") {
            counterTwo shouldBe 1
         }

         describe("nested level 2") {
            before {
               foo = "jazz"
               counterThree++
            }

            it("foo should be jazz") {
               foo shouldBe "jazz"
            }

            it("and counterOne should be 1") {
               counterOne shouldBe 1
            }

            it("and counterTwo should be 1") {
               counterTwo shouldBe 1
            }

            it("and counterThree should be 1") {
               counterThree shouldBe 1
            }
         }
      }
   }

   describe("after hooks") {
      counterOne = 0

      after {
         counterOne.shouldBe(2)
      }

      describe("nested level 1") {
         after {
            counterOne.shouldBe(1)
            counterOne++
         }

         it("counterOne should be 0") {
            counterOne.shouldBe(0)
         }

         describe("nested level 2") {
            after {
               counterOne.shouldBe(0)
               counterOne++
            }

            it("counterOne should still be 0") {
               counterOne.shouldBe(0)
            }
         }
      }
   }
})
