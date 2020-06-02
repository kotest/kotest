package com.sksamuel.kotest.specs.isolation.leaf

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

private var buffer = ""

class ShouldSpecInstancePerLeafTest : ShouldSpec() {

   override fun isolationMode() = IsolationMode.InstancePerLeaf

   init {

      afterProject {
         buffer shouldBe "abacdacefaceghijhik"
      }

      context("a") {
         buffer += "a"
         should("b") {
            buffer += "b"
         }
         context("c") {
            buffer += "c"
            should("d") {
               buffer += "d"
            }
            context("e") {
               buffer += "e"
               should("f") {
                  buffer += "f"
               }
               should("g") {
                  buffer += "g"
               }
            }
         }
      }
      context("h") {
         buffer += "h"
         context("i") {
            buffer += "i"
            should("j") {
               buffer += "j"
            }
            should("k") {
               buffer += "k"
            }
         }
      }
   }
}
