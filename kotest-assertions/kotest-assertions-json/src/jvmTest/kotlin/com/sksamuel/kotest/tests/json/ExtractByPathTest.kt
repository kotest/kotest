package com.sksamuel.kotest.tests.json

import io.kotest.assertions.json.ExtractValueOutcome.ExtractedValue
import io.kotest.assertions.json.ExtractValueOutcome.JsonPathNotFound
import io.kotest.assertions.json.JsonArrayElementRef
import io.kotest.assertions.json.JsonSubPathFound
import io.kotest.assertions.json.JsonSubPathJsonArrayTooShort
import io.kotest.assertions.json.JsonSubPathNotFound
import io.kotest.assertions.json.extractByPath
import io.kotest.assertions.json.extractPossiblePathOfJsonArray
import io.kotest.assertions.json.findValidSubPath
import io.kotest.assertions.json.getPossibleSizeOfJsonArray
import io.kotest.assertions.json.removeLastPartFromPath
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language

@EnabledIf(LinuxOnlyGithubCondition::class)
class ExtractByPathTest : WordSpec() {
   @Language("JSON")
   private val json =
      """
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
            extractByPath(
               json,
               "$.regime.temperature.unit",
               String::class.java
            ) shouldBe ExtractedValue("F")
         }
         "find null value by valid path" {
            extractByPath(json, "$.comments", String::class.java) shouldBe ExtractedValue(null)
         }
         "path not found" {
            extractByPath(json, "$.regime.temperature.unit.name", String::class.java) shouldBe JsonPathNotFound
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

      "findValidSubPath2" should {
         "find valid sub path" {
            findValidSubPath(
               json,
               "$.regime.temperature.unit.name.some.more.tokens",
            ) shouldBe JsonSubPathFound("$.regime.temperature.unit")
            findValidSubPath(
               json,
               "$.regime.temperature.unit.name",
            ) shouldBe JsonSubPathFound("$.regime.temperature.unit")
            findValidSubPath(json, "$.regime.temperature.name") shouldBe JsonSubPathFound("$.regime.temperature")
            findValidSubPath(json, "$.regime.no_such_element") shouldBe JsonSubPathFound("$.regime")
         }
         "return JsonSubPathJsonArrayTooShort" {
            findValidSubPath(json, "$.steps[0].comments[2]") shouldBe
               JsonSubPathJsonArrayTooShort("$.steps[0].comments", 2, 2)
            findValidSubPath(json, "$.steps[1].comments[2]") shouldBe
               JsonSubPathJsonArrayTooShort("$.steps", 1, 1)
         }
         "return null when nothing found" {
            findValidSubPath(json, "$.no.such.path") shouldBe JsonSubPathNotFound
         }
      }

      "getPossibleSizeOfJsonArray" should {
         "return size of array" {
            getPossibleSizeOfJsonArray(json, "$.ingredients") shouldBe 3
            getPossibleSizeOfJsonArray(json, "$.steps[0].comments") shouldBe 2
            getPossibleSizeOfJsonArray(json, "$.steps") shouldBe 1
            getPossibleSizeOfJsonArray(json, "$.regime.comments") shouldBe 0
         }
         "return null if not an array" {
            getPossibleSizeOfJsonArray(json, "$.appliance.type") shouldBe null
            getPossibleSizeOfJsonArray(json, "$.appliance") shouldBe null
            getPossibleSizeOfJsonArray(json, "$.steps.name") shouldBe null
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
            extractPossiblePathOfJsonArray("$.recipe.ingredients[42]") shouldBe
               JsonArrayElementRef("\$.recipe.ingredients", 42)
         }
         "handle nested JSONArray" {
            extractPossiblePathOfJsonArray("$.recipe.ingredients[2].comments[3]") shouldBe
               JsonArrayElementRef("\$.recipe.ingredients[2].comments", 3)
         }
      }
   }
}
