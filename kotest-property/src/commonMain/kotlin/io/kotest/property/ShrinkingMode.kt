package io.kotest.property

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
    * Shrink a maximum number of times
    */
   data class Bounded(val bound: Int) : ShrinkingMode()
}
