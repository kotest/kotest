package com.sksamuel.kotest.tests.json

import io.kotest.assertions.json.ExtractedValue
import io.kotest.assertions.json.JsonPathNotFound
import io.kotest.assertions.json.extractByPath
import io.kotest.assertions.json.findValidSubPath
import io.kotest.assertions.json.removeLastPartFromPath
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language

class ExtractByPathTest: WordSpec() {
   @Language("JSON")
   private val json = """
      {
          "ingredients": ["Rice", "Water", "Salt"],
          "appliance": {
             "type": "Stove",
             "kind": "Electric"
          },
          "regime": {
            "temperature": {
                "degrees": 320,
                "unit": "F"
            }
          },
          "comments": null
      }
   """.trimIndent()

   init {
      "extractByPath" should {
         "find not null value by valid path" {
            extractByPath<String>(json, "$.regime.temperature.unit") shouldBe ExtractedValue("F")
         }
         "find null value by valid path" {
            extractByPath<String>(json, "$.comments") shouldBe ExtractedValue(null)
         }
         "path not found" {
            extractByPath<String>(json, "$.regime.temperature.unit.name") shouldBe JsonPathNotFound
         }
      }

      "removeLastPartFromPath" should {
         "remove last part" {
            removeLastPartFromPath("$.regime.temperature.unit.name") shouldBe "$.regime.temperature.unit"
            removeLastPartFromPath("$.regime.temperature.unit") shouldBe "$.regime.temperature"
            removeLastPartFromPath("$.regime.temperature") shouldBe "$.regime"
            removeLastPartFromPath("$.regime") shouldBe "$"
         }
      }

      "findValidSubPath" should {
          "find valid sub path" {
             findValidSubPath(json, "$.regime.temperature.unit.name.some.more.tokens") shouldBe "$.regime.temperature.unit"
             findValidSubPath(json, "$.regime.temperature.unit.name") shouldBe "$.regime.temperature.unit"
             findValidSubPath(json, "$.regime.temperature.name") shouldBe "$.regime.temperature"
             findValidSubPath(json, "$.regime.no_such_element") shouldBe "$.regime"
          }
         "return null when nothing found" {
            findValidSubPath(json, "$.no.such.path") shouldBe null
         }
      }
   }
}
