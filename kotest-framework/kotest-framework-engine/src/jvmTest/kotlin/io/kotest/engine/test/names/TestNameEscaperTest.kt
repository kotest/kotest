package io.kotest.engine.test.names

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TestNameEscaperTest : FunSpec() {
   init {
      test("should escape periods") {
         TestNameEscaper.escape("foo.bar") shouldBe "foo bar"
      }
   }
}
