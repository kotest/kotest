package com.sksamuel.kotlintest.runner.console

import io.kotlintest.Description
import io.kotlintest.runner.console.DelimitedTestPathParser
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class DelimitedTestPathParserTest : FunSpec() {

  init {

    test("should parse single tests") {
      DelimitedTestPathParser.parse(Description.spec("myspec"), "testa") shouldBe
          Description.spec("myspec").append("testa")

      DelimitedTestPathParser.parse(Description.spec("myspec"), "testa--") shouldBe
          Description.spec("myspec").append("testa--")
    }

    test("should parse multiple tests") {
      DelimitedTestPathParser.parse(Description.spec("myspec"),
          "testa -- testb -- testc") shouldBe
          Description.spec("myspec").append("testa").append("testb").append("testc")

      DelimitedTestPathParser.parse(Description.spec("myspec"),
          "testa -- testb-- -- testc") shouldBe
          Description.spec("myspec").append("testa").append("testb--").append("testc")
    }

  }
}
