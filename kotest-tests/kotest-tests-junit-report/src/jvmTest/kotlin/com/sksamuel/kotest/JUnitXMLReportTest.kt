package com.sksamuel.kotest

import io.kotest.Tag
import io.kotest.shouldBe
import io.kotest.specs.WordSpec
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import java.io.File

class JUnitXMLReportTest : WordSpec() {

  override fun tags(): Set<Tag> = setOf(AppveyorTag, TravisTag)

  fun root(): Element {

    val ReportPath = "kotest-tests/kotest-tests-core/build/test-results/test/TEST-com.sksamuel.kotest.specs.wordspec.WordSpecTest.xml"
    val file = when {
      System.getenv("TRAVIS") == "true" -> {
        println("XML: " + File(System.getenv("TRAVIS_BUILD_DIR") + "/kotest-tests/kotest-tests-core/build/test-results/test").listFiles().joinToString("\n"))
        File(System.getenv("TRAVIS_BUILD_DIR") + "/$ReportPath")
      }
      System.getenv("APPVEYOR") == "True" -> {
        println("XML: " + File(System.getenv("APPVEYOR_BUILD_FOLDER") + "/kotest-tests/kotest-tests-core/build/test-results/test").listFiles().joinToString("\n"))
        File(System.getenv("APPVEYOR_BUILD_FOLDER") + "/$ReportPath")
      }
      else -> throw RuntimeException()
    }

    val builder = SAXBuilder()
    val doc = builder.build(file)
    return doc.rootElement
  }

  init {

    // we test the output from the earlier test of tests in
    // kotest-tests/kotest-tests-core
    "JUnit XML Output" should {

      "include top level information" {
        val root = root()
        root.getAttributeValue("name").shouldBe("com.sksamuel.kotest.specs.wordspec.WordSpecTest")
        root.getAttributeValue("tests").shouldBe("5")
        root.getAttributeValue("skipped").shouldBe("0")
        root.getAttributeValue("errors").shouldBe("0")
        root.getAttributeValue("failures").shouldBe("0")
      }

      "include test names" {
        val root = root()
        root.getChildren("testcase").map { it.getAttributeValue("name") }.toSet().shouldBe(setOf("have another test", "have a test with config", "have a test"))
        root.getChildren("testcase").map { it.getAttributeValue("classname") }.toSet().shouldBe(setOf("com.sksamuel.kotest.specs.wordspec.WordSpecTest"))
      }
    }
  }
}