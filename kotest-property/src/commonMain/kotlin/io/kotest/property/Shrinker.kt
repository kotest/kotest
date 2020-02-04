package io.kotest.property

/**
 * Given a value, T, this function returns reduced values to be used as candidates
 * for shrinking.
 *
 * A smaller value is defined per Shrinker. For a string it may be considered a string with
 * less characters, or less duplication/variation in the characters. For an integer it is typically
 * considered a smaller value with a positive sign.
 *
 * Shrinkers can return one or more values in a shrink step. Shrinkers can
 * return more than one value if there is no single "best path". For example,
 * when shrinking an integer, you probably want to return a single smaller value
 * at a time. For strings, you may wish to return a string that is simpler (YZ -> YY),
 * as well as smaller (YZ -> Y).
 *
 * If the value cannot be shrunk further, or the type
 * does not support meaningful shrinking, then this function should
 * return an empty list.
 */
interface Shrinker<A> {

   /**
    * Returns the "next level" of shrinks for the given value, or empty list if a "base case" has been reached.
    */
   fun shrink(value: A): List<A>
}

/**
 * Generates the shrinks for a given input as a lazily evaluated sequence.
 * It is acceptable for the sequence to include dups.
 */
fun <A> Shrinker<A>.shrinks(value: A): Sequence<A> {
   val shrinks = shrink(value).asSequence()
   return shrinks + shrinks.flatMap { shrinks(it) }
}
