package com.sksamuel.kotest.xml

import io.kotest.core.spec.style.DescribeSpec
import kotlin.test.Test

class KotestSpec : DescribeSpec() {
   init {
      describe("context a") {
         describe("context b") {
            describe("context c") {
               it("test d") {
               }
            }
         }
         it("test e") {
         }
         it("test f") {
         }
      }
      it("test g") {
      }
   }
}

class KotlinTest {
   @Test
   fun testA() {
   }

   @Test
   fun testB() {
   }
}
