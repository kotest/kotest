package com.sksamuel.kotest.tests.json

import io.kotest.assertions.json.JsonNode
import io.kotest.assertions.json.JsonTree
import io.kotest.assertions.json.toJsonTree
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.sequences.shouldContainAll
import kotlinx.serialization.json.Json
import org.intellij.lang.annotations.Language

class JsonTreeTest : FunSpec(
   {
      fun parseToTree(@Language("JSON") raw: String): JsonTree = toJsonTree(Json.parseToJsonElement(raw))

      test("Iterate across JsonTree") {
         val tree = parseToTree(
            """
            {
              "name": "Emil",
              "location": "Stockholm",
              "geo": {
                "lat": 59.334591,
                "long": 18.063240
              },
              "hobbies": [
                 "kotest",
                 "boardgames"
               ]
            }
            """
         )

         val lst = tree.iterator().asSequence().toList()

         lst.shouldContainExactly(
            "$.name" to JsonNode.StringNode("Emil"),
            "$.location" to JsonNode.StringNode("Stockholm"),
            "$.geo.lat" to JsonNode.NumberNode("59.334591"),
            "$.geo.long" to JsonNode.NumberNode("18.063240"),
            "$.hobbies[0]" to JsonNode.StringNode("kotest"),
            "$.hobbies[1]" to JsonNode.StringNode("boardgames"),
         )
      }
   }
)
