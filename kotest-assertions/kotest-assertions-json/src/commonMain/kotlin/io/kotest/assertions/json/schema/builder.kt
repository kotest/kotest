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

internal interface ValueNode<T> {
   val matcher: Matcher<T>?
      get() = null
}

data class JsonSchema(
   val root: JsonSchemaElement
) {

   /**
    * Provides a way to access pre-defined schemas in a conventional way (e.g. `type()`). Example:
    * ```kotlin
    * val addressSchema = jsonSchema {
    *   obj {
    *     withProperty("street") { string() }
    *     withProperty("zipCode") { integer { beEven() } }
    *   }
    * }
    *
    * val personSchema = jsonSchema {
    *   obj {
    *     withProperty("name") { string() }
    *     withProperty("address") { addressSchema() }
    *   }
    * }
    * ```
    */
   operator fun invoke() = root

   object Builder
   internal interface JsonNumber

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
       * By default properties are optional.
       * Using [required], you can specify that it must be included.
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

   data class JsonString(override val matcher: Matcher<String>? = null) : JsonSchemaElement, ValueNode<String> {
      override fun typeName() = "string"
   }

   data class JsonInteger(
      override val matcher: Matcher<Long>? = null
   ) : JsonSchemaElement, JsonNumber, ValueNode<Long> {
      override fun typeName() = "integer"
   }

   data class JsonDecimal(
      override val matcher: Matcher<Double>? = null
   ) : JsonSchemaElement, JsonNumber, ValueNode<Double> {
      override fun typeName() = "number"
   }

   @Serializable
   object JsonBoolean : JsonSchemaElement, ValueNode<Boolean> {
      override fun typeName() = "boolean"
   }

   @Serializable
   object Null : JsonSchemaElement {
      override fun typeName() = "null"
   }
}


/**
 * Creates a [JsonSchema.JsonString] node, which is a leaf node that will hold a [String] value.
 * Optionally, you can provide a [matcherBuilder] that constructs a [Matcher] which the node will be tested with.
 */
fun JsonSchema.Builder.string(matcherBuilder: () -> Matcher<String>? = { null }) =
   JsonSchema.JsonString(matcherBuilder())

/**
 * Creates a [JsonSchema.JsonInteger] node, which is a leaf node that will hold a [Long] value.
 * Optionally, you can provide a [matcherBuilder] that constructs a [Matcher] which the node will be tested with.
 */
fun JsonSchema.Builder.integer(matcherBuilder: () -> Matcher<Long>? = { null }) =
   JsonSchema.JsonInteger(matcherBuilder())

/**
 * Creates a [JsonSchema.JsonDecimal] node, which is a leaf node that will hold a [Double] value.
 * Optionally, you can provide a [matcherBuilder] that constructs a [Matcher] which the node will be tested with.
 */
fun JsonSchema.Builder.number(matcherBuilder: () -> Matcher<Double>? = { null }) =
   JsonSchema.JsonDecimal(matcherBuilder())

/**
 * Alias for [number]
 *
 * Creates a [JsonSchema.JsonDecimal] node, which is a leaf node that will hold a [Double] value.
 * Optionally, you can provide a [matcherBuilder] that constructs a [Matcher] which the node will be tested with.
 */
fun JsonSchema.Builder.decimal(matcherBuilder: () -> Matcher<Double>? = { null }) =
   JsonSchema.JsonDecimal(matcherBuilder())

/**
 * Creates a [JsonSchema.JsonBoolean] node, which is a leaf node that will hold a [Boolean] value.
 * It supports no further configuration. The actual value must always be either true or false.
 */
fun JsonSchema.Builder.boolean() =
   JsonSchema.JsonBoolean

/**
 * Creates a [JsonSchema.Null] node, which is a leaf node that must always be null, if present.
 */
fun JsonSchema.Builder.`null`() =
   JsonSchema.Null

/**
 * Creates a [JsonSchema.JsonObject] node. Expand on the object configuration using the [dsl] which lets you specify
 * properties using [withProperty]
 *
 * Example:
 * ```kotlin
 * val personSchema = jsonSchema {
 *   obj {
 *     withProperty("name") { string() }
 *     withProperty("age") { number() }
 *   }
 * }
 * ```
 */
fun JsonSchema.Builder.obj(dsl: JsonSchema.JsonObjectBuilder.() -> Unit = {}) =
   JsonSchema.JsonObjectBuilder().apply(dsl).build()

/**
 * Defines a [JsonSchema.JsonArray] node where the elements are of the type provided by [typeBuilder].
 */
fun JsonSchema.Builder.array(typeBuilder: () -> JsonSchemaElement) =
   JsonSchema.JsonArray(typeBuilder())

fun jsonSchema(
   rootBuilder: JsonSchema.Builder.() -> JsonSchemaElement
): JsonSchema =
   JsonSchema(
      JsonSchema.Builder.rootBuilder()
   )
