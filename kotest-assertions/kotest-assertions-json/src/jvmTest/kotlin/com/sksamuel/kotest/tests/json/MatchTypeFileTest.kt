package com.sksamuel.kotest.tests.json

import io.kotest.assertions.json.file.shouldBeJsonArray
import io.kotest.assertions.json.file.shouldBeJsonObject
import io.kotest.assertions.json.file.shouldBeValidJson
import io.kotest.assertions.json.file.shouldNotBeJsonArray
import io.kotest.assertions.json.file.shouldNotBeJsonObject
import io.kotest.assertions.json.file.shouldNotBeValidJson
import io.kotest.assertions.json.paths.shouldBeJsonArray
import io.kotest.assertions.json.paths.shouldBeJsonObject
import io.kotest.assertions.json.paths.shouldBeValidJson
import io.kotest.assertions.json.paths.shouldNotBeJsonArray
import io.kotest.assertions.json.paths.shouldNotBeJsonObject
import io.kotest.assertions.json.paths.shouldNotBeValidJson
import io.kotest.core.spec.style.FunSpec

class MatchTypeFileTest : FunSpec({
   test("should be valid json") {
      withJsonTestFile("""{"key":"value"}""").shouldBeValidJson()
      withJsonTestFile("""{"key":"value"}""").toPath().shouldBeValidJson()
   }
   test("should not be valid json") {
      withJsonTestFile("""{notvalid}""").shouldNotBeValidJson()
      withJsonTestFile("""{notvalid}""").toPath().shouldNotBeValidJson()
   }


   test("should be json array") {
      withJsonTestFile("""["abc","def"]""").shouldBeJsonArray()
      withJsonTestFile("""["abc","def"]""").toPath().shouldBeJsonArray()
      withJsonTestFile("""[1,2,3]""").shouldBeJsonArray()
      withJsonTestFile("""[1,2,3]""").toPath().shouldBeJsonArray()
      withJsonTestFile("""[{"key":"value"},{"key2":"value2"}]""").shouldBeJsonArray()
      withJsonTestFile("""[{"key":"value"},{"key2":"value2"}]""").toPath().shouldBeJsonArray()
   }

   test("should not be JsonArray") {
      withJsonTestFile("""{"key":"value"}""").shouldNotBeJsonArray()
      withJsonTestFile("""{"key":"value"}""").toPath().shouldNotBeJsonArray()
      withJsonTestFile("""{"array":["value", "value2"]}""").shouldNotBeJsonArray()
      withJsonTestFile("""{"array":["value", "value2"]}""").toPath().shouldNotBeJsonArray()
   }

   test("should be JsonObject") {
      withJsonTestFile("""{"key":"value"}""").shouldBeJsonObject()
      withJsonTestFile("""{"key":"value"}""").toPath().shouldBeJsonObject()
      withJsonTestFile("""{"array":["value", "value2"]}""").shouldBeJsonObject()
      withJsonTestFile("""{"array":["value", "value2"]}""").toPath().shouldBeJsonObject()

   }

   test("should not be JsonObject") {
      withJsonTestFile("""["abc","def"]""").shouldNotBeJsonObject()
      withJsonTestFile("""["abc","def"]""").toPath().shouldNotBeJsonObject()
      withJsonTestFile("""[1,2,3]""").shouldNotBeJsonObject()
      withJsonTestFile("""[1,2,3]""").toPath().shouldNotBeJsonObject()
      withJsonTestFile("""[{"key":"value"},{"key2":"value2"}]""").shouldNotBeJsonObject()
      withJsonTestFile("""[{"key":"value"},{"key2":"value2"}]""").toPath().shouldNotBeJsonObject()
   }
})
