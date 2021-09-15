package com.sksamuel.kotest.engine

import io.kotest.core.config.configuration
import io.kotest.core.spec.DisplayName
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.test.names.DisplayNameFormatter
import io.kotest.matchers.shouldBe

@DisplayName("ZZZZZ")
class DisplayNameTest : FunSpec() {
  init {
    test("@DisplayName should be used for spec name") {
       DisplayNameFormatter(configuration).format(DisplayNameTest::class) shouldBe "ZZZZZ"
    }
  }
}
