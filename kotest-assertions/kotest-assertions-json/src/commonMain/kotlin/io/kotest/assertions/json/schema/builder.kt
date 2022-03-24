package io.kotest.assertions.json.schema

import io.kotest.matchers.Matcher
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

@DslMarker
annotation class JsonSchemaMarker

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
   val allowExtraProperties: Boolean = false,
   val root: JsonSchemaElement<T>
) {

   object Builder : JsonSchemaElement<Nothing> {
      override fun name() = "JsonSchema.Builder"
   }

   class JsonArray<S, T : JsonSchemaElement<S>>(val elementType: T) : JsonSchemaElement<T> {
      override fun name() = "array"
   }

   class JsonObject : JsonSchemaElement<JsonObject> {
      internal var properties: MutableMap<String, JsonSchemaElement<*>> = mutableMapOf()

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

   object JsonDecimal : JsonSchemaElement<Double> {
      override fun name() = "decimal"
   }

   object JsonBoolean : JsonSchemaElement<Boolean> {
      override fun name() = "boolean"
   }

   object Null : JsonSchemaElement<Nothing> {
      override fun name() = "null"
   }
}

fun JsonSchema.Builder.string(matcherBuilder: () -> Matcher<String>? = { null }) =
   JsonSchema.JsonString(matcherBuilder())

fun JsonSchema.Builder.integer(matcherBuilder: () -> Matcher<Int>? = { null }) =
   JsonSchema.JsonInteger(matcherBuilder())

fun JsonSchema.Builder.decimal() =
   JsonSchema.JsonDecimal

fun JsonSchema.Builder.obj(dsl: JsonSchema.JsonObject.() -> Unit = {}) =
   JsonSchema.JsonObject().apply(dsl)

fun JsonSchema.Builder.boolean() =
   JsonSchema.JsonBoolean

fun JsonSchema.Builder.array(typeBuilder: () -> JsonSchemaElement<*>) =
   JsonSchema.JsonArray(typeBuilder())

fun <T> jsonSchema(
   allowExtraProperties: Boolean = false,
   rootBuilder: JsonSchema.Builder.() -> JsonSchemaElement<T>
): JsonSchema<T> =
   JsonSchema(
      allowExtraProperties,
      JsonSchema.Builder.rootBuilder()
   )
