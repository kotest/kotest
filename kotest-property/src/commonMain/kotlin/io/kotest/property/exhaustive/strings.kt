package io.kotest.property.exhaustive

fun Exhaustive.Companion.azstring(range: IntRange) = object : Exhaustive<String> {
   private fun az() = ('a'..'z').asSequence().map { it.toString() }
   override fun values(): Sequence<String> = range.asSequence().flatMap { size ->
      List(size) { az() }.reduce { acc, seq -> acc.zip(seq).map { (a, b) -> a + b } }
   }
}
