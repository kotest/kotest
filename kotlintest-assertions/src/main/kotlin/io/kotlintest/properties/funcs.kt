import io.kotlintest.properties.PropertyContext

fun convertValueToString(value: Any?): String = when (value) {
  null -> "<null>"
  "" -> "<empty string>"
  else -> {
    val str = value.toString()
    if (str.isBlank()) str.replace("\n", "\\n").replace("\t", "\\t").replace(" ", "\\s") else str
  }
}

fun outputClassifications(context: PropertyContext) {
  context.classificationCounts().entries.sortedByDescending { it.value }.forEach {
    val percentage = (it.value / context.attempts().toDouble() * 100)
    println("${String.format("%.2f", percentage)}% ${it.key}")
  }
}