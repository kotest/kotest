package io.kotest.assertions.json

sealed class JsonNode {

   fun type() = when (this) {
      is ObjectNode -> "object"
      is ArrayNode -> "array"
      is BooleanNode -> "boolean"
      is StringNode -> "string"
      is NumberNode -> "number"
      NullNode -> "null"
   }

   data class ObjectNode(val elements: Map<String, JsonNode>) : JsonNode()

   data class ArrayNode(val elements: List<JsonNode>) : JsonNode()

   interface ValueNode

   data class BooleanNode(val value: Boolean) : JsonNode(), ValueNode

   data class StringNode(val value: String) : JsonNode(), ValueNode {

      companion object {

         private val numberRegex = """-?([1-9]\d*|0)(\.\d+)?([eE][+-]?\d+)?""".toRegex()
      }

      internal fun contentIsNumber() = value.matches(numberRegex)
      internal fun toNumberNode() = NumberNode(value)
   }

   data class NumberNode(val content: String) : JsonNode(), ValueNode {

      companion object {

         private val exponentRegex = """.+[eE][+-]?\d+""".toRegex()
      }

      fun asString() = if (content.matches(exponentRegex)) content.toDouble().toString() else content
   }

   object NullNode : JsonNode(), ValueNode
}

internal fun show(node: JsonNode): String {
   return when (node) {
      is JsonNode.ArrayNode -> "[${node.elements.joinToString(separator = ",") { show(it) }}]"
      is JsonNode.BooleanNode -> node.value.toString()
      JsonNode.NullNode -> "null"
      is JsonNode.NumberNode -> node.content
      is JsonNode.ObjectNode -> "{${node.elements.map { "\"${it.key}\": ${show(it.value)}" }.joinToString(separator = ",")}}"
      is JsonNode.StringNode -> node.value
   }
}
