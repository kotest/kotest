package io.kotest.assertions.yaml

import com.charleskorn.kaml.YamlList
import com.charleskorn.kaml.YamlNode

internal sealed class YamlError {

   abstract val path: List<String>

   data class UnequalArrayLength(
      override val path: List<String>,
      val expected: Int,
      val actual: Int
   ) : YamlError()

   data class UnequalArrayContent(
      override val path: List<String>,
      val expected: YamlList,
      val missing: YamlNode
   ) : YamlError()

   data class ObjectMissingKeys(override val path: List<String>, val missing: Set<String>) : YamlError()
   data class ObjectExtraKeys(override val path: List<String>, val extra: Set<String>) : YamlError()
   data class ObjectExtraAndMissingKeys(
      override val path: List<String>,
      val extra: Set<String>,
      val missing: Set<String>
   ) : YamlError()

   data class ExpectedMap(override val path: List<String>, val b: YamlNode) : YamlError()
   data class ExpectedList(override val path: List<String>, val b: YamlNode) : YamlError()
   data class UnequalValues(override val path: List<String>, val a: String, val b: String) : YamlError()
   data class IncompatibleTypes(override val path: List<String>, val a: String, val b: String) : YamlError()
   data class ExpectedNull(override val path: List<String>, val b: YamlNode) : YamlError()

   data class NameOrderDiff(
      override val path: List<String>,
      val index: Int,
      val expected: String,
      val actual: String
   ) : YamlError()
}

internal fun YamlError.asString(): String {
   val dotpath = if (path.isEmpty()) "The top level" else "At '" + path.joinToString(".") + "'"
   return when (this) {
      is YamlError.UnequalArrayLength -> "$dotpath expected array length ${this.expected} but was ${this.actual}"
      is YamlError.ObjectMissingKeys -> "$dotpath object was missing expected field(s) [${missing.joinToString(",")}]"
      is YamlError.ObjectExtraKeys -> "$dotpath object has extra field(s) [${extra.joinToString(",")}]"
      is YamlError.ObjectExtraAndMissingKeys -> "$dotpath object has extra field(s) [${extra.joinToString(",")}] and missing field(s) [${missing.joinToString(",")}]"
      is YamlError.ExpectedMap -> "$dotpath expected map type but was ${b.type()}"
      is YamlError.ExpectedList -> "$dotpath expected list type but was ${b.type()}"
      is YamlError.UnequalValues -> "$dotpath expected '$a' but was '$b'"
      is YamlError.IncompatibleTypes -> "$dotpath expected ${a} but was ${b}"
      is YamlError.ExpectedNull -> "$dotpath expected null but was ${b.type()}"
      is YamlError.NameOrderDiff -> "$dotpath object expected field $index to be '$expected' but was '$actual'"
      is YamlError.UnequalArrayContent -> "$dotpath has extra element '${show(missing)}' not found (or too few) in '${show(expected)}'"
   }
}
