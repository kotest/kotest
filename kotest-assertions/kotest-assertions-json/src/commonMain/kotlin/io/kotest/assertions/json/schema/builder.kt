package io.kotest.assertions.json.schema

import io.kotest.matchers.Matcher
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlin.math.min

@DslMarker
annotation class JsonSchemaMarker

enum class JsonType {
   Array,
   Object,
   Boolean,
   Decimal,
   Integer,
   String,
   Null,
}

@JsonSchemaMarker
sealed interface JsonSchemaElement<in T> {
   /**
    * TODO: Matcher to match this element against when testing schema. Will allow for advanced schema definitions.
    */
   val matcher: Matcher<T>?
      get() = null

   fun name(): String
}

/**
 * Delegates element implementation to root to make itself composable, e.g. you can define a schema in multiple steps:
 * ```kotlin
 * val addressSchema = jsonSchema {
 *   obj {
 *     withProperty("street") { string() }
 *     withProperty("zipCode") { integer { beEven() } }
 *   }
 * }
 *
 * val personSchema = jsonSchema { obj { withProperty("name") { string() } }
 * ```
 */
data class JsonSchema<in T>(
   val root: JsonSchemaElement<T>
) {

   object Builder : JsonSchemaElement<Nothing> {
      override fun name() = "JsonSchema.Builder"
   }

   class JsonArray<S, T : JsonSchemaElement<S>>(val elementType: T) : JsonSchemaElement<T> {
      override fun name() = "array"
   }

   class JsonObject : JsonSchemaElement<JsonObject> {
      private var properties: MutableMap<String, JsonSchemaElement<*>> = mutableMapOf()

      fun withProperty(name: String, elementBuilder: JsonSchema.Builder.() -> JsonSchemaElement<*>) {
         properties[name] = JsonSchema.Builder.elementBuilder()
      }

      operator fun get(name: String) = properties.get(name)

      override fun name() = "object"
   }

   class JsonString(override val matcher: Matcher<String>?) : JsonSchemaElement<String> {
      override fun name() = "string"
   }

   class JsonInteger(override val matcher: Matcher<Int>?) : JsonSchemaElement<Int> {
      override fun name() = "integer"
   }

   object JsonDecimal: JsonSchemaElement<Double> {
      override fun name() = "decimal"
   }

   object JsonBoolean : JsonSchemaElement<Boolean> {
      override fun name() = "boolean"
   }

   object Null : JsonSchemaElement<Nothing> {
      override fun name() = "null"
   }

}

internal class JsonSchemaException(val path: String, message: String) : AssertionError(message)

private val specialChars = """.\["""
private val propertyAccessRegex = """^\.[^$specialChars]+""".toRegex()

internal operator fun JsonSchemaElement<*>.get(path: String): JsonSchemaElement<*> =
   when (this) {
      is JsonSchema.JsonArray<*, *> -> if (path.matches("""^\[\d+\].*""".toRegex())) this.elementType[path.substringAfter(']')] else throw JsonSchemaException(
         path,
         "Found unexpected array"
      )
      JsonSchema.Builder -> error("should never occur.. JsonSchema.Builder is just a marker token for Schema DSL")
      JsonSchema.JsonBoolean -> if (path == "") this else throw JsonSchemaException(
         path,
         "Found boolean, but expected to find further objects or arrays"
      )
      is JsonSchema.JsonDecimal -> if (path == "") this else throw JsonSchemaException(
         path,
         "Found decimal, but expected to find further objects or arrays"
      )
      is JsonSchema.JsonInteger -> if (path == "") this else throw JsonSchemaException(
         path,
         "Found decimal, but expected to find further objects or arrays"
      )
      is JsonSchema.JsonObject -> when {
         path == "" -> this
         path.startsWith(".") -> {
            val endIndex = path.indexOfAny(charArrayOf('.', '['), startIndex = 1).let { if (it == -1) path.length else it }
            val property = path.substring(1, endIndex)
            val elementForProperty = this[property] ?: throw JsonSchemaException(path, "Tried to find property $property but it is missing")
            elementForProperty[path.replaceFirst(".$property", "")]
         }
         else -> throw JsonSchemaException(path, "Unexpected path")
      }

      is JsonSchema.JsonString -> if (path == "") this else throw JsonSchemaException(
         path,
         "Found string, but expected to find further objects or arrays"
      )
      JsonSchema.Null -> if (path == "") this else TODO()
   }

fun JsonSchema.Builder.string(matcherBuilder: () -> Matcher<String>? = { null }) =
   JsonSchema.JsonString(matcherBuilder())

fun JsonSchema.Builder.integer(matcherBuilder: () -> Matcher<Int>? = { null }) =
   JsonSchema.JsonInteger(matcherBuilder())

fun JsonSchema.Builder.decimal() = JsonSchema.JsonDecimal

fun JsonSchema.Builder.obj(dsl: JsonSchema.JsonObject.() -> Unit = {}) =
   JsonSchema.JsonObject().apply(dsl)

fun JsonSchema.Builder.boolean() = JsonSchema.JsonBoolean

fun JsonSchema.Builder.array(
   typeBuilder: () -> JsonSchemaElement<*>
) = JsonSchema.JsonArray(typeBuilder())

fun <T> jsonSchema(rootBuilder: JsonSchema.Builder.() -> JsonSchemaElement<T>): JsonSchema<T> =
   JsonSchema(JsonSchema.Builder.rootBuilder())

/**
 * Parses a subset of JSON Schema into [JsonSchemaElement] which can be used to verify a json document with
 * [shouldMatchSchema]
 */
fun <T> parseSchema(jsonSchema: String): JsonSchema<T> =
   TODO("Not implemented yet")
