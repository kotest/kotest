package io.kotest.assertions.json.schema

import io.kotest.assertions.json.ContainsSpec
import io.kotest.assertions.json.JsonNode
import io.kotest.assertions.json.toJsonTree
import io.kotest.common.ExperimentalKotest
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.and
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@ExperimentalKotest
infix fun String?.shouldMatchSchema(schema: JsonSchema) =
   this should stringJsonMatcher(schema)

@ExperimentalKotest
infix fun String?.shouldNotMatchSchema(schema: JsonSchema) =
   this shouldNot stringJsonMatcher(schema)

@ExperimentalKotest
internal fun stringJsonMatcher(schema: JsonSchema): Matcher<String?> {
   return parseToJson.and(matchSchema(schema).contramap { it?.let(Json::parseToJsonElement) })
}

@ExperimentalKotest
infix fun JsonElement.shouldMatchSchema(schema: JsonSchema) = this should matchSchema(schema)

@ExperimentalKotest
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

@ExperimentalKotest
fun matchSchema(schema: JsonSchema) = object : Matcher<JsonElement?> {
   override fun test(value: JsonElement?): MatcherResult {
      if (value == null) return MatcherResult(
         false,
         { "expected null to match schema: " },
         { "expected not to match schema, but null matched JsonNull schema" }
      )

      val tree = toJsonTree(value)
      val violations = validate("$", tree.root, schema.root)

      return MatcherResult(
         violations.isEmpty(),
         { violations.joinToString(separator = "\n") { "${it.path} => ${it.message}" } },
         { "Expected some violation against JSON schema, but everything matched" }
      )
   }
}

@ExperimentalKotest
private fun validate(
   currentPath: String,
   tree: JsonNode,
   expected: JsonSchemaElement,
): List<SchemaViolation> {
   fun propertyViolation(propertyName: String, message: String) =
      listOf(SchemaViolation("$currentPath.$propertyName", message))

   fun violation(message: String) =
      listOf(SchemaViolation(currentPath, message))

   fun violationIf(conditionResult: Boolean, message: String) = if (conditionResult) violation(message) else emptyList()

   fun <T> violation(matcher: Matcher<T>?, value: T) = matcher?.let {
      val matcherResult = it.test(value)
      violationIf(!matcherResult.passed(), matcherResult.failureMessage())
   } ?: emptyList()

   fun ContainsSpec.violation(tree: JsonNode.ArrayNode): List<SchemaViolation> {
      val schemaViolations = tree.elements.mapIndexed { i, node ->
         validate("\t$currentPath[$i]", node, schema)
      }
      return when (val foundElements = schemaViolations.count { it.isEmpty() }) {
         0 -> violation("Expected some item to match contains-specification:") + schemaViolations.flatten()
         !in minContains..maxContains -> violation("Expected items of type ${schema.typeName()} between $minContains and $maxContains, but found $foundElements")
         else -> emptyList()
      }

   }

   fun JsonSchemaElement.violation(tree: JsonNode.ArrayNode): List<SchemaViolation> =
      tree.elements.flatMapIndexed { i, node ->
         validate("$currentPath[$i]", node, this)
      }

   return when (tree) {
      is JsonNode.ArrayNode -> {
         if (expected is JsonSchema.JsonArray) {
            val sizeViolation = violationIf(
               tree.elements.size < expected.minItems || tree.elements.size > expected.maxItems,
               "Expected items between ${expected.minItems} and ${expected.maxItems}, but was ${tree.elements.size}"
            )
            val matcherViolation = violation(expected.matcher, tree.elements.asSequence())
            val containsViolation = expected.contains?.violation(tree) ?: emptyList()
            val elementTypeViolation = expected.elementType?.violation(tree) ?: emptyList()
            matcherViolation + sizeViolation + containsViolation + elementTypeViolation
         } else violation("Expected ${expected.typeName()}, but was array")
      }

      is JsonNode.ObjectNode -> {
         if (expected is JsonSchema.JsonObject) {
            val extraKeyViolations =
               if (!expected.additionalProperties)
                  tree.elements.keys
                     .filterNot { it in expected.properties.keys }
                     .flatMap {
                        propertyViolation(it, "Key undefined in schema, and schema is set to disallow extra keys")
                     }
               else emptyList()

            extraKeyViolations + expected.properties.flatMap { (propertyName, schema) ->
               val actual = tree.elements[propertyName]

               if (actual == null) {
                  if (expected.requiredProperties.contains(propertyName)) {
                     propertyViolation(propertyName, "Expected ${schema.typeName()}, but was undefined")
                  } else {
                     emptyList()
                  }
               } else validate("$currentPath.$propertyName", actual, schema)
            }
         } else violation("Expected ${expected.typeName()}, but was object")
      }

      is JsonNode.NullNode -> {
         if (!isCompatible(tree, expected))
            violation("Expected ${expected.typeName()}, but was ${tree.type()}")
         else
            emptyList()
      }

      is JsonNode.NumberNode ->
         when (expected) {
            is JsonSchema.JsonInteger -> {
               if (tree.content.contains(".")) violation("Expected integer, but was number")
               else violation(expected.matcher, tree.content.toLong())
            }

            is JsonSchema.JsonDecimal -> {
               violation(expected.matcher, tree.content.toDouble())
            }

            else -> violation("Expected ${expected.typeName()}, but was ${tree.type()}")
         }

      is JsonNode.StringNode ->
         if (expected is JsonSchema.JsonString) {
            violation(expected.matcher, tree.value)
         } else violation("Expected ${expected.typeName()}, but was ${tree.type()}")

      is JsonNode.BooleanNode ->
         if (!isCompatible(tree, expected))
            violation("Expected ${expected.typeName()}, but was ${tree.type()}")
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
      (actual is JsonNode.NumberNode && schema is JsonSchema.JsonNumber) ||
      (actual is JsonNode.NullNode && schema is JsonSchema.Null)
