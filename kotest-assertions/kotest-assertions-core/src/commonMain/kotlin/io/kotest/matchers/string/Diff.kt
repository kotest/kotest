package io.kotest.matchers.string

sealed class Diff {
  abstract fun isEmpty(): Boolean

  abstract fun toString(level: Int): String

  override fun toString(): String = toString(level = 0)

  protected fun getIndent(level: Int): String = " ".repeat(2 * level)

  companion object {
    fun create(value: Any?, expected: Any?, ignoreExtraMapKeys: Boolean = false): Diff {
      return when {
        value is Map<*, *> && expected is Map<*, *> -> {
          val missingKeys = ArrayList<Any?>()
          val extraKeys = ArrayList<Any?>()
          val differentValues = ArrayList<Diff>()
          expected.forEach { (k, v) ->
            if (!value.containsKey(k)) {
              missingKeys.add(k)
            } else if (value[k] != v) {
              differentValues.add(MapValues(k, create(value[k], v, ignoreExtraMapKeys = ignoreExtraMapKeys))
              )
            }
          }
          if (!ignoreExtraMapKeys) {
            value.keys.forEach { k ->
              if (!expected.containsKey(k)) {
                extraKeys.add(k)
              }
            }
          }
          Maps(missingKeys, extraKeys, differentValues)
        }
        else -> {
          Values(value, expected)
        }
      }
    }
  }

  class Values(
    private val value: Any?,
    private val expected: Any?
  ) : Diff() {
    override fun isEmpty(): Boolean = value == expected

    override fun toString(level: Int): String {
      return """
        |expected:
        |  ${stringify(expected)}
        |but was:
        |  ${stringify(value)}
        """.replaceIndentByMargin(getIndent(level))
    }
  }

  class MapValues(
    private val key: Any?,
    private val valueDiff: Diff
  ) : Diff() {
    override fun isEmpty(): Boolean = valueDiff.isEmpty()

    override fun toString(level: Int): String {
      return """
        |${getIndent(level)}${stringify(key)}:
        |${valueDiff.toString(level + 1)}
        """.trimMargin()
    }
  }

  class Maps(
    private val missingKeys: List<Any?>,
    private val extraKeys: List<Any?>,
    private val differentValues: List<Diff>
  ) : Diff() {
    override fun isEmpty(): Boolean {
      return missingKeys.isEmpty() &&
        extraKeys.isEmpty() &&
        differentValues.isEmpty()
    }

    override fun toString(level: Int): String {
      val diffValues = differentValues.map {
        it.toString(level + 1)
      }
      return listOf(
        "missing keys" to missingKeys.map { getIndent(level + 1) + stringify(it) },
        "extra keys" to extraKeys.map { getIndent(level + 1) + stringify(it) },
        "different values" to diffValues
      ).filter {
        it.second.isNotEmpty()
      }.joinToString("\n") {
        """
        |${getIndent(level)}${it.first}:
        |${it.second.joinToString("\n")}
        """.trimMargin()
      }
    }
  }
}

internal fun stringify(value: Any?): String = when (value) {
  null -> "null"
  is String -> "\"${escapeString(value)}\""
  is Int -> "$value"
  is Double -> "$value"
  is Char -> "'${escapeString(value.toString())}'"
  is Byte -> "$value.toByte()"
  is Short -> "$value.toShort()"
  is Long -> "${value}L"
  is Float -> "${value}F"
  is Map<*, *> -> {
    value.entries.joinToString(prefix = "mapOf(", postfix = ")") {
      "${stringify(it.key)} to ${stringify(it.value)}"
    }
  }
  is List<*> -> reprCollection("listOf", value)
  is Set<*> -> reprCollection("setOf", value)
  is Array<*> -> reprCollection("arrayOf", value.asList())
  is ByteArray -> reprCollection("byteArrayOf", value.asList())
  is ShortArray -> reprCollection("shortArrayOf", value.asList())
  is IntArray -> reprCollection("intArrayOf", value.asList())
  is LongArray -> reprCollection("longArrayOf", value.asList())
  is FloatArray -> reprCollection("floatArrayOf", value.asList())
  is DoubleArray -> reprCollection("doubleArrayOf", value.asList())
  is CharArray -> reprCollection("charArrayOf", value.asList())
  else -> value.toString()
}

private fun reprCollection(funcName: String, value: Collection<*>): String {
  return value.joinToString(prefix = "$funcName(", postfix = ")") { stringify(it) }
}

private fun escapeString(s: String): String {
  return s
    .replace("\\", "\\\\")
    .replace("\"", "\\\"")
    .replace("\'", "\\\'")
    .replace("\t", "\\\t")
    .replace("\b", "\\\b")
    .replace("\n", "\\\n")
    .replace("\r", "\\\r")
    .replace("\$", "\\\$")
}
