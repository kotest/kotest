package com.sksamuel.kotest.tests.json

import io.kotest.assertions.json.shouldBeEmptyJsonArray
import io.kotest.assertions.json.shouldBeEmptyJsonObject
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempfile

class EmptyJsonFileTests : FunSpec({
   test("should match empty array") {
      withJsonTestFile("[]").shouldBeEmptyJsonArray()
      withJsonTestFile("[]").toPath().shouldBeEmptyJsonArray()
      withJsonTestFile("   [  ]  ").shouldBeEmptyJsonArray()
      withJsonTestFile("   [  ]  ").toPath().shouldBeEmptyJsonArray()
      withJsonTestFile("[\n] ").shouldBeEmptyJsonArray()
      withJsonTestFile("[\n] ").shouldBeEmptyJsonArray()
   }
   test("should match empty object") {
      withJsonTestFile("{}").shouldBeEmptyJsonObject()
      withJsonTestFile("{}").toPath().shouldBeEmptyJsonObject()
      withJsonTestFile("    {    } ").shouldBeEmptyJsonObject()
      withJsonTestFile("    {    } ").toPath().shouldBeEmptyJsonObject()
      withJsonTestFile("  {\n\n } ").shouldBeEmptyJsonObject()
      withJsonTestFile("  {\n\n } ").toPath().shouldBeEmptyJsonObject()
   }
})
