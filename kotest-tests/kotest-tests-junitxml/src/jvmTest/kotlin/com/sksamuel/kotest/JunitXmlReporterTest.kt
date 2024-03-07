package com.sksamuel.kotest

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.Order
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import org.jdom2.Element
import org.jdom2.input.SAXBuilder

// this must have a higher order number than the dummy tests
// so that when we get to this test, we already have written some data
@Order(1)
class JunitXmlReporterTest : WordSpec() {

   private fun loadTestFile(filename: String): Element {
      val path = "./build/test-results/$filename"
      val builder = SAXBuilder()
      val doc = builder.build(path)
      return doc.rootElement
   }

   init {

      "JunitXmlReporter with containers" should {

         "include all tests" {
            val root = loadTestFile("with_containers/TEST-com.sksamuel.kotest.DummyBehaviorSpecTest.xml")
            assertSoftly {
               root.getAttributeValue("name").shouldBe("com.sksamuel.kotest.DummyBehaviorSpecTest")
               root.getAttributeValue("tests").shouldBe("6")
               root.getAttributeValue("skipped").shouldBe("0")
               root.getAttributeValue("errors").shouldBe("0")
               root.getAttributeValue("failures").shouldBe("0")
            }
         }

         "include test names" {
            val root = loadTestFile("with_containers/TEST-com.sksamuel.kotest.DummyBehaviorSpecTest.xml")
            root.getChildren("testcase").map { it.getAttributeValue("name") }.toSet() shouldBe
               setOf(
                  "When: another when",
                  "When: a when",
                  "Then: a final then",
                  "Given: a given",
                  "Then: a then",
                  "Then: another then"
               )
            root.getChildren("testcase").map { it.getAttributeValue("classname") }.toSet()
               .shouldBe(setOf("com.sksamuel.kotest.DummyBehaviorSpecTest"))
         }
      }

      "JunitXmlReporter without containers" should {

         "only include leaf tests" {
            val root = loadTestFile("without_containers/TEST-com.sksamuel.kotest.DummyBehaviorSpecTest.xml")
            assertSoftly {
               root.getAttributeValue("name").shouldBe("com.sksamuel.kotest.DummyBehaviorSpecTest")
               root.getAttributeValue("tests").shouldBe("3")
               root.getAttributeValue("skipped").shouldBe("0")
               root.getAttributeValue("errors").shouldBe("0")
               root.getAttributeValue("failures").shouldBe("0")
            }
         }

         "!include test names" {
            val root = loadTestFile("without_containers/TEST-com.sksamuel.kotest.DummyBehaviorSpecTest.xml")
            root.getChildren("testcase").map { it.getAttributeValue("name") }.toSet() shouldBe
               setOf(
                  "Given: a given When: a when Then: a then",
                  "Given: a given When: another when Then: a final then",
                  "Given: a given When: a when Then: another then"
               )
            root.getChildren("testcase").map { it.getAttributeValue("classname") }.toSet()
               .shouldBe(setOf("com.sksamuel.kotest.DummyBehaviorSpecTest"))
         }

         "include the full path of deeply nested tests" {
            val root = loadTestFile("without_containers/TEST-com.sksamuel.kotest.DummyFreeSpecTest.xml")
            root.getChildren("testcase").map { it.getAttributeValue("name") }.toSet() shouldBe
               setOf(
                  "1 -- 2 -- 3",
                  "1 -- 2 -- 4 -- 5 -- 6",
               )
            root.getChildren("testcase").map { it.getAttributeValue("classname") }.toSet()
               .shouldBe(setOf("com.sksamuel.kotest.DummyFreeSpecTest"))

         }
      }
   }

}
