package io.kotlintest.runner.console

import io.kotlintest.Description
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class FeatureSpecStyleParserTest : FunSpec() {

  init {

    test("should parse Feature") {
      FeatureSpecStyleParser.parse(Description.spec("myspec"), "Feature: foo") shouldBe
          Description.spec("myspec").append("Feature: foo")

      FeatureSpecStyleParser.parse(Description.spec("myspec"), "Feature:    foo!") shouldBe
          Description.spec("myspec").append("Feature:    foo!")
    }

    test("should parse Feature / Scenario") {
      FeatureSpecStyleParser.parse(Description.spec("myspec"), "Feature: foo Scenario: bar") shouldBe
          Description.spec("myspec").append("Feature: foo").append("Scenario: bar")

      FeatureSpecStyleParser.parse(Description.spec("myspec"), "Feature: foo     Scenario: bar!!!") shouldBe
          Description.spec("myspec").append("Feature: foo    ").append("Scenario: bar!!!")
    }
  }
}