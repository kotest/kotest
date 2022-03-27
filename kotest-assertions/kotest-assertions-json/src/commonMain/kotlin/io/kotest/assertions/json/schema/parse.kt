package io.kotest.assertions.json.schema

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
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
         "string" -> JsonSchema.JsonString.serializer()
         "number" -> JsonSchema.JsonNumber.serializer()
         "boolean" -> JsonSchema.JsonBoolean.serializer()
         "array" -> JsonSchema.JsonArray.serializer()
         "object" -> JsonSchema.JsonObject.serializer()
         else -> error("Unknown type: $type")
      }
   }
}
