package io.kotest.property.internal

import io.kotest.property.PropertyContext
import io.kotest.property.Sample
import io.kotest.property.ShrinkingMode

typealias ShrinkFn = suspend () -> List<Any?>

/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to reutrn
 * the smallest failing case.
 */
fun <A> shrinkfn(
   a: Sample<A>,
   property: suspend PropertyContext.(A) -> Unit,
   shrinkingMode: ShrinkingMode
): ShrinkFn = {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   with(context) {
      val smallestA = doShrinking(a.shrinks, shrinkingMode) { property(it) }
      listOf(smallestA)
   }
}

/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to reutrn
 * the smallest failing case.
 */
fun <A, B> shrinkfn(
   a: Sample<A>,
   b: Sample<B>,
   property: suspend PropertyContext.(A, B) -> Unit,
   shrinkingMode: ShrinkingMode
): ShrinkFn = {
   val context = PropertyContext()
   with(context) {
      val smallestA = doShrinking(a.shrinks, shrinkingMode) { property(it, b.value) }
      val smallestB = doShrinking(b.shrinks, shrinkingMode) { property(smallestA, it) }
      listOf(smallestA, smallestB)
   }
}

/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to reutrn
 * the smallest failing case.
 */
fun <A, B, C> shrinkfn(
   a: Sample<A>,
   b: Sample<B>,
   c: Sample<C>,
   property: suspend PropertyContext.(A, B, C) -> Unit,
   shrinkingMode: ShrinkingMode
): ShrinkFn = {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   with(context) {
      val smallestA = doShrinking(a.shrinks, shrinkingMode) { property(it, b.value, c.value) }
      val smallestB = doShrinking(b.shrinks, shrinkingMode) { property(smallestA, it, c.value) }
      val smallestC = doShrinking(c.shrinks, shrinkingMode) { property(smallestA, smallestB, it) }
      listOf(smallestA, smallestB, smallestC)
   }
}
