@file:OptIn(ExperimentalSerializationApi::class)

package io.kotest.assertions.json.schema

import io.kotest.matchers.Matcher
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@DslMarker
annotation class JsonSchemaMarker

@JsonSchemaMarker
@Serializable(with = SchemaDeserializer::class)
sealed interface JsonSchemaElement {
   fun typeName(): String
}

interface ValueNode<T> {
   val matcher: Matcher<T>?
      get() = null
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
data class JsonSchema(
   val allowExtraProperties: Boolean = false,
   val root: JsonSchemaElement
) {

   object Builder : JsonSchemaElement {
      override fun typeName() = "JsonSchema.Builder"
   }

   @SerialName("array")
   @Serializable
   data class JsonArray(val elementType: JsonSchemaElement) : JsonSchemaElement {
      override fun typeName() = "array"
   }

   @SerialName("object")
   @Serializable
   data class JsonObject(
      val properties: MutableMap<
         String,
         JsonSchemaElement
      > = mutableMapOf()
   ) : JsonSchemaElement {

      fun withProperty(name: String, elementBuilder: JsonSchema.Builder.() -> JsonSchemaElement) {
         properties[name] = JsonSchema.Builder.elementBuilder()
      }

      operator fun get(name: String) = properties.get(name)

      override fun typeName() = "object"
   }

   @SerialName("string")
   @Serializable
   data class JsonString(override val matcher: Matcher<String>? = null) : JsonSchemaElement, ValueNode<String> {
      override fun typeName() = "string"
   }

   data class JsonInteger(override val matcher: Matcher<Int>? = null) : JsonSchemaElement, ValueNode<Int> {
      override fun typeName() = "integer"
   }

   @SerialName("number")
   @Serializable
   object JsonDecimal : JsonSchemaElement, ValueNode<Double> {
      override fun typeName() = "decimal"
   }

   @SerialName("boolean")
   @Serializable
   object JsonBoolean : JsonSchemaElement, ValueNode<Boolean> {
      override fun typeName() = "boolean"
   }

   object Null : JsonSchemaElement {
      override fun typeName() = "null"
   }
}

fun JsonSchema.Builder.string(matcherBuilder: () -> Matcher<String>? = { null }) =
   JsonSchema.JsonString(matcherBuilder())

fun JsonSchema.Builder.integer(matcherBuilder: () -> Matcher<Int>? = { null }) =
   JsonSchema.JsonInteger(matcherBuilder())

fun JsonSchema.Builder.decimal() =
   JsonSchema.JsonDecimal

fun JsonSchema.Builder.jsonObject(dsl: JsonSchema.JsonObject.() -> Unit = {}) =
   JsonSchema.JsonObject().apply(dsl)

fun JsonSchema.Builder.boolean() =
   JsonSchema.JsonBoolean

fun JsonSchema.Builder.jsonArray(typeBuilder: () -> JsonSchemaElement) =
   JsonSchema.JsonArray(typeBuilder())

fun jsonSchema(
   allowExtraProperties: Boolean = false,
   rootBuilder: JsonSchema.Builder.() -> JsonSchemaElement
): JsonSchema =
   JsonSchema(
      allowExtraProperties,
      JsonSchema.Builder.rootBuilder()
   )
