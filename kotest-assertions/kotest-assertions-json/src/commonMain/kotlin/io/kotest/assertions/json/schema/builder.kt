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
   val root: JsonSchemaElement
) {
   operator fun invoke() = root

   object Builder

   @Serializable
   data class JsonArray(val elementType: JsonSchemaElement) : JsonSchemaElement {
      override fun typeName() = "array"
   }

   class JsonObjectBuilder {
      var additionalProperties: Boolean = true
      var minProperties: Int = 0
      var maxProperties: Int? = null
      var properties: MutableMap<String, JsonSchemaElement> = mutableMapOf()

      /**
       * https://json-schema.org/understanding-json-schema/reference/object.html#required-properties
       */
      var requiredProperties: MutableList<String> = mutableListOf()

      /**
       * By default, properties are not required, however by setting [required] to true you can specify that it must be
       * included
       */
      fun withProperty(
         name: String,
         required: Boolean = false,
         elementBuilder: JsonSchema.Builder.() -> JsonSchemaElement
      ) {
         properties[name] = JsonSchema.Builder.elementBuilder()
         if (required) requiredProperties.add(name)
      }

      fun build() = JsonObject(
         additionalProperties = additionalProperties,
         minProperties = minProperties,
         maxProperties = maxProperties,
         properties = properties,
         requiredProperties = requiredProperties.toTypedArray()
      )
   }

   @Serializable
   data class JsonObject(
      /**
       * Controls whether this node allows additional properties to be defined or not.
       * By default, additional properties are _allowed_
       *
       * https://json-schema.org/understanding-json-schema/reference/object.html#additional-properties
       */
      val additionalProperties: Boolean = true,
      val minProperties: Int = 0,
      val maxProperties: Int? = null,
      val properties: Map<String, JsonSchemaElement>,

      /**
       * https://json-schema.org/understanding-json-schema/reference/object.html#required-properties
       */
      val requiredProperties: Array<String> = emptyArray(),
   ) : JsonSchemaElement {
      operator fun get(name: String) = properties.get(name)
      override fun typeName() = "object"
   }

   @Serializable
   data class JsonString(override val matcher: Matcher<String>? = null) : JsonSchemaElement, ValueNode<String> {
      override fun typeName() = "string"
   }

   @Serializable
   data class JsonNumber(override val matcher: Matcher<@Contextual Double>?) : JsonSchemaElement, ValueNode<Double> {
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

fun JsonSchema.Builder.number(matcherBuilder: () -> Matcher<Double>? = { null }) =
   JsonSchema.JsonNumber(matcherBuilder())

fun JsonSchema.Builder.obj(dsl: JsonSchema.JsonObjectBuilder.() -> Unit = {}) =
   JsonSchema.JsonObjectBuilder().apply(dsl).build()

fun JsonSchema.Builder.boolean() =
   JsonSchema.JsonBoolean

fun JsonSchema.Builder.array(typeBuilder: () -> JsonSchemaElement) =
   JsonSchema.JsonArray(typeBuilder())

fun jsonSchema(
   rootBuilder: JsonSchema.Builder.() -> JsonSchemaElement
): JsonSchema =
   JsonSchema(
      JsonSchema.Builder.rootBuilder()
   )
