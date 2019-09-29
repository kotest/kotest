package io.kotest.properties

fun outputClassifications(context: PropertyContext) {
  context.classificationCounts().entries.sortedByDescending { it.value }.forEach {
    val percentage = (it.value / context.attempts().toDouble() * 100)
    val formatted = (percentage * 100).toInt() / 100.0
    println("$formatted% ${it.key}")
  }
}
