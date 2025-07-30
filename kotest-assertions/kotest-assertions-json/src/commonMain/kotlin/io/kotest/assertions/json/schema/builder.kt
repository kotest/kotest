@file:Suppress("unused") // We use unused receivers to scope DSL functions

package io.kotest.assertions.json.schema

import io.kotest.assertions.json.ContainsSpec
import io.kotest.assertions.json.JsonNode
import io.kotest.common.ExperimentalKotest
import io.kotest.matchers.Matcher
import io.kotest.matchers.sequences.beUnique
import kotlinx.serialization.Serializable

@DslMarker
annotation class JsonSchemaMarker

@JsonSchemaMarker
@ExperimentalKotest
@Serializable(with = SchemaDeserializer::class)
sealed interface JsonSchemaElement {
   fun typeName(): String
}

internal interface ValueNode<T> {
   val matcher: Matcher<T>?
      get() = null
}

@ExperimentalKotest
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
   @ExperimentalKotest
   operator fun invoke() = root

   object Builder
   internal interface JsonNumber

   data class JsonArray(
     val minItems: Int = 0,
     val maxItems: Int = Int.MAX_VALUE,
     val matcher: Matcher<Sequence<JsonNode>>? = null,
     val contains: ContainsSpec? = null,
     val elementType: JsonSchemaElement? = null,
   ) : JsonSchemaElement {
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
       * By default, properties are optional.
       * Using [requiredProperties], you can specify that it must be included.
       */
      fun withProperty(
         name: String,
         optional: Boolean = false,
         elementBuilder: Builder.() -> JsonSchemaElement
      ) {
         properties[name] = Builder.elementBuilder()
         if (!optional) requiredProperties.add(name)
      }

      fun string(
         name: String,
         optional: Boolean = false,
         matcherBuilder: () -> Matcher<String>? = { null }
      ) = withProperty(name, optional) { string(matcherBuilder) }

      fun integer(
         name: String,
         optional: Boolean = false,
         matcherBuilder: () -> Matcher<Long>? = { null }
      ) = withProperty(name, optional) { integer(matcherBuilder) }

      fun decimal(
         name: String,
         optional: Boolean = false,
         matcherBuilder: () -> Matcher<Double>? = { null }
      ) = withProperty(name, optional) { decimal(matcherBuilder) }

      fun number(
         name: String,
         optional: Boolean = false,
         matcherBuilder: () -> Matcher<Double>? = { null }
      ) = withProperty(name, optional) { number(matcherBuilder) }

      fun array(
         name: String,
         optional: Boolean = false,
         typeBuilder: () -> JsonSchemaElement
      ) = withProperty(name, optional) { array(typeBuilder = typeBuilder) }

      fun obj(
         name: String,
         optional: Boolean = false,
         dsl: JsonSchema.JsonObjectBuilder.() -> Unit = {}
      ) = withProperty(name, optional) { obj(dsl) }

      fun boolean(
         name: String,
         optional: Boolean = false,
      ) = withProperty(name, optional) { boolean() }

      fun `null`(
         name: String,
         optional: Boolean = false,
      ) = withProperty(name, optional) { `null`() }

      fun build() = JsonObject(
         additionalProperties = additionalProperties,
         minProperties = minProperties,
         maxProperties = maxProperties,
         properties = properties,
         requiredProperties = requiredProperties
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
      val requiredProperties: List<String> = emptyList(),
   ) : JsonSchemaElement {
      operator fun get(name: String): JsonSchemaElement? = properties[name]
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
 * Optionally, you can provide a [matcherBuilder] that constructs a [io.kotest.matchers.Matcher] which the node will be tested with.
 */
@ExperimentalKotest
fun JsonSchema.Builder.string(matcherBuilder: () -> Matcher<String>? = { null }) =
   JsonSchema.JsonString(matcherBuilder())

/**
 * Creates a [JsonSchema.JsonInteger] node, which is a leaf node that will hold a [Long] value.
 * Optionally, you can provide a [matcherBuilder] that constructs a [io.kotest.matchers.Matcher] which the node will be tested with.
 */
@ExperimentalKotest
fun JsonSchema.Builder.integer(matcherBuilder: () -> Matcher<Long>? = { null }) =
   JsonSchema.JsonInteger(matcherBuilder())

/**
 * Creates a [JsonSchema.JsonDecimal] node, which is a leaf node that will hold a [Double] value.
 * Optionally, you can provide a [matcherBuilder] that constructs a [io.kotest.matchers.Matcher] which the node will be tested with.
 */
@ExperimentalKotest
fun JsonSchema.Builder.number(matcherBuilder: () -> Matcher<Double>? = { null }) =
   JsonSchema.JsonDecimal(matcherBuilder())

/**
 * Alias for [number]
 *
 * Creates a [JsonSchema.JsonDecimal] node, which is a leaf node that will hold a [Double] value.
 * Optionally, you can provide a [matcherBuilder] that constructs a [io.kotest.matchers.Matcher] which the node will be tested with.
 */
@ExperimentalKotest
fun JsonSchema.Builder.decimal(matcherBuilder: () -> Matcher<Double>? = { null }) =
   JsonSchema.JsonDecimal(matcherBuilder())

/**
 * Creates a [JsonSchema.JsonBoolean] node, which is a leaf node that will hold a [Boolean] value.
 * It supports no further configuration. The actual value must always be either true or false.
 */
@ExperimentalKotest
fun JsonSchema.Builder.boolean() = JsonSchema.JsonBoolean

/**
 * Creates a [JsonSchema.Null] node, which is a leaf node that must always be null, if present.
 */
@ExperimentalKotest
fun JsonSchema.Builder.`null`() = JsonSchema.Null

/**
 * Creates a [JsonSchema.JsonObject] node. Expand on the object configuration using the [dsl] which lets you specify
 * properties using [JsonSchema.JsonObjectBuilder.withProperty]
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
@ExperimentalKotest
fun JsonSchema.Builder.obj(dsl: JsonSchema.JsonObjectBuilder.() -> Unit = {}) =
   JsonSchema.JsonObjectBuilder().apply(dsl).build()

/**
 * Defines a [JsonSchema.JsonArray] node where the elements are of the type provided by [typeBuilder].
 * The length of the array can be specified using the [minItems] and [maxItems] keywords. Schema can ensure
 * that each of item in an array is unique specified by [uniqueItems] keyword.
 *
 * @param minItems - minimum array length, default value is 0
 * @param maxItems - maximum array length, default value is [Int.MAX_VALUE]
 * @param uniqueItems - item uniqueness, default value is false
 */
@ExperimentalKotest
fun JsonSchema.Builder.array(
   minItems: Int = 0,
   maxItems: Int = Int.MAX_VALUE,
   uniqueItems: Boolean = false,
   contains: ContainsSpec? = null,
   typeBuilder: (() -> JsonSchemaElement?)? = null
): JsonSchema.JsonArray {
   val matcher: Matcher<Sequence<JsonNode>>? = if (uniqueItems) beUnique() else null
   return JsonSchema.JsonArray(minItems, maxItems, matcher, contains, typeBuilder?.invoke())
}

@ExperimentalKotest
fun jsonSchema(
   rootBuilder: JsonSchema.Builder.() -> JsonSchemaElement
): JsonSchema = JsonSchema(
   JsonSchema.Builder.rootBuilder()
)

fun JsonSchema.Builder.containsSpec(
   minContains: Int = 0,
   maxContains: Int = Int.MAX_VALUE,
   schema: JsonSchema.Builder.() -> JsonSchemaElement
) = ContainsSpec(schema(), minContains = minContains, maxContains = maxContains)
