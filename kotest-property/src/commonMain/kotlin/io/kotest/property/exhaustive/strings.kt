package io.kotest.property.exhaustive

import io.kotest.property.Exhaustive
import io.kotest.property.RandomSource

fun Exhaustive.Companion.azstring(range: IntRange): Exhaustive<String> {
   fun az() = ('a'..'z').map { it.toString() }
   val values = range.toList().flatMap { size ->
      List(size) { az() }.reduce { acc, seq -> acc.flatMap { a ->
         seq.map { b -> a + b } }
      }
   }
   return values.exhaustive()
}

fun Exhaustive.Companion.upperLowerCase(s: String): Exhaustive<String> {
   val caseSensitiveChars = s.filter(::isCaseSensitive).length

   if (caseSensitiveChars > 20) {
      println(
         "[WARN] Generating exhaustive permutations for $s, which has $caseSensitiveChars case-sensitive characters. " +
            "This will generate 2**$caseSensitiveChars permutations, which may take a while to run."
      )
   }

   return Exhaustive.of(*s.upperLowerCases(RandomSource.default()).toList().toTypedArray())
}

internal fun String.upperLowerCases(rs: RandomSource): Sequence<String> = when {
   isEmpty() -> sequenceOf("")
   else -> first().let { c ->
      val upper = c.uppercaseChar()
      val lower = c.lowercaseChar()
      val rest = substring(1).upperLowerCases(rs)

      sequence {
         if (upper == lower) rest.forEach { yield(buildString { append(c); append(it) }) }
         else rest.forEach { substringCasing ->
            // Otherwise we will guarantee to start with all upper, or all lower
            val (first, second) = if (rs.random.nextBoolean()) upper to lower else lower to upper

            yield(buildString { append(first); append(substringCasing) })
            yield(buildString { append(second); append(substringCasing) })
         }
      }
   }
}

private fun isCaseSensitive(c: Char) = c.uppercaseChar() != c.lowercaseChar()
