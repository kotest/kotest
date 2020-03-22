package com.sksamuel.kotest.runner.console

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.Description
import io.kotest.matchers.shouldBe
import io.kotest.runner.console.WordSpecStyleParser

class WordSpecStyleParserTest : FunSpec() {

  init {

    test("should parse word spec") {
      WordSpecStyleParser.parse(Description.spec("myspec"), "foo should bar") shouldBe
          Description.spec("myspec").append("foo should").append("bar")

      WordSpecStyleParser.parse(
          Description.spec("myspec"),
          "foo    should bar!  ") shouldBe
          Description.spec("myspec").append("foo    should").append("bar!  ")
    }
  }
}
