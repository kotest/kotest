package com.sksamuel.kotlintest.runner.console

import io.kotlintest.Description
import io.kotlintest.runner.console.WordSpecStyleParser
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class WordSpecStyleParserTest : FunSpec() {

  init {

    test("should parse word spec") {
      WordSpecStyleParser.parse(Description.spec("myspec"), "foo should bar") shouldBe
          Description.spec("myspec").append("foo should").append("bar")

      WordSpecStyleParser.parse(Description.spec("myspec"),
          "foo    should bar!  ") shouldBe
          Description.spec("myspec").append("foo    should").append("bar!  ")
    }
  }
}