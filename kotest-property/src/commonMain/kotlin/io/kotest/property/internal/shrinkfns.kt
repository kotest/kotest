package io.kotest.property.internal

import io.kotest.property.PropertyContext
import io.kotest.property.Sample
import io.kotest.property.ShrinkingMode

/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to return
 * the smallest failing case.
 */
fun <A> shrinkfn(
   a: Sample<A>,
   property: suspend PropertyContext.(A) -> Unit,
   shrinkingMode: ShrinkingMode
): suspend () -> List<ShrinkResult<A>> = {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   with(context) {
      val smallestA = doShrinking(a.shrinks, shrinkingMode) { property(it) }
      listOf(smallestA)
   }
}

/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to return
 * the smallest failing case.
 */
fun <A, B> shrinkfn(
   a: Sample<A>,
   b: Sample<B>,
   property: suspend PropertyContext.(A, B) -> Unit,
   shrinkingMode: ShrinkingMode
): suspend () -> List<ShrinkResult<Any?>> = {
   val context = PropertyContext()
   with(context) {
      val smallestA = doShrinking(a.shrinks, shrinkingMode) { property(it, b.value) }
      val smallestB = doShrinking(b.shrinks, shrinkingMode) { property(smallestA.shrink, it) }
      listOf(smallestA, smallestB)
   }
}

/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to return
 * the smallest failing case.
 */
fun <A, B, C> shrinkfn(
   a: Sample<A>,
   b: Sample<B>,
   c: Sample<C>,
   property: suspend PropertyContext.(A, B, C) -> Unit,
   shrinkingMode: ShrinkingMode
): suspend () -> List<ShrinkResult<Any?>> = {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   with(context) {
      val smallestA = doShrinking(a.shrinks, shrinkingMode) { property(it, b.value, c.value) }
      val smallestB = doShrinking(b.shrinks, shrinkingMode) { property(smallestA.shrink, it, c.value) }
      val smallestC = doShrinking(c.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, it) }
      listOf(smallestA, smallestB, smallestC)
   }
}

/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to return
 * the smallest failing case.
 */
fun <A, B, C, D> shrinkfn(
   a: Sample<A>,
   b: Sample<B>,
   c: Sample<C>,
   d: Sample<D>,
   property: suspend PropertyContext.(A, B, C, D) -> Unit,
   shrinkingMode: ShrinkingMode
): suspend () -> List<ShrinkResult<Any?>> = {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   with(context) {
      val smallestA = doShrinking(a.shrinks, shrinkingMode) { property(it, b.value, c.value, d.value) }
      val smallestB = doShrinking(b.shrinks, shrinkingMode) { property(smallestA.shrink, it, c.value, d.value) }
      val smallestC = doShrinking(c.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, it, d.value) }
      val smallestD = doShrinking(d.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, it) }
      listOf(smallestA, smallestB, smallestC, smallestD)
   }
}

/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to return
 * the smallest failing case.
 */
fun <A, B, C, D, E> shrinkfn(
   a: Sample<A>,
   b: Sample<B>,
   c: Sample<C>,
   d: Sample<D>,
   e: Sample<E>,
   property: suspend PropertyContext.(A, B, C, D, E) -> Unit,
   shrinkingMode: ShrinkingMode
): suspend () -> List<ShrinkResult<Any?>> = {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   with(context) {
      val smallestA = doShrinking(a.shrinks, shrinkingMode) { property(it, b.value, c.value, d.value, e.value) }
      val smallestB = doShrinking(b.shrinks, shrinkingMode) { property(smallestA.shrink, it, c.value, d.value, e.value) }
      val smallestC = doShrinking(c.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, it, d.value, e.value) }
      val smallestD = doShrinking(d.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, it, e.value) }
      val smallestE = doShrinking(e.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, it) }
      listOf(smallestA, smallestB, smallestC, smallestD, smallestE)
   }
}

/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to return
 * the smallest failing case.
 */
fun <A, B, C, D, E, F> shrinkfn(
   a: Sample<A>,
   b: Sample<B>,
   c: Sample<C>,
   d: Sample<D>,
   e: Sample<E>,
   f: Sample<F>,
   property: suspend PropertyContext.(A, B, C, D, E, F) -> Unit,
   shrinkingMode: ShrinkingMode
): suspend () -> List<ShrinkResult<Any?>> = {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   with(context) {
      val smallestA =
         doShrinking(a.shrinks, shrinkingMode) { property(it, b.value, c.value, d.value, e.value, f.value) }
      val smallestB =
         doShrinking(b.shrinks, shrinkingMode) { property(smallestA.shrink, it, c.value, d.value, e.value, f.value) }
      val smallestC =
         doShrinking(c.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, it, d.value, e.value, f.value) }
      val smallestD =
         doShrinking(d.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, it, e.value, f.value) }
      val smallestE =
         doShrinking(e.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, it, f.value) }
      val smallestF =
         doShrinking(f.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, it) }
      listOf(smallestA, smallestB, smallestC, smallestD, smallestE, smallestF)
   }
}
