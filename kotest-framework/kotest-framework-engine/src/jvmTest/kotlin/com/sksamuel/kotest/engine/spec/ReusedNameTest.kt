package com.sksamuel.kotest.engine.spec

import io.kotest.core.annotation.Description
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec

@Description("Checks that a context name can be re-used in another, non competing scope")
@EnabledIf(LinuxCondition::class)
class ReusedNameTest : DescribeSpec({

   isolationMode = IsolationMode.InstancePerLeaf

   describe("repeating container description") {
      it("x") {
      }

      it("y") {
      }
   }

   describe("some other container") {
      describe("repeating container description") {
         it("x") {
         }

         it("y") {
         }
      }
   }
})
