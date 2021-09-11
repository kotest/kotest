package com.sksamuel.kotest.engine.test

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.test.interceptors.testNameEscape
import io.kotest.matchers.shouldBe

class TestNameEscapeTest : FunSpec() {
   init {
      test("should escape periods") {
         testNameEscape("foo.bar") shouldBe "foo bar"
      }
   }
}
