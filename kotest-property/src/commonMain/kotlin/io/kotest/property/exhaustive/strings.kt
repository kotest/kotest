package io.kotest.property.exhaustive

fun Exhaustive.Companion.azstring(range: IntRange) = object : Exhaustive<String> {
   private fun az() = ('a'..'z').map { it.toString() }
   override val values: List<String> = range.toList().flatMap { size ->
      List(size) { az() }.reduce { acc, seq -> acc.zip(seq).map { (a, b) -> a + b } }
   }
}
