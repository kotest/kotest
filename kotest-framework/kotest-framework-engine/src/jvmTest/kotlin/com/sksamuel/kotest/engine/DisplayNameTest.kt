package com.sksamuel.kotest.engine

import io.kotest.core.spec.DisplayNameAnno
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.plan.toDescriptor

@DisplayNameAnno("ZZZZZ")
class DisplayNameTest : FunSpec() {
  init {
    test("@DisplayName should be used for spec name") {
       DisplayNameTest::class.toDescriptor().name.displayName shouldBe "ZZZZZ"
    }
  }
}
