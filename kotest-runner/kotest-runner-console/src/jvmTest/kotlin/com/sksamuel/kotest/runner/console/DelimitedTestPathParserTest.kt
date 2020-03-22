package com.sksamuel.kotest.runner.console

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.Description
import io.kotest.matchers.shouldBe
import io.kotest.runner.console.DelimitedTestPathParser

class DelimitedTestPathParserTest : FunSpec() {

  init {

    test("should parse single tests") {
      DelimitedTestPathParser.parse(Description.spec("myspec"), "testa") shouldBe
          Description.spec("myspec").append("testa")

      DelimitedTestPathParser.parse(Description.spec("myspec"), "testa--") shouldBe
          Description.spec("myspec").append("testa--")
    }

    test("should parse multiple tests") {
      DelimitedTestPathParser.parse(
          Description.spec("myspec"),
          "testa -- testb -- testc") shouldBe
          Description.spec("myspec").append("testa").append("testb").append("testc")

      DelimitedTestPathParser.parse(
          Description.spec("myspec"),
          "testa -- testb-- -- testc") shouldBe
          Description.spec("myspec").append("testa").append("testb--").append("testc")
    }

  }
}
