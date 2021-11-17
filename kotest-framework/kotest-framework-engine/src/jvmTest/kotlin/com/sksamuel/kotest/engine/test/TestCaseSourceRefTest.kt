package com.sksamuel.kotest.engine.test

import io.kotest.core.SourceRef
import io.kotest.core.config.Configuration
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.Materializer
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.seconds

class TestCaseSourceRefTest : FunSpec() {
   init {
      test("source ref should include file name and line number") {
         val tests = Materializer(Configuration()).materialize(MySpec())
         tests.first().source shouldBe SourceRef.ClassLineSource("com.sksamuel.kotest.engine.test.MySpec", 28)
         tests[1].source shouldBe SourceRef.ClassLineSource("com.sksamuel.kotest.engine.test.MySpec", 31)
      }

      test("source ref should be performant").config(timeout = 30.seconds) {
         repeat(100000) {
            Materializer(Configuration()).materialize(MySpec()).first().source
         }
      }
   }
}

private class MySpec : FunSpec() {
   init {
      test("my test case") {
         1 shouldBe 1
      }
      test("test case 2") {
         1 shouldBe 1
      }
   }
}
