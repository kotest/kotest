package io.kotest.assertions.json.schema

import io.kotest.assertions.json.JsonNode
import io.kotest.assertions.json.JsonTree

internal class JsonSchemaException(val path: String, message: String) : AssertionError(message)


internal operator fun JsonSchemaElement.get(path: String): JsonSchemaElement? {
   fun valueNodeOrException() =
      when {
         path == "" -> this
         else -> null
      }

   return when (this) {
      is JsonSchema.JsonArray ->
         if (path.matches(arrayIndexRegex)) {
            this.elementType[path.substringAfter(']')]
         } else throw JsonSchemaException(path, "Found unexpected array")

      is JsonSchema.JsonObject -> when {
         path == "" -> this
         path.startsWith(".") -> {
            val endIndex =
               path.indexOfAny(charArrayOf('.', '['), startIndex = 1).let { if (it == -1) path.length else it }
            val property = path.substring(1, endIndex)
            this[property]?.get(path.replaceFirst(".$property", ""))
         }
         else -> throw JsonSchemaException(path, "Unexpected path")
      }

      JsonSchema.JsonBoolean -> valueNodeOrException()
      is JsonSchema.JsonDecimal -> valueNodeOrException()
      is JsonSchema.JsonInteger -> valueNodeOrException()
      is JsonSchema.JsonString -> valueNodeOrException()
      JsonSchema.Null -> TODO()
   }
}

internal operator fun JsonNode.get(path: String): JsonNode? =
   when (this) {
      is JsonNode.ArrayNode -> {
         val indexToGet = arrayIndexRegex.find(path)?.let { it -> it.groupValues[1].toInt() }
         indexToGet?.let(elements::get)
      }

      is JsonNode.ObjectNode -> {
         val endIndex =
            path.indexOfAny(charArrayOf('.', '['), startIndex = 1).let { if (it == -1) path.length else it }
         val property = path.substring(1, endIndex)
         elements[property]?.get(path.replaceFirst(".$property", ""))
      }

      is JsonNode.BooleanNode -> if (path == "") this else null
      JsonNode.NullNode -> if (path == "") this else null
      is JsonNode.NumberNode -> if (path == "") this else null
      is JsonNode.StringNode -> if (path == "") this else null
   }

private val arrayIndexRegex = """^\[(\d+)\].*""".toRegex()

internal operator fun JsonTree.iterator() = iterator<Pair<String, JsonNode>> {
   yieldAll(sequenceFor(node = root))
}

private fun sequenceFor(currentPath: String = "$", node: JsonNode): Sequence<Pair<String, JsonNode>> =
   sequence<Pair<String, JsonNode>> {
      when (node) {
         is JsonNode.ArrayNode -> node.elements.flatMapIndexed { i, child ->
            sequenceFor("$currentPath[$i]", child).toList()
         }.let { yieldAll(it) }

         is JsonNode.ObjectNode ->
            node.elements.flatMap { (key, child) ->
               sequenceFor("$currentPath.$key", child).toList()
            }.let { yieldAll(it) }

         is JsonNode.BooleanNode -> yield(currentPath to node)
         is JsonNode.NumberNode -> yield(currentPath to node)
         is JsonNode.StringNode -> yield(currentPath to node)
         JsonNode.NullNode -> yield(currentPath to JsonNode.NullNode)
      }
   }

internal operator fun JsonSchemaElement.iterator() = iterator<Pair<String, JsonSchemaElement>> {
   yieldAll(sequenceForSchema("$", node = this@iterator))
}

private fun sequenceForSchema(
   currentPath: String = "$",
   node: JsonSchemaElement
): Sequence<Pair<String, JsonSchemaElement>> =
   sequence {
      when (node) {
         is JsonSchema.JsonArray -> yieldAll(sequenceForSchema("$currentPath[]", node.elementType))

         is JsonSchema.JsonObject -> node.properties.flatMap { (name, element) ->
            sequenceForSchema("$currentPath.$name", element)
         }.let { yieldAll(it) }

         JsonSchema.JsonBoolean -> yield(currentPath to node)
         JsonSchema.JsonDecimal -> yield(currentPath to node)
         is JsonSchema.JsonInteger -> yield(currentPath to node)
         is JsonSchema.JsonString -> yield(currentPath to node)
         JsonSchema.Null -> TODO()
      }
   }
