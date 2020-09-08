package io.kotest.property.exhaustive

import io.kotest.property.Exhaustive

/**
 * Returns an Exhaustive which returns the characters a to z.
 */
fun Exhaustive.Companion.az(): Exhaustive<Char> {
   return ('a'..'z').map { it }.exhaustive()
}
