package com.sksamuel.kotest.tests.json

import io.kotest.assertions.json.ExtractedValue
import io.kotest.assertions.json.JsonPathNotFound
import io.kotest.assertions.json.decrementIndexOfJsonArray
import io.kotest.assertions.json.extractByPath
import io.kotest.assertions.json.findValidSubPath
import io.kotest.assertions.json.removeLastPartFromPath
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.nulls.shouldBeNull
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
          "steps": [
            {
               "name": "Boil",
               "comments": ["Heat on 3", "No lid"]
            }
          ],
          "comments": null
      }
   """.trimIndent()

   init {
      "extractByPath" should {
         "find not null value by valid path" {
            extractByPath<String>(json, "$.regime.temperature.unit") shouldBe ExtractedValue("F")
         }
         "find not null value by valid path in array" {
            extractByPath<String>(json, "$.steps[0].comments[1]") shouldBe ExtractedValue("No lid")
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
         "decrement positive index" {
            removeLastPartFromPath("$.ingredients[3]") shouldBe "$.ingredients[2]"
            removeLastPartFromPath("$.steps[0].comments[2]") shouldBe "$.steps[0].comments[1]"
         }
         "not decrement zero index" {
            removeLastPartFromPath("$.ingredients[0]") shouldBe "$"
         }
      }

      "decrementIndexOfJsonArray" should {
         "return null for anything that is not an indexed name" {
            decrementIndexOfJsonArray("color").shouldBeNull()
            decrementIndexOfJsonArray("color[").shouldBeNull()
            decrementIndexOfJsonArray("color[123").shouldBeNull()
            decrementIndexOfJsonArray("color[red]").shouldBeNull()
            decrementIndexOfJsonArray("color[12][3]").shouldBeNull()
         }
         "return subPath with decremented index" {
            decrementIndexOfJsonArray("color[2]") shouldBe "color[1]"
            decrementIndexOfJsonArray("color[1]") shouldBe "color[0]"
         }
         "return null if index cannot be decremented" {
            decrementIndexOfJsonArray("color[0]").shouldBeNull()
         }
      }

      "findValidSubPath" should {
          "find valid sub path" {
             findValidSubPath(json, "$.regime.temperature.unit.name.some.more.tokens") shouldBe "$.regime.temperature.unit"
             findValidSubPath(json, "$.regime.temperature.unit.name") shouldBe "$.regime.temperature.unit"
             findValidSubPath(json, "$.regime.temperature.name") shouldBe "$.regime.temperature"
             findValidSubPath(json, "$.regime.no_such_element") shouldBe "$.regime"
             findValidSubPath(json, "$.steps[0].comments[2]") shouldBe "$.steps[0].comments[1]"
          }
         "return null when nothing found" {
            findValidSubPath(json, "$.no.such.path") shouldBe null
         }
      }
   }
}
