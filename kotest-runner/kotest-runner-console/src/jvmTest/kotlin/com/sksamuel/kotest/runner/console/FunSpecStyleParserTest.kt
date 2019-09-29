package com.sksamuel.kotest.runner.console

import io.kotest.Description
import io.kotest.runner.console.DelimitedTestPathParser
import io.kotest.shouldBe
import io.kotest.specs.FunSpec

class FunSpecStyleParserTest : FunSpec() {

  init {

    test("should parse tests") {
      DelimitedTestPathParser.parse(Description.spec("myspec"), "should testa") shouldBe
          Description.spec("myspec").append("should testa")

      DelimitedTestPathParser.parse(Description.spec("myspec"), "should testatesta--") shouldBe
          Description.spec("myspec").append("should testatesta--")
    }

    test("should parse tests with a context") {
      DelimitedTestPathParser.parse(Description.spec("myspec"), "some context -- some other context -- test") shouldBe
          Description.spec("myspec").append("some context").append("some other context").append("test")

      DelimitedTestPathParser.parse(Description.spec("myspec"), "some context -- test") shouldBe
          Description.spec("myspec").append("some context").append("test")

    }
  }
}
