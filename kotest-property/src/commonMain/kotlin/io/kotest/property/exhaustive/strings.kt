package io.kotest.property.exhaustive

import io.kotest.property.Exhaustive

fun Exhaustive.Companion.azstring(range: IntRange): Exhaustive<String> {
   fun az() = ('a'..'z').map { it.toString() }
   val values = range.toList().flatMap { size ->
      List(size) { az() }.reduce { acc, seq -> acc.zip(seq).map { (a, b) -> a + b } }
   }
   return values.exhaustive()
}
