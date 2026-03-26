package io.kotest.assertions.json.schema

import io.kotest.assertions.json.ContainsSpecSerializer
import io.kotest.assertions.json.JsonNode
import io.kotest.common.ExperimentalKotest
import io.kotest.matchers.Matcher
import io.kotest.matchers.and
import io.kotest.matchers.doubles.beGreaterThan
import io.kotest.matchers.doubles.beGreaterThanOrEqualTo
import io.kotest.matchers.doubles.beLessThan
import io.kotest.matchers.doubles.beLessThanOrEqualTo
import io.kotest.matchers.doubles.beMultipleOf
import io.kotest.matchers.longs.beGreaterThan
import io.kotest.matchers.longs.beGreaterThanOrEqualTo
import io.kotest.matchers.longs.beLessThan
import io.kotest.matchers.longs.beLessThanOrEqualTo
import io.kotest.matchers.longs.beMultipleOf
import io.kotest.matchers.sequences.beUnique
import io.kotest.matchers.string.haveMaxLength
import io.kotest.matchers.string.haveMinLength
import io.kotest.matchers.string.match
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import org.intellij.lang.annotations.Language

private val schemaJsonConfig = Json {
   ignoreUnknownKeys = true
   classDiscriminator = "type"
}

/**
 * Parses a subset of JSON Schema into [JsonSchemaElement] which can be used to verify a JSON document with
 * [shouldMatchSchema]
 */
@ExperimentalKotest
fun parseSchema(@Language("json") jsonSchema: String): JsonSchema =
   JsonSchema(root = schemaJsonConfig.decodeFromString(SchemaDeserializer, jsonSchema))

@ExperimentalKotest
internal object SchemaDeserializer : JsonContentPolymorphicSerializer<JsonSchemaElement>(JsonSchemaElement::class) {
   override fun selectDeserializer(element: JsonElement): DeserializationStrategy<JsonSchemaElement> {
      if (element.jsonObject.containsKey("anyOf")) return JsonSchemaAnyOfSerializer
      if (element.jsonObject.containsKey("oneOf")) return JsonSchemaOneOfSerializer
      if (element.jsonObject.containsKey("enum")) return JsonSchemaEnumSerializer
      return when (val type = element.jsonObject["type"]?.jsonPrimitive?.content) {
         "array" -> JsonSchemaArraySerializer
         "object" -> JsonSchema.JsonObject.serializer()
         "string" -> JsonSchemaStringSerializer
         "integer" -> JsonSchemaIntegerSerializer
         "number" -> JsonSchemaNumberSerializer
         "boolean" -> JsonSchema.JsonBoolean.serializer()
         "null" -> JsonSchema.Null.serializer()
         else -> error("Unknown type: $type")
      }
   }
}

private infix fun <T> Matcher<T>?.and(other: Matcher<T>) =
   this?.and(other) ?: other

@ExperimentalKotest
internal object JsonSchemaArraySerializer : KSerializer<JsonSchema.JsonArray> {
   override val descriptor = buildClassSerialDescriptor("JsonSchema.JsonArray") {
      element<String>("type")
      element<Int>("minItems", isOptional = true)
      element<Int>("maxItems", isOptional = true)
      element<Boolean>("uniqueItems", isOptional = true)
      element<String>("elementType", isOptional = true)
      element<String>("contains", isOptional = true)
      element<JsonElement>("prefixItems", isOptional = true)
   }

   override fun deserialize(decoder: Decoder): JsonSchema.JsonArray {
      require(decoder is JsonDecoder) { "JsonSchemaArraySerializer only supports JSON decoding" }
      val obj = decoder.decodeJsonElement().jsonObject

      val minItems = obj["minItems"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0
      val maxItems = obj["maxItems"]?.jsonPrimitive?.content?.toIntOrNull() ?: Int.MAX_VALUE
      val uniqueItems = obj["uniqueItems"]?.jsonPrimitive?.booleanOrNull ?: false
      val matcher: Matcher<Sequence<JsonNode>>? = if (uniqueItems) beUnique() else null
      val elementType = obj["elementType"]?.let { decoder.json.decodeFromJsonElement(SchemaDeserializer, it) }
      val containsSpec = obj["contains"]?.let { decoder.json.decodeFromJsonElement(ContainsSpecSerializer, it) }
      val prefixItems = obj["prefixItems"]?.jsonArray?.map {
         decoder.json.decodeFromJsonElement(SchemaDeserializer, it)
      } ?: emptyList()

      return JsonSchema.JsonArray(minItems, maxItems, matcher, containsSpec, elementType, prefixItems)
   }

   override fun serialize(encoder: Encoder, value: JsonSchema.JsonArray) {
      TODO("Serialization of JsonSchema not supported atm")
   }
}

@ExperimentalKotest
internal object JsonSchemaEnumSerializer : KSerializer<JsonSchema.JsonEnum> {
   override val descriptor = buildClassSerialDescriptor("JsonSchema.JsonEnum") {
      element<JsonElement>("enum")
   }

   override fun deserialize(decoder: Decoder): JsonSchema.JsonEnum {
      require(decoder is JsonDecoder) { "JsonSchemaEnumSerializer only supports JSON decoding" }
      val obj = decoder.decodeJsonElement().jsonObject
      val enumArray = obj["enum"]?.jsonArray ?: error("Expected 'enum' field in schema")

      val values = enumArray.map { element ->
         when (element) {
           is JsonNull -> null
            is JsonPrimitive if element.isString -> element.content
            is JsonPrimitive if element.booleanOrNull != null -> element.booleanOrNull!!
            is JsonPrimitive if element.longOrNull != null -> element.longOrNull!!
            is JsonPrimitive if element.doubleOrNull != null -> element.doubleOrNull!!
            else -> error("Unsupported enum value: $element")
         }
      }

      return JsonSchema.JsonEnum(values)
   }

   override fun serialize(encoder: Encoder, value: JsonSchema.JsonEnum) {
      TODO("Serialization of JsonSchema not supported atm")
   }
}

@ExperimentalKotest
internal object JsonSchemaStringSerializer : KSerializer<JsonSchema.JsonString> {
   override fun deserialize(decoder: Decoder): JsonSchema.JsonString =
      decoder.decodeStructure(descriptor) {
         var matcher: Matcher<String>? = null

         while (true) {
            when (val index = decodeElementIndex(descriptor)) {
               1 -> matcher = matcher.and(haveMinLength(decodeIntElement(descriptor, index)))
               2 -> matcher = matcher.and(haveMaxLength(decodeIntElement(descriptor, index)))
               3 -> matcher = matcher.and(match(decodeStringElement(descriptor, index).toRegex()))
               // Formats: https://json-schema.org/understanding-json-schema/reference/string.html#built-in-formats
               // TODO: Map formats to matchers
               4 -> println("Formats are currently not supported")
               CompositeDecoder.DECODE_DONE -> break
            }
         }

         JsonSchema.JsonString(matcher)
      }

   override val descriptor = buildClassSerialDescriptor("JsonSchema.JsonString") {
      element<String>("type")
      element<Int>("minLength", isOptional = true)
      element<Int>("maxLength", isOptional = true)
      element<String>("pattern", isOptional = true)
      element<String>("format", isOptional = true)
   }

   override fun serialize(encoder: Encoder, value: JsonSchema.JsonString) {
      TODO("Serialization of JsonSchema not supported atm")
   }
}

@ExperimentalKotest
internal object JsonSchemaIntegerSerializer : KSerializer<JsonSchema.JsonInteger> {
   override fun deserialize(decoder: Decoder): JsonSchema.JsonInteger =
      decoder.decodeStructure(descriptor) {
         var matcher: Matcher<Long>? = null

         while (true) {
            when (val index = decodeElementIndex(descriptor)) {
               1 -> matcher = matcher.and(beMultipleOf(decodeLongElement(descriptor, index)))
               2 -> matcher = matcher.and(beGreaterThanOrEqualTo(decodeLongElement(descriptor, index)))
               3 -> matcher = matcher.and(beGreaterThan(decodeLongElement(descriptor, index)))
               4 -> matcher = matcher.and(beLessThanOrEqualTo(decodeLongElement(descriptor, index)))
               5 -> matcher = matcher.and(beLessThan(decodeLongElement(descriptor, index)))
               CompositeDecoder.DECODE_DONE -> break
            }
         }

         JsonSchema.JsonInteger(matcher)
      }

   override val descriptor = buildClassSerialDescriptor("JsonSchema.JsonInteger") {
      element<String>("type")
      element<Long>("multipleOf", isOptional = true)
      element<Long>("minimum", isOptional = true)
      element<Long>("exclusiveMinimum", isOptional = true)
      element<Long>("maximum", isOptional = true)
      element<Long>("exclusiveMaximum", isOptional = true)
   }

   override fun serialize(encoder: Encoder, value: JsonSchema.JsonInteger) {
      TODO("Not yet implemented")
   }
}

@ExperimentalKotest
internal object JsonSchemaNumberSerializer : KSerializer<JsonSchema.JsonDecimal> {
   override fun deserialize(decoder: Decoder): JsonSchema.JsonDecimal =
      decoder.decodeStructure(descriptor) {
         var matcher: Matcher<Double>? = null

         while (true) {
            when (val index = decodeElementIndex(descriptor)) {
               1 -> matcher = matcher.and(beMultipleOf(decodeDoubleElement(descriptor, index)))
               2 -> matcher = matcher.and(beGreaterThanOrEqualTo(decodeDoubleElement(descriptor, index)))
               3 -> matcher = matcher.and(beGreaterThan(decodeDoubleElement(descriptor, index)))
               4 -> matcher = matcher.and(beLessThanOrEqualTo(decodeDoubleElement(descriptor, index)))
               5 -> matcher = matcher.and(beLessThan(decodeDoubleElement(descriptor, index)))
               CompositeDecoder.DECODE_DONE -> break
            }
         }

         JsonSchema.JsonDecimal(matcher)
      }

   override val descriptor = buildClassSerialDescriptor("JsonSchema.JsonDecimal") {
      element<String>("type")
      element<Double>("multipleOf", isOptional = true)
      element<Double>("minimum", isOptional = true)
      element<Double>("exclusiveMinimum", isOptional = true)
      element<Double>("maximum", isOptional = true)
      element<Double>("exclusiveMaximum", isOptional = true)
   }

   override fun serialize(encoder: Encoder, value: JsonSchema.JsonDecimal) {
      TODO("Not yet implemented")
   }
}

@ExperimentalKotest
internal object JsonSchemaAnyOfSerializer : KSerializer<JsonSchema.JsonAnyOf> {
   override fun deserialize(decoder: Decoder): JsonSchema.JsonAnyOf {
      require(decoder is JsonDecoder) { "JsonSchema can only be deserialized from JSON" }
      val jsonObject = decoder.decodeJsonElement().jsonObject
      val schemas = jsonObject["anyOf"]!!.jsonArray.map {
         schemaJsonConfig.decodeFromJsonElement(SchemaDeserializer, it)
      }
      require(schemas.isNotEmpty()) { "anyOf requires at least one schema" }
      return JsonSchema.JsonAnyOf(schemas)
   }

   override val descriptor = buildClassSerialDescriptor("JsonSchema.JsonAnyOf") {
      element<JsonElement>("anyOf")
   }

   override fun serialize(encoder: Encoder, value: JsonSchema.JsonAnyOf) {
      TODO("Serialization of JsonSchema not supported atm")
   }
}

@ExperimentalKotest
internal object JsonSchemaOneOfSerializer : KSerializer<JsonSchema.JsonOneOf> {
   override fun deserialize(decoder: Decoder): JsonSchema.JsonOneOf {
      require(decoder is JsonDecoder) { "JsonSchema can only be deserialized from JSON" }
      val jsonObject = decoder.decodeJsonElement().jsonObject
      val schemas = jsonObject["oneOf"]!!.jsonArray.map {
         schemaJsonConfig.decodeFromJsonElement(SchemaDeserializer, it)
      }
      require(schemas.isNotEmpty()) { "oneOf requires at least one schema" }
      return JsonSchema.JsonOneOf(schemas)
   }

   override val descriptor = buildClassSerialDescriptor("JsonSchema.JsonOneOf") {
      element<JsonElement>("oneOf")
   }

   override fun serialize(encoder: Encoder, value: JsonSchema.JsonOneOf) {
      TODO("Serialization of JsonSchema not supported atm")
   }
}
