package com.sksamuel.kotest.engine.spec.execmode

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

private var trace = ""

class InstancePerLeafTest : DescribeSpec({

   isolationMode = IsolationMode.InstancePerLeaf

   beforeSpec {
      trace = ""
   }

   describe("d1") {
      trace += "d1_"

      context("c1") {
         trace += "c1_"

         it("i1") {
            trace += "i1_"
            trace shouldBe "d1_c1_i1_"
         }
      }

      context("c2") {
         trace += "c2_"

         it("i2") {
            trace += "i2_"
            trace shouldBe "d1_c2_i2_"
         }
      }
   }
})

class InstancePerLeafTest2 : DescribeSpec({

   isolationMode = IsolationMode.InstancePerLeaf

   beforeSpec {
      trace = ""
   }

   describe("d1") {
      trace += "d1_"

      context("c1") {
         trace += "c1_"

         it("i1") {
            trace += "i1_"
            trace shouldBe "d1_c1_i1_"
         }

         it("i2") {
            trace += "i2_"
            trace shouldBe "d1_c1_i2_"
         }
      }

      context("c2") {
         trace += "c2_"

         it("i3") {
            trace += "i3_"
            trace shouldBe "d1_c2_i3_"
         }
      }
   }
})

class InstancePerLeafTest3 : DescribeSpec({

   isolationMode = IsolationMode.InstancePerLeaf

   beforeSpec {
      trace = ""
   }

   describe("d1") {
      trace += "d1_"

      context("c1") {
         trace += "c1_"

         it("i1") {
            trace += "i1_"
            trace shouldBe "d1_c1_i1_"
         }
      }
   }

   describe("d2") {
      trace += "d2_"

      context("c2") {
         trace += "c2_"

         it("i2") {
            trace += "i2_"
            trace shouldBe "d2_c2_i2_"
         }
      }
   }
})

class InstancePerLeafTest4 : DescribeSpec({

   isolationMode = IsolationMode.InstancePerLeaf

   beforeSpec {
      println("beforeSpec")
   }

   afterSpec {
      println("afterSpec")
   }

   describe("d") {
      println("d")
      context("c1") {
         it("i1") {
            println("a")
         }
      }
      context("c2") {
         it("i2") {
            println("b")
         }
         it("i3") {
            println("b2")
         }
      }
      context("c3") {
         it("i4") {
            println("c")
         }
      }
   }
})
