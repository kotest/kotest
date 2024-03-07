package io.kotest.assertions.json

import kotlinx.serialization.Serializable

@Serializable
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

      fun lenientEquals(other: NumberNode): Boolean {

         if (other.content == content) return true

         // if one or the other is exponent notation, we must compare by parsed value
         if (content.matches(exponentRegex) xor other.content.matches(exponentRegex)) {
            return content.toDouble() == other.content.toDouble()
         }

         val fractionalZeroesRegex = """(\.\d*)0+$""".toRegex()
         /**
          * Removes insignificant part of a number. e.g. 1.0 -> 1 or 3.1400 -> 3.14
          */
         fun trimInsignificant(value: String): String =
            value.replace(fractionalZeroesRegex) { it.groupValues[1].trimEnd('0') }
               .trimEnd('.')

         return trimInsignificant(content) == trimInsignificant(other.content)

      }

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
