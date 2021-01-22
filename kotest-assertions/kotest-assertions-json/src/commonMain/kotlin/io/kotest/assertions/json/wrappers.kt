package io.kotest.assertions.json

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.longOrNull

fun JsonElement.toJsonNode(): JsonNode = when (this) {
   JsonNull -> JsonNode.NullNode
   is JsonObject -> JsonNode.ObjectNode(entries.map { it.key to it.value.toJsonNode() }.toMap())
   is JsonArray -> JsonNode.ArrayNode(map { it.toJsonNode() })
   is JsonPrimitive -> when {
      intOrNull != null -> JsonNode.IntNode(intOrNull!!)
      longOrNull != null -> JsonNode.LongNode(longOrNull!!)
      doubleOrNull != null -> JsonNode.DoubleNode(doubleOrNull!!)
      floatOrNull != null -> JsonNode.FloatNode(floatOrNull!!)
      booleanOrNull != null -> JsonNode.BooleanNode(booleanOrNull!!)
      contentOrNull != null -> JsonNode.StringNode(contentOrNull!!)
      else -> error("Unsupported kotlinx-serialization type $this")
   }
}
