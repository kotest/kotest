package io.kotest.assertions.json

private const val indent = "  "

internal fun prettyPrint(node: JsonNode, depth: Int = 0): String {
   return when (node) {
      is JsonNode.ArrayNode -> prettyPrintElements(depth, "[", "]", node.elements) { child ->
         indent.repeat(depth + 1) + prettyPrint(child, depth = depth + 1)
      }

      is JsonNode.ObjectNode -> prettyPrintElements(depth, "{", "}", node.elements.entries) {
         indent.repeat(depth + 1) + "\"${it.key}\": ${prettyPrint(it.value, depth = depth + 1)}"
      }

      is JsonNode.BooleanNode -> node.value.toString()
      JsonNode.NullNode -> "null"
      is JsonNode.NumberNode -> node.content
      is JsonNode.StringNode -> "\"${node.value.replace("\"", "\\\"")}\""
   }
}

private fun <S, T : Collection<S>> prettyPrintElements(
   depth: Int,
   prefix: String,
   suffix: String,
   elements: T,
   transform: (S) -> CharSequence
) = buildString {
   if (elements.isEmpty()) append(prefix + suffix)
   else {
      appendLine(prefix)

      appendLine(elements.joinToString(",\n", transform = transform))

      append(indent.repeat(depth) + suffix)
   }
}
