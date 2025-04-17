package io.kotest.property.exhaustive

import io.kotest.property.Exhaustive

/**
 * Returns an [Exhaustive] which returns the characters a to z.
 */
fun Exhaustive.Companion.az(): Exhaustive<Char> {
   return Exhaustive.char('a'..'z')
}

/**
 * Returns a [Exhaustive] that iterates over the given characters.
 */
fun Exhaustive.Companion.char(range: CharRange): Exhaustive<Char> {
   return range.map { it }.exhaustive()
}
