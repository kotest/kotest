package io.kotest.assertions.json

sealed class JsonNode {

   fun type() = when (this) {
      is ObjectNode -> "object"
      is ArrayNode -> "array"
      is BooleanNode -> "boolean"
      is StringNode -> "string"
      is LongNode -> "long"
      is DoubleNode -> "double"
      NullNode -> "null"
   }

   data class ObjectNode(val elements: Map<String, JsonNode>) : JsonNode()

   data class ArrayNode(val elements: List<JsonNode>) : JsonNode()

   interface ValueNode

   data class BooleanNode(val value: Boolean) : JsonNode(), ValueNode

   data class StringNode(val value: String) : JsonNode(), ValueNode

   data class LongNode(val value: Long) : JsonNode(), ValueNode

   data class DoubleNode(val value: Double) : JsonNode(), ValueNode

   object NullNode : JsonNode(), ValueNode
}
