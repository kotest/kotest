package com.sksamuel.kotest

import io.kotest.core.spec.CompositeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.shouldBe
import io.kotest.shouldThrowAny

val emptyTestName = funSpec {

    test("empty test name should error") {
        shouldThrowAny {
            EmptyTestName()
        }.message.shouldBe("Cannot add test with blank or empty name")
    }

    test("blank test name should error") {
        shouldThrowAny {
            BlankTestName()
        }.message.shouldBe("Cannot add test with blank or empty name")
    }
}

class TestNameTest : CompositeSpec(emptyTestName)

private class EmptyTestName : FunSpec() {
   init {
      test("") {

      }
   }
}


private class BlankTestName : FunSpec() {
   init {
      test("  ") {

      }
   }
}
