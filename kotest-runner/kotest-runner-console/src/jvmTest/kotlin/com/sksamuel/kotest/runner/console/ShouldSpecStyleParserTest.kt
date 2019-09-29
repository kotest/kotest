package com.sksamuel.kotest.runner.console

import io.kotest.Description
import io.kotest.runner.console.ShouldSpecStyleParser
import io.kotest.shouldBe
import io.kotest.specs.FunSpec

class ShouldSpecStyleParserTest : FunSpec() {

  init {

    test("should parse tests") {
      ShouldSpecStyleParser.parse(Description.spec("myspec"), "should test") shouldBe
          Description.spec("myspec").append("should test")

      ShouldSpecStyleParser.parse(Description.spec("myspec"),
          "should testatesta--") shouldBe
          Description.spec("myspec").append("should testatesta--")
    }

    test("should parse tests with parent") {
      ShouldSpecStyleParser.parse(Description.spec("myspec"), "foo should testa") shouldBe
          Description.spec("myspec").append("foo").append("should testa")

      ShouldSpecStyleParser.parse(Description.spec("myspec"),
          "foo -- bar should testatesta--") shouldBe
          Description.spec("myspec").append("foo").append("bar").append("should testatesta--")
    }

  }
}
