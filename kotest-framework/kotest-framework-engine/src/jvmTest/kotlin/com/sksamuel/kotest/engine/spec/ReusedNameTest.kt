package com.sksamuel.kotest.engine.spec

import io.kotest.core.annotation.Description
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec

@Description("Checks that a context name can be re-used in another, non competing scope")
@EnabledIf(LinuxOnlyGithubCondition::class)
class ReusedNameTest : DescribeSpec({

   isolationMode = IsolationMode.InstancePerRoot

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
