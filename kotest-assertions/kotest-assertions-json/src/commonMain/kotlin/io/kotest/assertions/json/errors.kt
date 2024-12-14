package io.kotest.assertions.json

sealed class JsonError {

   abstract val path: List<String>

   data class UnequalArrayLength(
      override val path: List<String>,
      val expected: Int,
      val actual: Int
   ) : JsonError()

   data class UnequalArrayContent(
      override val path: List<String>,
      val expected: JsonNode.ArrayNode,
      val missing: JsonNode
   ): JsonError()

   data class ObjectMissingKeys(override val path: List<String>, val missing: Set<String>) : JsonError()
   data class ObjectExtraKeys(override val path: List<String>, val extra: Set<String>) : JsonError()
   data class ObjectExtraAndMissingKeys(override val path: List<String>, val extra: Set<String>, val missing: Set<String>) : JsonError()
   data class ExpectedObject(override val path: List<String>, val b: JsonNode) : JsonError()
   data class ExpectedArray(override val path: List<String>, val b: JsonNode) : JsonError()
   data class UnequalStrings(override val path: List<String>, val a: String, val b: String) : JsonError()
   data class UnequalBooleans(override val path: List<String>, val a: Boolean, val b: Boolean) : JsonError()
   data class UnequalValues(override val path: List<String>, val a: Any, val b: Any) : JsonError()
   data class IncompatibleTypes(override val path: List<String>, val a: JsonNode, val b: JsonNode) : JsonError()
   data class ExpectedNull(override val path: List<String>, val b: JsonNode) : JsonError()

   data class NameOrderDiff(
      override val path: List<String>,
      val index: Int,
      val expected: String,
      val actual: String
   ) : JsonError()
}

fun JsonError.asString(): String {
   val dotpath = if (path.isEmpty()) "The top level" else "At '" + path.joinToString(".") + "'"
   return when (this) {
      is JsonError.UnequalArrayLength -> "$dotpath expected array length ${this.expected} but was ${this.actual}"
      is JsonError.ObjectMissingKeys -> "$dotpath object was missing expected field(s) [${missing.joinToString(",")}]"
      is JsonError.ObjectExtraKeys -> "$dotpath object has extra field(s) [${extra.joinToString(",")}]"
      is JsonError.ObjectExtraAndMissingKeys -> "$dotpath object has extra field(s) [${extra.joinToString(",")}] and missing field(s) [${missing.joinToString(",")}]"
      is JsonError.ExpectedObject -> "$dotpath expected object type but was ${b.type()}"
      is JsonError.ExpectedArray -> "$dotpath expected array type but was ${b.type()}"
      is JsonError.UnequalStrings -> "$dotpath expected '$a' but was '$b'"
      is JsonError.UnequalBooleans -> "$dotpath expected $a but was $b"
      is JsonError.UnequalValues -> "$dotpath expected $a but was $b"
      is JsonError.IncompatibleTypes -> "$dotpath expected ${a.type()} but was ${b.type()}"
      is JsonError.ExpectedNull -> "$dotpath expected null but was ${b.type()}"
      is JsonError.NameOrderDiff -> "$dotpath object expected field $index to be '$expected' but was '$actual'"
      is JsonError.UnequalArrayContent -> "$dotpath has extra element '${show(missing)}' not found (or too few) in '${show(expected)}'"
   }
}
