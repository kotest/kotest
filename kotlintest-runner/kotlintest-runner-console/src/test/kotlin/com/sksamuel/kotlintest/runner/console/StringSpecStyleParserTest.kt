package com.sksamuel.kotlintest.runner.console

import io.kotlintest.Description
import io.kotlintest.runner.console.StringSpecStyleParser
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

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