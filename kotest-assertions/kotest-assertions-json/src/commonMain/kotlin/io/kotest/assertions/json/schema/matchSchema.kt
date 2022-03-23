package io.kotest.assertions.json.schema

import io.kotest.assertions.json.JsonNode
import io.kotest.assertions.json.JsonTree
import io.kotest.assertions.json.pretty
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
): RuntimeException(message, cause)

infix fun String.shouldNotMatchSchema(schema: JsonSchema<*>) = this shouldNot matchSchema(schema.root)
infix fun String.shouldMatchSchema(schema: JsonSchema<*>) = this should matchSchema(schema.root)

fun matchSchema(schema: JsonSchemaElement<*>) = object : Matcher<String?> {
   override fun test(value: String?): MatcherResult {
      if (value == null) return MatcherResult(
         schema is JsonSchema.Null,
         { "expected null to match schema: " },
         { "expected not to match schema, but null matched JsonNull schema" }
      )


      val violations = mutableListOf<SchemaViolation>()
      val visitedSchemaElements = mutableSetOf<JsonSchemaElement<*>>()

      try {
         val tree = toJsonTree(Json.parseToJsonElement(value))

         for ((path, node) in tree) {
            val schemaForPath = try { schema[path.replace("$", "")] } catch (e: JsonSchemaException) {
               throw SchemaViolation(path, "${e.path} - ${e.message ?: ""}", e)
            }

            visitedSchemaElements.add(schemaForPath)

            when (node) {
               is JsonNode.NumberNode -> {
                  if (node.content.contains(".")) {
                     if (schemaForPath !is JsonSchema.JsonDecimal) {
                        violations.add(SchemaViolation(path, "Expected ${schemaForPath.name()} but was a decimal"))
                     }
                  } else {
                     if (schemaForPath !is JsonSchema.JsonInteger) {
                        violations.add(SchemaViolation(path, "Expected ${schemaForPath.name()} but was an integer"))
                     }
                  }
               }

               is JsonNode.BooleanNode -> {
                  if (schemaForPath !is JsonSchema.JsonBoolean) {
                     violations.add(SchemaViolation(path, "Expected ${schemaForPath.name()} but was a boolean"))
                  }
               }

               is JsonNode.StringNode -> {
                  if (schemaForPath !is JsonSchema.JsonString) {
                     violations.add(SchemaViolation(path, "Expected ${schemaForPath.name()} but was a string"))
                  }
               }
            }
         }

      } catch (e: SerializationException) {
         violations.add(SchemaViolation("$", "Tried to parse actual as JSON, but it failed"))
      } catch (e: SchemaViolation) {
         violations.add(e)
      }

//      schema.iterator()
//         .filterNot { it in visitedSchemaElements}
//         .forEach { (path, element) ->
//            violations.add(SchemaViolation(""))
//         }

      return MatcherResult(
         violations.isEmpty(),
         { violations.joinToString(separator = "\n") { "${it.path} => ${it.message}" } },
         { "Expected some violation against JSON schema, but everything matched" }
      )
   }
}
