package io.kotest.assertions.json.schema

import io.kotest.matchers.Matcher
import kotlinx.serialization.Contextual
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
   operator fun invoke() = root

   object Builder

   @Serializable
   data class JsonArray(val elementType: JsonSchemaElement) : JsonSchemaElement {
      override fun typeName() = "array"
   }

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

   @Serializable
   data class JsonString(override val matcher: Matcher<String>? = null) : JsonSchemaElement, ValueNode<String> {
      override fun typeName() = "string"
   }

   @Serializable
   data class JsonNumber(override val matcher: Matcher<@Contextual Number>?) : JsonSchemaElement, ValueNode<Number> {
      override fun typeName() = "number"
   }

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

fun JsonSchema.Builder.number(matcherBuilder: () -> Matcher<Number>? = { null }) =
   JsonSchema.JsonNumber(matcherBuilder())

fun JsonSchema.Builder.obj(dsl: JsonSchema.JsonObject.() -> Unit = {}) =
   JsonSchema.JsonObject().apply(dsl)

fun JsonSchema.Builder.boolean() =
   JsonSchema.JsonBoolean

fun JsonSchema.Builder.array(typeBuilder: () -> JsonSchemaElement) =
   JsonSchema.JsonArray(typeBuilder())

fun jsonSchema(
   allowExtraProperties: Boolean = false,
   rootBuilder: JsonSchema.Builder.() -> JsonSchemaElement
): JsonSchema =
   JsonSchema(
      allowExtraProperties,
      JsonSchema.Builder.rootBuilder()
   )
