package com.sksamuel.kotest.runner.console

import io.kotest.Description
import io.kotest.runner.console.WordSpecStyleParser
import io.kotest.shouldBe
import io.kotest.specs.FunSpec

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
