package io.kotlintest.properties

private fun convertValueToString(value: Any?): String = when (value) {
  null -> "<null>"
  "" -> "<empty string>"
  else -> {
    val str = value.toString()
    if (str.isBlank()) "${str.replace("\n", "\\n").replace("\t", "\\t")} <whitespace only>" else str
  }
}

class PropertyAssertionError(val e: AssertionError, val attempt: Int, val values: List<Any?>) :
    AssertionError("Property failed for\n${values.withIndex().joinToString("\n") { "${it.index}: ${convertValueToString(it.value)}" }}\nafter $attempt attempts")