package com.sksamuel.kotest.specs

import io.kotest.core.specs.createTestName
import io.kotest.shouldBe
import io.kotest.specs.FunSpec

class TestPrefixesTest : FunSpec() {

  init {
    val prefix = "Prefix: "

    test("Should bring bang to the start of the test if there's no focus") {
      val name = "!banged"

      val bangedName = createTestName(prefix, name)

      bangedName shouldBe "!Prefix: banged"
    }

    test("Should bring focus to the start of the test if there's no bang") {
      val name = "f:Focused"

      val focusedName = createTestName(prefix, name)

      focusedName shouldBe "f:Prefix: Focused"
    }

    test("Should bring bang to the start of the test if there's a focus after it") {
      val name = "!f: BangFocus"

      val focusBangedName = createTestName(prefix, name)

      focusBangedName shouldBe "!Prefix: f: BangFocus"
    }

  }
}
