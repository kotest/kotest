package com.sksamuel.kotest.runner.console

import io.kotest.Description
import io.kotest.runner.console.StringSpecStyleParser
import io.kotest.shouldBe
import io.kotest.specs.FunSpec

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
