package io.kotest.assertions.json.schema

import io.kotest.matchers.Matcher
import io.kotest.matchers.and
import io.kotest.matchers.doubles.beGreaterThan
import io.kotest.matchers.doubles.beGreaterThanOrEqualTo
import io.kotest.matchers.doubles.beLessThan
import io.kotest.matchers.doubles.beLessThanOrEqualTo
import io.kotest.matchers.doubles.beMultipleOf
import io.kotest.matchers.string.haveMaxLength
import io.kotest.matchers.string.haveMinLength
import io.kotest.matchers.string.match
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

val schemaJsonConfig = Json {
   ignoreUnknownKeys = true
   classDiscriminator = "type"
}

/**
 * Parses a subset of JSON Schema into [JsonSchemaElement] which can be used to verify a json document with
 * [shouldMatchSchema]
 */
fun parseSchema(jsonSchema: String): JsonSchema =
   JsonSchema(root = schemaJsonConfig.decodeFromString(SchemaDeserializer, jsonSchema))

object SchemaDeserializer : JsonContentPolymorphicSerializer<JsonSchemaElement>(JsonSchemaElement::class) {
   override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out JsonSchemaElement> {
      return when (val type = element.jsonObject.get("type")?.jsonPrimitive?.content) {
         "string" -> JsonSchemaStringSerializer
         "number" -> JsonSchemaNumberSerializer
         "boolean" -> JsonSchema.JsonBoolean.serializer()
         "array" -> JsonSchema.JsonArray.serializer()
         "object" -> JsonSchema.JsonObject.serializer()
         else -> error("Unknown type: $type")
      }
   }
}

private infix fun <T> Matcher<T>?.and(other: Matcher<T>) =
   if (this != null) this and other else other

object JsonSchemaStringSerializer : KSerializer<JsonSchema.JsonString> {
   override fun deserialize(decoder: Decoder): JsonSchema.JsonString =
      decoder.decodeStructure(descriptor) {
         var matcher: Matcher<String>? = null

         while (true) {
            when (val index = decodeElementIndex(descriptor)) {
               1 -> matcher = matcher and haveMinLength(decodeIntElement(descriptor, index))
               2 -> matcher = matcher and haveMaxLength(decodeIntElement(descriptor, index))
               3 -> matcher = matcher and match(decodeStringElement(descriptor, index).toRegex())
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

object JsonSchemaNumberSerializer : KSerializer<JsonSchema.JsonNumber> {
   override fun deserialize(decoder: Decoder): JsonSchema.JsonNumber =
      decoder.decodeStructure(descriptor) {
         var matcher: Matcher<Double>? = null

         while (true) {
            when (val index = decodeElementIndex(descriptor)) {
               0 -> matcher =
                  if (decodeStringElement(descriptor, index) == "integer") matcher and beMultipleOf(1.0)
                  else matcher
               1 -> matcher = matcher and beMultipleOf(decodeDoubleElement(descriptor, index))
               2 -> matcher = matcher and beGreaterThanOrEqualTo(decodeDoubleElement(descriptor, index))
               3 -> matcher = matcher and beGreaterThan(decodeDoubleElement(descriptor, index))
               4 -> matcher = matcher and beLessThanOrEqualTo(decodeDoubleElement(descriptor, index))
               5 -> matcher = matcher and beLessThan(decodeDoubleElement(descriptor, index))
               CompositeDecoder.DECODE_DONE -> break
            }
         }

         JsonSchema.JsonNumber(matcher)
      }

   override val descriptor = buildClassSerialDescriptor("JsonSchema.JsonNumber") {
      element<String>("type")
      element<Double>("multipleOf", isOptional = true)
      element<Double>("minimum", isOptional = true)
      element<Double>("exclusiveMinimum", isOptional = true)
      element<Double>("maximum", isOptional = true)
      element<Double>("exclusiveMaximum", isOptional = true)
   }

   override fun serialize(encoder: Encoder, value: JsonSchema.JsonNumber) {
      TODO("Not yet implemented")
   }
}
