package io.kotest.assertions.json

import io.kotest.assertions.json.shouldBeEmptyJsonArray
import io.kotest.assertions.json.shouldBeEmptyJsonObject
import io.kotest.core.spec.style.FunSpec

class EmptyJsonTests : FunSpec() {
   init {
      test("should match empty array") {
         "[]".shouldBeEmptyJsonArray()
         "   [  ]  ".shouldBeEmptyJsonArray()
         "[\n] ".shouldBeEmptyJsonArray()
      }
      test("should match empty object") {
         "{}".shouldBeEmptyJsonObject()
         "    {    } ".shouldBeEmptyJsonObject()
         "  {\n\n } ".shouldBeEmptyJsonObject()
      }
   }
}
