package com.sksamuel.kotlintest.runner.console

import io.kotlintest.Description
import io.kotlintest.runner.console.FunSpecStyleParser
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class FunSpecStyleParserTest : FunSpec() {

  init {

    test("should parse tests") {
      FunSpecStyleParser.parse(Description.spec("myspec"), "should testa") shouldBe
          Description.spec("myspec").append("should testa")

      FunSpecStyleParser.parse(Description.spec("myspec"), "should testatesta--") shouldBe
          Description.spec("myspec").append("should testatesta--")
    }

  }
}