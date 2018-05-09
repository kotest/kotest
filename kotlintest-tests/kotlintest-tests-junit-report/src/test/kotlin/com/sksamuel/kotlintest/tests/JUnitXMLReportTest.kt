package com.sksamuel.kotlintest.tests

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import org.jdom2.input.SAXBuilder
import java.io.File

class JUnitXMLReportTest : WordSpec() {
  init {
    // we test the output from the earlier test of tests in
    // kotlintest-tests/kotlintest-tests-core
    "JUnit XML Output" should {

      val file = when {
        System.getenv("TRAVIS") == "true" ->
          File(System.getenv("TRAVIS_BUILD_DIR") + "/kotlintest-tests/kotlintest-tests-core/build/test-results/test/TEST-com.sksamuel.kotlintest.tests.specs.WordSpecTest.xml")
        System.getenv("APPVEYOR") == "true" ->
          File(System.getenv("APPVEYOR_BUILD_FOLDER") + "/kotlintest-tests/kotlintest-tests-core/build/test-results/test/TEST-com.sksamuel.kotlintest.tests.specs.WordSpecTest.xml")
        else ->
          File(System.getProperty("user.home") + "/development/workspace/kotlintest/kotlintest-tests/kotlintest-tests-core/build/test-results/test/TEST-com.sksamuel.kotlintest.tests.specs.WordSpecTest.xml")
      }

      val builder = SAXBuilder()
      val doc = builder.build(file)
      val root = doc.rootElement
      "include top level information" {
        root.getAttributeValue("name").shouldBe("com.sksamuel.kotlintest.tests.specs.WordSpecTest")
        root.getAttributeValue("tests").shouldBe("4")
        root.getAttributeValue("skipped").shouldBe("0")
        root.getAttributeValue("errors").shouldBe("0")
        root.getAttributeValue("failures").shouldBe("0")
      }
      "include test names" {
        root.getChildren("testcase").map { it.getAttributeValue("name") }.toSet().shouldBe(setOf("should have another test", "should have a test with config", "should have a test", "a context"))
        root.getChildren("testcase").map { it.getAttributeValue("classname") }.toSet().shouldBe(setOf("com.sksamuel.kotlintest.tests.specs.WordSpecTest"))
      }
    }
  }
}