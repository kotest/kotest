package com.sksamuel.kotest.runner.console

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.Description
import io.kotest.matchers.shouldBe
import io.kotest.runner.console.FeatureSpecStyleParser

class FeatureSpecStyleParserTest : FunSpec() {

  init {

    test("should parse Feature") {
      FeatureSpecStyleParser.parse(Description.spec("myspec"), "Feature: foo") shouldBe
          Description.spec("myspec").append("Feature: foo")

      FeatureSpecStyleParser.parse(Description.spec("myspec"), "Feature:    foo!") shouldBe
          Description.spec("myspec").append("Feature:    foo!")
    }

    test("should parse Feature / Scenario") {
      FeatureSpecStyleParser.parse(
          Description.spec("myspec"),
          "Feature: foo Scenario: bar") shouldBe
          Description.spec("myspec").append("Feature: foo").append("Scenario: bar")

      FeatureSpecStyleParser.parse(
          Description.spec("myspec"),
          "Feature: foo     Scenario: bar!!!") shouldBe
          Description.spec("myspec").append("Feature: foo    ").append("Scenario: bar!!!")
    }
  }
}
