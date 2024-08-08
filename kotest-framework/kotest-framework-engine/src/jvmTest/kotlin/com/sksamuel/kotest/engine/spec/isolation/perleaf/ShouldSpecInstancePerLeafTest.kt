package com.sksamuel.kotest.engine.spec.isolation.perleaf

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

private var buffer = ""

class ShouldSpecInstancePerLeafTest : ShouldSpec() {

   override fun isolationMode() = IsolationMode.InstancePerLeaf

   override suspend fun beforeSpec(spec: Spec) {
      buffer += "-init-"
   }

   init {

      afterProject {
         buffer shouldBe "-init-ab-init-acd-init-acef-init-aceg-init-hij-init-hik"
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
