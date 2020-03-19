package com.sksamuel.kotest

import io.kotest.core.Tag
import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import java.io.File

@Ignored
class JUnitXMLReportTest : WordSpec() {

   override fun tags(): Set<Tag> = setOf(GithubActionsTag)

   private fun root(): Element {

      val ReportPath =
         "kotest-tests/kotest-tests-core/build/test-results/test/TEST-com.sksamuel.kotest.specs.wordspec.WordSpecTest.xml"
      val file = when {
         System.getenv("GITHUB_ACTIONS") == "true" -> {
            println(
               "XML: " + File(System.getenv("GITHUB_WORKSPACE") + "/kotlintest/kotest-tests/kotest-tests-core/build/test-results/test").listFiles()
                  .joinToString("\n")
            )
            File(System.getenv("GITHUB_WORKSPACE") + "/kotest/$ReportPath")
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
            root.getChildren("testcase").map { it.getAttributeValue("name") }.toSet()
               .shouldBe(setOf("have another test", "have a test with config", "have a test"))
            root.getChildren("testcase").map { it.getAttributeValue("classname") }.toSet()
               .shouldBe(setOf("com.sksamuel.kotest.specs.wordspec.WordSpecTest"))
         }
      }
   }
}
