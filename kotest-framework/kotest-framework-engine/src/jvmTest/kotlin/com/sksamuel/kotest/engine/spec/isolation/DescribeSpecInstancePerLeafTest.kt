package com.sksamuel.kotest.engine.spec.isolation

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

private var counter = 0 // to confirm afterSpec hook in InstancePerLeafTest4

class InstancePerLeafTest4 : DescribeSpec({

   isolationMode = IsolationMode.InstancePerLeaf

   beforeSpec {
      trace = ""
      counter++
   }

   afterSpec {
      when (counter) {
         1 -> trace shouldBe "d1_c1_i1_"
         2 -> trace shouldBe "d1_c2_i2_"
         else -> error("Should have run 2 tests")
      }
   }

   describe("d1") {
      trace += "d1_"

      context("c1") {
         trace += "c1_"

         it("i1") {
            trace += "i1_"
         }
      }

      context("c2") {
         trace += "c2_"

         it("i2") {
            trace += "i2_"
         }
      }
   }
})

class InstancePerLeafTest5 : DescribeSpec({

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

         context("c2") {
            trace += "c2_"

            it("i2") {
               trace += "i2_"
               trace shouldBe "d1_c1_c2_i2_"
            }
         }
      }
   }
})

class DescribeSpecInstancePerLeaf6 : DescribeSpec() {
   init {
      isolationMode = IsolationMode.InstancePerLeaf
      describe("tests") {
         describe("for") {
            it("should pass") {
               true shouldBe true
            }
            it("should pass too") {
               true shouldBe true
            }
         }
      }
   }
}

private var counterForTest7 = 0

class InstancePerLeafTest7 : DescribeSpec({

   isolationMode = IsolationMode.InstancePerLeaf

   counterForTest7++

   describe("d1") {
      context("c1") {
         it("i1") {
            counterForTest7 shouldBe 1
         }
      }

      context("c2") {
         it("i2") {
            counterForTest7 shouldBe 2
         }
      }
   }
})
