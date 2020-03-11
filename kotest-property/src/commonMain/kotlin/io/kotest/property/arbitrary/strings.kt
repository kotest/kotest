package io.kotest.property.arbitrary

import io.kotest.properties.nextPrintableString
import io.kotest.property.Shrinker
import io.kotest.property.internal.azstring

/**
 * Returns an [Arb] where each random value is a String of length between minSize and maxSize.
 *
 * The edge cases values are:
 *
 * The empty string
 * A line separator
 * Multi-line string
 * a UTF8 string.
 */
fun Arb.Companion.string(
   minSize: Int = 0,
   maxSize: Int = 100
): Arb<String> {

   val range = minSize..maxSize
   val edgecases = listOf(
      "",
      "\n",
      "\nabc\n123\n",
      "\u006c\u0069b/\u0062\u002f\u006d\u0069nd/m\u0061x\u002e\u0070h\u0070"
   ).filter { it.length in range }
   return arb(StringShrinker, edgecases) {
      it.random.nextPrintableString(range.first + it.random.nextInt(range.last - range.first + 1))
   }
}

fun Arb.Companion.string(range: IntRange): Arb<String> = Arb.string(range.first, range.last)

fun Arb.Companion.email(usernameSize: IntRange = 3..10, domainSize: IntRange = 3..10) = arb {
   val username = it.random.azstring(usernameSize)
   val domain = it.random.azstring(domainSize)
   val tld = listOf("com", "net", "gov", "co.uk", "jp", "nl", "ru", "de", "com.br", "it", "pl", "io")
   "$username@$domain.$tld"
}

object StringShrinker : Shrinker<String> {

   override fun shrink(value: String): List<String> {
      return when {
         value == "" -> emptyList()
         value == "a" -> listOf("")
         value.length == 1 -> listOf("", "a")
         else -> {
            val firstHalf = value.take(value.length / 2 + value.length % 2)
            val secondHalf = value.takeLast(value.length / 2)
            val secondHalfAs = firstHalf.padEnd(value.length, 'a')
            val firstHalfAs = secondHalf.padStart(value.length, 'a')
            val dropFirstChar = value.drop(1)
            val dropLastChar = value.dropLast(1)
            listOf(
               firstHalf,
               firstHalfAs,
               secondHalf,
               secondHalfAs,
               dropFirstChar,
               dropLastChar
            )
         }
      }
   }
}
