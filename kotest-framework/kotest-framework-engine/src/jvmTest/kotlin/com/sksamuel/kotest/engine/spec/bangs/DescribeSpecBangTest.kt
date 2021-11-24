package com.sksamuel.kotest.engine.spec.bangs

import io.kotest.core.spec.style.DescribeSpec

class DescribeSpecBangTest : DescribeSpec() {

   init {

      describe("!BangedDescribe") {
         error("CRUNCH!")
      }

      describe("!Foo") {
         it("foo") {
            error("FLRBBBBB!")
         }
      }

      describe("NonBangedDescribe") {
         it("!BangedIt") {
            error("CLASH!")
         }

         describe("!BangedContext") {
            error("KRUNCH!")
         }

         describe("NonBangedContext") {
            it("!BangedIt") {
               error("OOOFF!")
            }
         }
      }
   }
}
