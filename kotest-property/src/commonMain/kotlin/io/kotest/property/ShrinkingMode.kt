package io.kotest.property

import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
sealed class ShrinkingMode {

   /**
    * Shrinking disabled
    */
   object Off : ShrinkingMode()

   /**
    * Shrinks until no smaller value can be found. May result in an infinite loop if shrinkers are not coded properly.
    */
   object Unbounded : ShrinkingMode()

   /**
    * Shrinks until a maximum number of values have been found.
    */
   data class Bounded(val bound: Int) : ShrinkingMode()
}
