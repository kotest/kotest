package com.sksamuel.kotest.engine

import io.kotest.core.spec.DisplayName
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.toDescription
import io.kotest.matchers.shouldBe

@DisplayName("ZZZZZ")
class DisplayNameTest : FunSpec() {
  init {
    test("@DisplayName should be used for spec name") {
       DisplayNameTest::class.toDescription().name.displayName shouldBe "ZZZZZ"
    }
  }
}
