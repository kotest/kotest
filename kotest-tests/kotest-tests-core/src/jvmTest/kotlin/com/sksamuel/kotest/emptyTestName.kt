package com.sksamuel.kotest

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.CompositeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe

val emptyTestName = funSpec {

    test("empty test name should error") {
        shouldThrowAny {
            EmptyTestName()
        }.message.shouldBe("Cannot create test with blank or empty name")
    }

    test("blank test name should error") {
        shouldThrowAny {
            BlankTestName()
        }.message.shouldBe("Cannot create test with blank or empty name")
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
