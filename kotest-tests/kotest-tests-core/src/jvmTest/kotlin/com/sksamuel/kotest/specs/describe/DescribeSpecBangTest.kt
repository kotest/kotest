package com.sksamuel.kotest.specs.describe

import com.sksamuel.kotest.specs.attemptToFail
import io.kotest.core.spec.style.DescribeSpec

class DescribeSpecBangTest : DescribeSpec() {

   init {

      describe("!BangedDescribe") {
         attemptToFail()
      }

      describe("!Foo") {
         it("foo") {
            assertEquals(1, 2)
         }
      }

      describe("NonBangedDescribe") {
         it("!BangedIt") {
            attemptToFail()
         }

         context("!BangedContext") {
            attemptToFail()
         }

         context("NonBangedContext") {
            it("!BangedIt") {
               attemptToFail()
            }
         }
      }
   }
}
