package io.kotlintest.runner.console

import io.kotlintest.Description
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class FreeSpecStyleParserTest : FunSpec() {

  init {

    test("should parse single tests") {
      FreeSpecStyleParser.parse(Description.spec("myspec"), "testa") shouldBe
          Description.spec("myspec").append("testa")

      FreeSpecStyleParser.parse(Description.spec("myspec"), "testa--") shouldBe
          Description.spec("myspec").append("testa--")
    }

    test("should parse multiple tests") {
      FreeSpecStyleParser.parse(Description.spec("myspec"), "testa -- testb -- testc") shouldBe
          Description.spec("myspec").append("testa").append("testb").append("testc")

      FreeSpecStyleParser.parse(Description.spec("myspec"), "testa -- testb-- -- testc") shouldBe
          Description.spec("myspec").append("testa").append("testb--").append("testc")
    }

  }
}