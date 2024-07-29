package io.kotest.assertions.json

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.longOrNull

fun JsonElement.toJsonNode(): JsonNode = when (this) {
   JsonNull -> JsonNode.NullNode
   is JsonObject -> JsonNode.ObjectNode(entries.associate { it.key to it.value.toJsonNode() })
   is JsonArray -> JsonNode.ArrayNode(map { it.toJsonNode() })
   is JsonPrimitive -> when {
      isString -> JsonNode.StringNode(content)
      booleanOrNull != null -> JsonNode.BooleanNode(boolean)
      else -> {
         requireNotNull(doubleOrNull ?: longOrNull ?: intOrNull ?: floatOrNull) {
            "Unsupported kotlinx-serialization type $this"
         }

         JsonNode.NumberNode(content)
      }
   }
}
