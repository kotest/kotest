package com.sksamuel.kotest.engine.spec.duplicatedname

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe

abstract class ExpectSpecDuplicateTest(iso: IsolationMode) : ExpectSpec() {
   init {
      isolationMode = iso
      context("foo") {
         expect("woo") {}
         expect("woo") { this.testCase.name.name shouldBe "(1) woo" }
         expect("woo") { this.testCase.name.name shouldBe "(2) woo" }
      }
      context("foo") {
         this.testCase.name.name shouldBe "(1) foo"
         expect("woo") {}
      }
      context("foo") {
         this.testCase.name.name shouldBe "(2) foo"
         expect("woo") {}
      }
   }
}

class ExpectSpecSingleInstanceDuplicateNameTest : ExpectSpecDuplicateTest(IsolationMode.SingleInstance)
class ExpectSpecInstancePerRootDuplicateNameTest : ExpectSpecDuplicateTest(IsolationMode.InstancePerRoot)
