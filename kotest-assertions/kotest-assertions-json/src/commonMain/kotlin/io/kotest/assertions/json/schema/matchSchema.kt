package io.kotest.assertions.json.schema

import io.kotest.assertions.json.JsonNode
import io.kotest.assertions.json.JsonTree
import io.kotest.assertions.json.toJsonTree
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

class SchemaViolation(
   val path: String,
   message: String,
   cause: Throwable? = null
) : RuntimeException(message, cause)

infix fun String.shouldNotMatchSchema(schema: JsonSchema<*>) = this shouldNot matchSchema(schema)
infix fun String.shouldMatchSchema(schema: JsonSchema<*>) = this should matchSchema(schema)

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

            val expected = schema.root[path.replace("$", "")]

            if (expected == null) {
               if (!schema.allowExtraProperties) {
                  violations.add(
                     SchemaViolation(
                        path,
                        "Key undefined in schema, and schema is set to disallow extra keys"
                     )
                  )
               }
            } else {
               visitedNodes.add(expected)

               when (actual) {
                  is JsonNode.NumberNode -> {
                     if (actual.content.contains(".")) {
                        if (expected !is JsonSchema.JsonDecimal) {
                           violations.add(SchemaViolation(path, "Expected ${expected.name()}, but was decimal"))
                        }
                     } else {
                        if (expected !is JsonSchema.JsonInteger) {
                           violations.add(SchemaViolation(path, "Expected ${expected.name()}, but was integer"))
                        }
                     }
                  }

                  is JsonNode.BooleanNode -> {
                     if (expected !is JsonSchema.JsonBoolean) {
                        violations.add(SchemaViolation(path, "Expected ${expected.name()}, but was boolean"))
                     }
                  }

                  is JsonNode.StringNode -> {
                     if (expected !is JsonSchema.JsonString) {
                        violations.add(SchemaViolation(path, "Expected ${expected.name()}, but was string"))
                     }
                  }
               }
            }
         } catch (e: JsonSchemaException) {
            violations.add(SchemaViolation(e.path, e.message ?: ""))
         }
      }

      schema.root.iterator().asSequence()
         .filterNot { it.second in visitedNodes }
         .forEach { (path, element) ->
            violations.add(SchemaViolation(path, "Expected ${element.name()}, but was undefined"))
         }

      return MatcherResult(
         violations.isEmpty(),
         { violations.joinToString(separator = "\n") { "${it.path} => ${it.message}" } },
         { "Expected some violation against JSON schema, but everything matched" }
      )
   }
}
