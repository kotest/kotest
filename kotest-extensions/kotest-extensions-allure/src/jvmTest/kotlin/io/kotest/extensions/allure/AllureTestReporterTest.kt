package io.kotest.extensions.allure

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.Order
import io.kotest.core.spec.style.WordSpec
import io.kotest.inspectors.forOne
import io.kotest.matchers.shouldBe
import java.nio.file.Paths

// this must have a higher order number than the dummy tests
// so that when we get to this test, we have some data
@Order(1)
class AllureTestReporterTest : WordSpec() {

   private val mapper = jacksonObjectMapper()

   private fun findTestFile(name: String): JsonNode {
      return Paths.get("./build/allure-results").toFile().listFiles()
        ?.filter { it.name.endsWith(".json") }
         ?.map { mapper.readTree(it) }
         ?.first { it.get("name").textValue() == name } ?: error("Could not find test file for $name")
   }

   init {

      "AllureTestReporter" should {
         "write out data" {
            val json = findTestFile("Given: a given When: another when Then: a final then")
            json["name"].textValue() shouldBe "Given: a given When: another when Then: a final then"
            json["fullName"].textValue() shouldBe "Given: a given / When: another when / Then: a final then"
            val labels = json.get("labels") as ArrayNode
            labels.toList().forOne {
               it["name"].asText() shouldBe "suite"
               it["value"].textValue() shouldBe "com.sksamuel.kotest.DummyBehaviorSpecTest"
            }
            labels.toList().forOne {
               it["name"].textValue() shouldBe "language"
               it["value"].textValue() shouldBe "kotlin"
            }
            labels.toList().forOne {
               it["name"].textValue() shouldBe "framework"
               it["value"].textValue() shouldBe "kotest"
            }
            labels.toList().forOne {
               it["name"].textValue() shouldBe "epic"
               it["value"].textValue() shouldBe "my epic"
            }
            labels.toList().forOne {
               it["name"].textValue() shouldBe "package"
               it["value"].textValue() shouldBe "com.sksamuel.kotest"
            }
         }
         "set correct historyId" {
            val json = findTestFile("context a should work")
            json["historyId"].textValue() shouldBe "com.sksamuel.kotest.DummyShouldSpec/a -- work"
         }
         "set correct testCaseId" {
            val json = findTestFile("context a should work")
            json["testCaseId"].textValue() shouldBe "com.sksamuel.kotest.DummyShouldSpec/a -- work"
         }
         "set correct fullName" {
            val json = findTestFile("context a should work")
            json["fullName"].textValue() shouldBe "context a / should work"
         }
      }
   }

}
