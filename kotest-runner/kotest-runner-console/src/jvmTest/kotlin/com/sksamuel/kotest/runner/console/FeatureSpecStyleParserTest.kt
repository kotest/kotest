package com.sksamuel.kotest.runner.console

import io.kotest.Description
import io.kotest.runner.console.FeatureSpecStyleParser
import io.kotest.shouldBe
import io.kotest.specs.FunSpec

class FeatureSpecStyleParserTest : FunSpec() {

  init {

    test("should parse Feature") {
      FeatureSpecStyleParser.parse(Description.spec("myspec"), "Feature: foo") shouldBe
          Description.spec("myspec").append("Feature: foo")

      FeatureSpecStyleParser.parse(Description.spec("myspec"), "Feature:    foo!") shouldBe
          Description.spec("myspec").append("Feature:    foo!")
    }

    test("should parse Feature / Scenario") {
      FeatureSpecStyleParser.parse(Description.spec("myspec"),
          "Feature: foo Scenario: bar") shouldBe
          Description.spec("myspec").append("Feature: foo").append("Scenario: bar")

      FeatureSpecStyleParser.parse(Description.spec("myspec"),
          "Feature: foo     Scenario: bar!!!") shouldBe
          Description.spec("myspec").append("Feature: foo    ").append("Scenario: bar!!!")
    }
  }
}
