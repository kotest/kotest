package com.sksamuel.kotest.framework.api

import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.seconds

@Ignored
class TestCaseSourceRefTest : FunSpec() {
   init {
      test("source ref should include file name and line number") {
         MySpec().materializeRootTests().first().testCase.source.apply {
            fileName shouldBe "TestCaseSourceRefTest.kt"
            lineNumber shouldBe 31
         }
      }

      test("source ref should be performant").config(timeout = 10.seconds) {
         repeat(100000) {
            MySpec().materializeRootTests().first().testCase.source.apply {
               fileName shouldBe "TestCaseSourceRefTest.kt"
               lineNumber shouldBe 31
            }
         }
      }
   }
}

private class MySpec : FunSpec() {
   init {
      test("my test case") {
         1 shouldBe 1
      }
   }
}
