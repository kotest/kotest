package com.sksamuel.kotest.tests.json

import io.kotest.assertions.json.file.shouldBeJsonObject
import io.kotest.assertions.json.file.shouldBeValidJson
import io.kotest.assertions.json.file.shouldEqualJson
import io.kotest.assertions.json.paths.shouldBeJsonObject
import io.kotest.assertions.json.paths.shouldBeValidJson
import io.kotest.assertions.json.paths.shouldEqualJson
import io.kotest.assertions.json.shouldMatchJsonResource
import io.kotest.assertions.json.shouldNotMatchJsonResource
import io.kotest.core.spec.style.FunSpec
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
class CustomJsonParserFileTest : FunSpec({

   val withTrailingComma = Json {
      allowTrailingComma = true
      prettyPrint = true
      prettyPrintIndent = "  "
   }

   test("File - valid json with trailing comma") {
      withJsonTestFile("""{"name": "sam", "age": 25,}""").shouldBeValidJson(withTrailingComma)
   }

   test("Path - valid json with trailing comma") {
      withJsonTestFile("""{"name": "sam", "age": 25,}""").toPath().shouldBeValidJson(withTrailingComma)
   }

   test("File - json object with trailing comma") {
      withJsonTestFile("""{"name": "sam",}""").shouldBeJsonObject(withTrailingComma)
   }

   test("Path - json object with trailing comma") {
      withJsonTestFile("""{"name": "sam",}""").toPath().shouldBeJsonObject(withTrailingComma)
   }

   test("File - equal json with custom parser") {
      withJsonTestFile("""{"name": "sam", "age": 25,}""")
         .shouldEqualJson("""{"age": 25, "name": "sam"}""", withTrailingComma)
   }

   test("Path - equal json with custom parser") {
      withJsonTestFile("""{"name": "sam", "age": 25,}""").toPath()
         .shouldEqualJson("""{"age": 25, "name": "sam"}""", withTrailingComma)
   }

   test("match json resource with custom parser") {
      """{"name": "sam", "age": 25}""".shouldMatchJsonResource("/lenient.json", withTrailingComma)
   }

   test("should not match json resource with custom parser") {
      """{"name": "sam", "age": 30}""".shouldNotMatchJsonResource("/lenient.json", withTrailingComma)
   }
})
