package com.sksamuel.kotest.tests.json

import io.kotest.assertions.json.ExtractedValue
import io.kotest.assertions.json.JsonPathNotFound
import io.kotest.assertions.json.JsonSubPathFound
import io.kotest.assertions.json.JsonSubPathNotFound
import io.kotest.assertions.json.extractByPath
import io.kotest.assertions.json.extractPossiblePathOfJsonArray
import io.kotest.assertions.json.findValidSubPath
import io.kotest.assertions.json.findValidSubPath2
import io.kotest.assertions.json.possibleSizeOfJsonArray
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
            },
            "comments": []
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
            findValidSubPath(
               json,
               "$.regime.temperature.unit.name.some.more.tokens"
            ) shouldBe "$.regime.temperature.unit"
            findValidSubPath(json, "$.regime.temperature.unit.name") shouldBe "$.regime.temperature.unit"
            findValidSubPath(json, "$.regime.temperature.name") shouldBe "$.regime.temperature"
            findValidSubPath(json, "$.regime.no_such_element") shouldBe "$.regime"
         }
         "return null when nothing found" {
            findValidSubPath(json, "$.no.such.path") shouldBe null
         }
      }

      "findValidSubPath2" should {
         "find valid sub path" {
            findValidSubPath2(
               json,
               "$.regime.temperature.unit.name.some.more.tokens"
            ) shouldBe JsonSubPathFound("$.regime.temperature.unit")
            findValidSubPath2(
               json,
               "$.regime.temperature.unit.name"
            ) shouldBe JsonSubPathFound("$.regime.temperature.unit")
            findValidSubPath2(json, "$.regime.temperature.name") shouldBe JsonSubPathFound("$.regime.temperature")
            findValidSubPath2(json, "$.regime.no_such_element") shouldBe JsonSubPathFound("$.regime")
         }
         "return null when nothing found" {
            findValidSubPath2(json, "$.no.such.path") shouldBe JsonSubPathNotFound
         }
      }

      "possibleSizeOfJsonArray" should {
         "return size of array" {
            possibleSizeOfJsonArray(json, "$.ingredients") shouldBe 3
            possibleSizeOfJsonArray(json, "$.steps[0].comments") shouldBe 2
            possibleSizeOfJsonArray(json, "$.steps") shouldBe 1
            possibleSizeOfJsonArray(json, "$.regime.comments") shouldBe 0
         }
         "return null if not an array" {
            possibleSizeOfJsonArray(json, "$.appliance.type") shouldBe null
            possibleSizeOfJsonArray(json, "$.appliance") shouldBe null
            possibleSizeOfJsonArray(json, "$.steps.name") shouldBe null
         }
      }

      "extractPossiblePathOfJsonArray" should {
         "return null if not array" {
            extractPossiblePathOfJsonArray("$.not.an.array") shouldBe null
         }
         "return null if not valid index" {
            extractPossiblePathOfJsonArray("$.recipe.ingredients[") shouldBe null
            extractPossiblePathOfJsonArray("$.recipe.ingredients[]") shouldBe null
            extractPossiblePathOfJsonArray("$.recipe.ingredients]") shouldBe null
            extractPossiblePathOfJsonArray("$.recipe.ingredients[42") shouldBe null
            extractPossiblePathOfJsonArray("$.recipe.ingredients[42[") shouldBe null
            extractPossiblePathOfJsonArray("$.recipe.ingredients[42]]") shouldBe null
            extractPossiblePathOfJsonArray("$.recipe.ingredients[BAD-INDEX]") shouldBe null
         }
         "return path" {
            extractPossiblePathOfJsonArray("$.recipe.ingredients[42]") shouldBe "\$.recipe.ingredients"
         }
      }
   }
}
