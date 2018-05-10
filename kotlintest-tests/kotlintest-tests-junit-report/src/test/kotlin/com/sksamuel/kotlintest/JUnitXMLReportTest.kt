package com.sksamuel.kotlintest

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import org.jdom2.input.SAXBuilder
import java.io.File

class JUnitXMLReportTest : WordSpec() {
  init {

    val ReportPath = "kotlintest-tests/kotlintest-tests-core/build/test-results/test/TEST-com.sksamuel.kotlintest.specs.WordSpecTest.xml"

    // we test the output from the earlier test of tests in
    // kotlintest-tests/kotlintest-tests-core
    "JUnit XML Output" should {

      val file = when {
        System.getenv("TRAVIS") == "true" -> {
          println("XML: " + File(System.getenv("TRAVIS_BUILD_DIR") + "/kotlintest-tests/kotlintest-tests-core/build/test-results/test").listFiles().joinToString("\n"))
          File(System.getenv("TRAVIS_BUILD_DIR") + ReportPath)
        }
        System.getenv("APPVEYOR") == "True" -> {
          println("XML: " + File(System.getenv("APPVEYOR_BUILD_FOLDER") + "/kotlintest-tests/kotlintest-tests-core/build/test-results/test").listFiles().joinToString("\n"))
          File(System.getenv("APPVEYOR_BUILD_FOLDER") + ReportPath)
        }
        else ->
          File(System.getProperty("user.home") + "/development/workspace/kotlintest/" + ReportPath)
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
        root.getChildren("testcase").map { it.getAttributeValue("name") }.toSet().shouldBe(setOf("have another test", "have a test with config", "have a test", "a context should"))
        root.getChildren("testcase").map { it.getAttributeValue("classname") }.toSet().shouldBe(setOf("com.sksamuel.kotlintest.tests.specs.WordSpecTest"))
      }
    }
  }
}