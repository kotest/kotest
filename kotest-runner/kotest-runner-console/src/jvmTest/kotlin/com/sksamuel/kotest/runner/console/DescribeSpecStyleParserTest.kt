package com.sksamuel.kotest.runner.console

import io.kotest.Description
import io.kotest.runner.console.DescribeSpecStyleParser
import io.kotest.shouldBe
import io.kotest.specs.FunSpec

class DescribeSpecStyleParserTest : FunSpec() {

  init {

    test("should parse Describe") {
      DescribeSpecStyleParser.parse(Description.spec("myspec"), "Describe: foo") shouldBe
          Description.spec("myspec").append("Describe: foo")

      DescribeSpecStyleParser.parse(Description.spec("myspec"),
          "Describe:    foo!") shouldBe
          Description.spec("myspec").append("Describe:    foo!")
    }

    test("should parse Describe It") {
      DescribeSpecStyleParser.parse(Description.spec("myspec"),
          "Describe: foo It: bar") shouldBe
          Description.spec("myspec").append("Describe: foo").append("It: bar")

      DescribeSpecStyleParser.parse(Description.spec("myspec"),
          "Describe: foo     It: bar!!!") shouldBe
          Description.spec("myspec").append("Describe: foo    ").append("It: bar!!!")
    }
  }
}
