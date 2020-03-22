package com.sksamuel.kotest.runner.console

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.Description
import io.kotest.matchers.shouldBe
import io.kotest.runner.console.DescribeSpecStyleParser

class DescribeSpecStyleParserTest : FunSpec() {

  init {

    test("should parse Describe") {
      DescribeSpecStyleParser.parse(Description.spec("myspec"), "Describe: foo") shouldBe
          Description.spec("myspec").append("Describe: foo")

      DescribeSpecStyleParser.parse(
          Description.spec("myspec"),
          "Describe:    foo!") shouldBe
          Description.spec("myspec").append("Describe:    foo!")
    }

    test("should parse Describe It") {
      DescribeSpecStyleParser.parse(
          Description.spec("myspec"),
          "Describe: foo It: bar") shouldBe
          Description.spec("myspec").append("Describe: foo").append("It: bar")

      DescribeSpecStyleParser.parse(
          Description.spec("myspec"),
          "Describe: foo     It: bar!!!") shouldBe
          Description.spec("myspec").append("Describe: foo    ").append("It: bar!!!")
    }
  }
}
