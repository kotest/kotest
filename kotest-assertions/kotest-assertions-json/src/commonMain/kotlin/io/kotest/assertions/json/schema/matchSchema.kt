package io.kotest.assertions.json.schema

import io.kotest.assertions.json.JsonNode
import io.kotest.assertions.json.toJsonTree
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlinx.serialization.json.Json

class SchemaViolation(
   val path: String,
   message: String,
   cause: Throwable? = null
) : RuntimeException(message, cause)

infix fun String.shouldNotMatchSchema(schema: JsonSchema<*>) = this shouldNot matchSchema(schema)
infix fun String.shouldMatchSchema(schema: JsonSchema<*>) = this should matchSchema(schema)

private fun isCompatible(actual: JsonNode, schema: JsonSchemaElement<*>) =
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

fun matchSchema(schema: JsonSchema<*>) = object : Matcher<String?> {
   override fun test(value: String?): MatcherResult {
      // TODO: Improve nullability handling..
      if (value == null) return MatcherResult(
         false,
         { "expected null to match schema: " },
         { "expected not to match schema, but null matched JsonNull schema" }
      )

      val parsed = runCatching {
         Json.parseToJsonElement(value)
      }

      if (parsed.isFailure) return MatcherResult(
         false,
         { "Failed to parse actual as JSON: ${parsed.exceptionOrNull()?.message}" },
         { "Failed to parse actual as JSON: ${parsed.exceptionOrNull()?.message}" },
      )

      val violations = mutableListOf<SchemaViolation>()
      val visitedNodes = mutableSetOf<JsonSchemaElement<*>>()
      val tree = toJsonTree(parsed.getOrThrow())

      for ((path, actual) in tree) {
         try {

            /** The JsonSchemaElement at [path], or [null] if undefined in schema */
            val expected = schema.root[path.replace("$", "")]

            if (expected == null) {
               if (!schema.allowExtraProperties) {
                  violations.add(
                     SchemaViolation(path, "Key undefined in schema, and schema is set to disallow extra keys")
                  )
               }
            } else {
               visitedNodes.add(expected)
               if (!isCompatible(actual, expected))
                  violations.add(SchemaViolation(path, "Expected ${expected.typeName()}, but was ${actual.numberAwareTypeName()}"))
            }
         } catch (e: JsonSchemaException) {
            violations.add(SchemaViolation(path, e.message ?: ""))
         }
      }

      schema.root.iterator().asSequence()
         .filterNot { it.second in visitedNodes }
         .forEach { (path, element) ->
            violations.add(SchemaViolation(path, "Expected ${element.typeName()}, but was undefined"))
         }

      return MatcherResult(
         violations.isEmpty(),
         { violations.joinToString(separator = "\n") { "${it.path} => ${it.message}" } },
         { "Expected some violation against JSON schema, but everything matched" }
      )
   }
}
