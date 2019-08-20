package com.sksamuel.kotlintest.runner.console

import io.kotlintest.Description
import io.kotlintest.runner.console.DescribeSpecStyleParser
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

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
