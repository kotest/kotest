package io.kotest.assertions.json.schema

import io.kotest.assertions.json.JsonNode
import io.kotest.assertions.json.JsonTree
import io.kotest.assertions.json.toJsonTree
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.and
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

infix fun String?.shouldMatchSchema(schema: JsonSchema) =
   this should parseToJson.and(matchSchema(schema).contramap<String?> { it?.let(Json::parseToJsonElement) })

infix fun String?.shouldNotMatchSchema(schema: JsonSchema) =
   this shouldNot parseToJson.and(matchSchema(schema).contramap<String?> { it?.let(Json::parseToJsonElement) })

infix fun JsonElement.shouldMatchSchema(schema: JsonSchema) = this should matchSchema(schema)
infix fun JsonElement.shouldNotMatchSchema(schema: JsonSchema) = this shouldNot matchSchema(schema)

val parseToJson = object : Matcher<String?> {
   override fun test(value: String?): MatcherResult {
      if (value == null) return MatcherResult(
         false,
         { "expected null to match schema: " },
         { "expected not to match schema, but null matched JsonNull schema" }
      )

      val parsed = runCatching {
         Json.parseToJsonElement(value)
      }

      return MatcherResult(
         parsed.isSuccess,
         { "Failed to parse actual as JSON: ${parsed.exceptionOrNull()?.message}" },
         { "Failed to parse actual as JSON: ${parsed.exceptionOrNull()?.message}" },
      )
   }
}

fun matchSchema(schema: JsonSchema) = object : Matcher<JsonElement?> {
   override fun test(value: JsonElement?): MatcherResult {
      if (value == null) return MatcherResult(
         false,
         { "expected null to match schema: " },
         { "expected not to match schema, but null matched JsonNull schema" }
      )

      val visitedNodes = mutableSetOf<String>()
      val tree = toJsonTree(value)
      val violations = validate("$", tree.root, schema.root, schema.allowExtraProperties)

      return MatcherResult(
         violations.isEmpty(),
         { violations.joinToString(separator = "\n") { "${it.path} => ${it.message}" } },
         { "Expected some violation against JSON schema, but everything matched" }
      )
   }
}

private fun validate(
   currentPath: String,
   tree: JsonNode,
   expected: JsonSchemaElement,
   allowExtraKeys: Boolean
): List<SchemaViolation> {
   fun propertyViolation(propertyName: String, message: String) =
      listOf(SchemaViolation("$currentPath.$propertyName", message))

   fun violation(message: String) =
      listOf(SchemaViolation(currentPath, message))

   return when (tree) {
      is JsonNode.ArrayNode -> {
         if (expected is JsonSchema.JsonArray)
            tree.elements.flatMapIndexed { i, node ->
               validate("$currentPath[$i]", node, expected.elementType, allowExtraKeys)
            }
         else violation("Expected ${expected.typeName()}, but was array")
      }
      is JsonNode.ObjectNode -> {
         if (expected is JsonSchema.JsonObject) {
            val extraKeyViolations =
               if (!allowExtraKeys)
                  tree.elements.keys
                     .filterNot { it in expected.properties.keys }
                     .flatMap {
                        propertyViolation(it, "Key undefined in schema, and schema is set to disallow extra keys")
                     }
               else
                  emptyList<SchemaViolation>()

            extraKeyViolations + expected.properties.flatMap { (propertyName, schema) ->
               val actual = tree.elements[propertyName]

               if (actual == null)
                  propertyViolation(propertyName, "Expected ${schema.typeName()}, but was undefined")
               else
                  validate("$currentPath.$propertyName", actual, schema, allowExtraKeys)
            }
         } else violation("Expected ${expected.typeName()}, but was object")
      }

      is JsonNode.NullNode -> TODO()
      is JsonNode.BooleanNode,
      is JsonNode.NumberNode,
      is JsonNode.StringNode ->
         if (!isCompatible(tree, expected))
            violation("Expected ${expected.typeName()}, but was ${tree.numberAwareTypeName()}")
         else emptyList()
   }
}

private class SchemaViolation(
   val path: String,
   message: String,
   cause: Throwable? = null
) : RuntimeException(message, cause)

private fun isCompatible(actual: JsonNode, schema: JsonSchemaElement) =
   (actual is JsonNode.BooleanNode && schema is JsonSchema.JsonBoolean) ||
      (actual is JsonNode.StringNode && schema is JsonSchema.JsonString) ||
      (actual is JsonNode.NumberNode && actual.content.contains(".") && schema is JsonSchema.JsonDecimal) ||
      (actual is JsonNode.NumberNode && !actual.content.contains(".") && schema is JsonSchema.JsonInteger)

/**
 * Expands upon [JsonNode.type] and adds the ability of differentiating between integer and decimal numbers
 */
private fun JsonNode.numberAwareTypeName() =
   when (this) {
      is JsonNode.NumberNode -> {
         if (this.content.contains(".")) "decimal"
         else "integer"
      }
      else -> this.type()
   }

