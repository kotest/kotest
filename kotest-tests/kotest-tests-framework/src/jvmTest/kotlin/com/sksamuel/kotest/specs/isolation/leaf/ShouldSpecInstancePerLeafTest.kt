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

      "a" {
         buffer += "a"
         should("b") {
            buffer += "b"
         }
         "c" {
            buffer += "c"
            should("d") {
               buffer += "d"
            }
            "e" {
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
      "h" {
         buffer += "h"
         "i" {
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
