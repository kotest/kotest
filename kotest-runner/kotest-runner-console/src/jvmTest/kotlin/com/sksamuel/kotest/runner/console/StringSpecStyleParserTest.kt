package com.sksamuel.kotest.runner.console

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.Description
import io.kotest.matchers.shouldBe
import io.kotest.runner.console.StringSpecStyleParser

class StringSpecStyleParserTest : FunSpec() {

  init {

    test("should parse tests") {
      StringSpecStyleParser.parse(Description.spec("myspec"), "testa") shouldBe
          Description.spec("myspec").append("testa")

      StringSpecStyleParser.parse(Description.spec("myspec"), "testatesta--") shouldBe
          Description.spec("myspec").append("testatesta--")
    }

  }
}
