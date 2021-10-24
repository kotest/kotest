package com.sksamuel.kotest.engine.spec.types

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestType
import io.kotest.matchers.shouldBe

class DescribeSpecTestTypeTest : DescribeSpec() {
   init {
      context("context") {
         this.testCase.type shouldBe TestType.Container
         context("context 2") {
            this.testCase.type shouldBe TestType.Container
            describe("test") {
               this.testCase.type shouldBe TestType.Container
               it("it") {
                  this.testCase.type shouldBe TestType.Test
               }
            }
         }
         describe("test 2") {
            this.testCase.type shouldBe TestType.Container
            it("it 2") {
               this.testCase.type shouldBe TestType.Test
            }
         }
      }
      describe("test 3") {
         this.testCase.type shouldBe TestType.Container
         it("it 3") {
            this.testCase.type shouldBe TestType.Test
         }
      }
   }
}
