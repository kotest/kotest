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

   sealed interface LiteralNode {

      val content: String
      val isString: Boolean
   }

   data class BooleanNode(val value: Boolean) : JsonNode(), LiteralNode {

      override val content = value.toString()
      override val isString = false
   }

   data class StringNode(val value: String) : JsonNode(), LiteralNode {

      companion object {

         private val numberRegex = """-?([1-9]\d*|0)(\.\d+)?([eE][+-]?\d+)?""".toRegex()
      }

      override val content = value
      override val isString = true

      internal fun contentIsNumber() = value.matches(numberRegex)
      internal fun toNumberNode() = NumberNode(content)
   }

   data class NumberNode(override val content: String) : JsonNode(), LiteralNode {

      companion object {
         private val exponentRegex = """.+[eE][+-]?\d+""".toRegex()
      }

      fun asString() = if (content.matches(exponentRegex)) content.toDouble().toString() else content
      override val isString = false
   }

   object NullNode : JsonNode(), LiteralNode {

      override val content = "null"
      override val isString = false
   }
}
