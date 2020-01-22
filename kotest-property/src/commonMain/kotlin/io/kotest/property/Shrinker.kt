package io.kotest.property

interface Shrinker<T> {

   /**
    * Given a value, T, this function returns possible "smaller"
    * values to be used as candidates for shrinking. The shrinking candidates
    * are returned as a Rose Tree. https://en.wikipedia.org/wiki/Rose_tree
    *
    * A smaller value is defined by the shrinker. For a string it may
    * be considered a "simpler" string (one with less duplication), and/or
    * one with less characters. For an integer it could be considered a smaller
    * value with the same sign.
    *
    * Each level in the rose tree contains zero or more entries. Zero entries means that
    * further shrinking cannot take place (for example, 0 or empty string).
    *
    * A single value means there is just one shrink step, and multiple values means there
    * is no single "best path". For example, when shrinking an integer, you probably want
    * to return a single smaller value at a time. For strings, you may wish to return a
    * string that is simpler (YZ -> YY), as well as smaller (YZ -> Y).
    */
   fun shrink(value: T): List<T>

   /**
    * Generates a [RTree] of pre-shunk values depending on the [ShrinkingMode].
    */
   fun shrinks(value: T, mode: ShrinkingMode): RTree<T> {

      fun shrinks(value: T, remaining: Int): RTree<T> {
         return if (remaining == 0) RTree(value, emptyList()) else {
            val shrinks = shrink(value)
            RTree(value, shrinks.map { shrinks(it, remaining - 1) })
         }
      }

      return when (mode) {
         ShrinkingMode.Off -> RTree(value, emptyList())
         ShrinkingMode.Unbounded -> shrinks(value, Int.MAX_VALUE)
         is ShrinkingMode.Bounded -> shrinks(value, mode.bound)
      }
   }
}
