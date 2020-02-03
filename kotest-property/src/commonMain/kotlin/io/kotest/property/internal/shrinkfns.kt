package io.kotest.property.internal

import io.kotest.property.GenValue
import io.kotest.property.PropertyContext

typealias ShrinkFn = suspend () -> List<Any?>

/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to reutrn
 * the smallest failing case.
 */
fun <A> shrinkfn(
   a: GenValue<A>,
   property: suspend PropertyContext.(A) -> Unit
): ShrinkFn = {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   with(context) {
      val smallestA = doShrinking(a.value, a.shrinks.value, a.shrinking) { property(it) }
      listOf(smallestA)
   }
}

/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to reutrn
 * the smallest failing case.
 */
fun <A, B> shrinkfn(
   a: GenValue<A>,
   b: GenValue<B>,
   property: suspend PropertyContext.(A, B) -> Unit
): ShrinkFn = {
   val context = PropertyContext()
   with(context) {
      val smallestA = doShrinking(a.value, a.shrinks.value, a.shrinking) { property(it, b.value) }
      val smallestB = doShrinking(b.value, b.shrinks.value, b.shrinking) { property(smallestA, it) }
      listOf(smallestA, smallestB)
   }
}

/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to reutrn
 * the smallest failing case.
 */
fun <A, B, C> shrinkfn(
   a: GenValue<A>,
   b: GenValue<B>,
   c: GenValue<C>,
   property: suspend PropertyContext.(A, B, C) -> Unit
): ShrinkFn = {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   with(context) {
      val smallestA = doShrinking(a.value, a.shrinks.value, a.shrinking) { property(it, b.value, c.value) }
      val smallestB = doShrinking(b.value, b.shrinks.value, b.shrinking) { property(smallestA, it, c.value) }
      val smallestC = doShrinking(c.value, c.shrinks.value, c.shrinking) { property(smallestA, smallestB, it) }
      listOf(smallestA, smallestB, smallestC)
   }
}
