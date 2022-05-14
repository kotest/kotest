package io.kotest.property.internal

import io.kotest.property.PropTestConfig
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


/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to return
 * the smallest failing case.
 */
fun <A, B, C, D, E, F, G> shrinkfn(
   a: Sample<A>,
   b: Sample<B>,
   c: Sample<C>,
   d: Sample<D>,
   e: Sample<E>,
   f: Sample<F>,
   g: Sample<G>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G) -> Unit,
   shrinkingMode: ShrinkingMode
): suspend () -> List<ShrinkResult<Any?>> = {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   with(context) {
      val smallestA =
         doShrinking(a.shrinks, shrinkingMode) { property(it, b.value, c.value, d.value, e.value, f.value, g.value) }
      val smallestB =
         doShrinking(b.shrinks, shrinkingMode) { property(smallestA.shrink, it, c.value, d.value, e.value, f.value, g.value) }
      val smallestC =
         doShrinking(c.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, it, d.value, e.value, f.value, g.value) }
      val smallestD =
         doShrinking(d.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, it, e.value, f.value, g.value) }
      val smallestE =
         doShrinking(e.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, it, f.value, g.value) }
      val smallestF =
         doShrinking(f.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, it, g.value) }
      val smallestG =
         doShrinking(g.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, it) }
      listOf(smallestA, smallestB, smallestC, smallestD, smallestE, smallestF, smallestG)
   }
}

/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to return
 * the smallest failing case.
 */
fun <A, B, C, D, E, F, G, H> shrinkfn(
   a: Sample<A>,
   b: Sample<B>,
   c: Sample<C>,
   d: Sample<D>,
   e: Sample<E>,
   f: Sample<F>,
   g: Sample<G>,
   h: Sample<H>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H) -> Unit,
   shrinkingMode: ShrinkingMode
): suspend () -> List<ShrinkResult<Any?>> = {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   with(context) {
      val smallestA =
         doShrinking(a.shrinks, shrinkingMode) { property(it, b.value, c.value, d.value, e.value, f.value, g.value, h.value) }
      val smallestB =
         doShrinking(b.shrinks, shrinkingMode) { property(smallestA.shrink, it, c.value, d.value, e.value, f.value, g.value, h.value) }
      val smallestC =
         doShrinking(c.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, it, d.value, e.value, f.value, g.value, h.value) }
      val smallestD =
         doShrinking(d.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, it, e.value, f.value, g.value, h.value) }
      val smallestE =
         doShrinking(e.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, it, f.value, g.value, h.value) }
      val smallestF =
         doShrinking(f.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, it, g.value, h.value) }
      val smallestG =
         doShrinking(g.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, it, h.value) }
      val smallestH =
         doShrinking(h.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, it) }
      listOf(smallestA, smallestB, smallestC, smallestD, smallestE, smallestF, smallestG, smallestH)
   }
}

/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to return
 * the smallest failing case.
 */
fun <A, B, C, D, E, F, G, H, I> shrinkfn(
   a: Sample<A>,
   b: Sample<B>,
   c: Sample<C>,
   d: Sample<D>,
   e: Sample<E>,
   f: Sample<F>,
   g: Sample<G>,
   h: Sample<H>,
   i: Sample<I>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I) -> Unit,
   shrinkingMode: ShrinkingMode
): suspend () -> List<ShrinkResult<Any?>> = {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   with(context) {
      val smallestA =
         doShrinking(a.shrinks, shrinkingMode) { property(it, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value) }
      val smallestB =
         doShrinking(b.shrinks, shrinkingMode) { property(smallestA.shrink, it, c.value, d.value, e.value, f.value, g.value, h.value, i.value) }
      val smallestC =
         doShrinking(c.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, it, d.value, e.value, f.value, g.value, h.value, i.value) }
      val smallestD =
         doShrinking(d.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, it, e.value, f.value, g.value, h.value, i.value) }
      val smallestE =
         doShrinking(e.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, it, f.value, g.value, h.value, i.value) }
      val smallestF =
         doShrinking(f.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, it, g.value, h.value, i.value) }
      val smallestG =
         doShrinking(g.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, it, h.value, i.value) }
      val smallestH =
         doShrinking(h.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, it, i.value) }
      val smallestI =
         doShrinking(i.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, it) }
      listOf(smallestA, smallestB, smallestC, smallestD, smallestE, smallestF, smallestG, smallestH, smallestI)
   }
}


/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to return
 * the smallest failing case.
 */
fun <A, B, C, D, E, F, G, H, I, J> shrinkfn(
   a: Sample<A>,
   b: Sample<B>,
   c: Sample<C>,
   d: Sample<D>,
   e: Sample<E>,
   f: Sample<F>,
   g: Sample<G>,
   h: Sample<H>,
   i: Sample<I>,
   j: Sample<J>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J) -> Unit,
   shrinkingMode: ShrinkingMode
): suspend () -> List<ShrinkResult<Any?>> = {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   with(context) {
      val smallestA =
         doShrinking(a.shrinks, shrinkingMode) { property(it, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value) }
      val smallestB =
         doShrinking(b.shrinks, shrinkingMode) { property(smallestA.shrink, it, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value) }
      val smallestC =
         doShrinking(c.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, it, d.value, e.value, f.value, g.value, h.value, i.value, j.value) }
      val smallestD =
         doShrinking(d.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, it, e.value, f.value, g.value, h.value, i.value, j.value) }
      val smallestE =
         doShrinking(e.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, it, f.value, g.value, h.value, i.value, j.value) }
      val smallestF =
         doShrinking(f.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, it, g.value, h.value, i.value, j.value) }
      val smallestG =
         doShrinking(g.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, it, h.value, i.value, j.value) }
      val smallestH =
         doShrinking(h.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, it, i.value, j.value) }
      val smallestI =
         doShrinking(i.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, it, j.value) }
      val smallestJ =
         doShrinking(j.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, it) }
      listOf(smallestA, smallestB, smallestC, smallestD, smallestE, smallestF, smallestG, smallestH, smallestI, smallestJ)
   }
}


/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to return
 * the smallest failing case.
 */
fun <A, B, C, D, E, F, G, H, I, J, K> shrinkfn(
   a: Sample<A>,
   b: Sample<B>,
   c: Sample<C>,
   d: Sample<D>,
   e: Sample<E>,
   f: Sample<F>,
   g: Sample<G>,
   h: Sample<H>,
   i: Sample<I>,
   j: Sample<J>,
   k: Sample<K>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K) -> Unit,
   shrinkingMode: ShrinkingMode
): suspend () -> List<ShrinkResult<Any?>> = {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   with(context) {
      val smallestA =
         doShrinking(a.shrinks, shrinkingMode) { property(it, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value) }
      val smallestB =
         doShrinking(b.shrinks, shrinkingMode) { property(smallestA.shrink, it, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value) }
      val smallestC =
         doShrinking(c.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, it, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value) }
      val smallestD =
         doShrinking(d.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, it, e.value, f.value, g.value, h.value, i.value, j.value, k.value) }
      val smallestE =
         doShrinking(e.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, it, f.value, g.value, h.value, i.value, j.value, k.value) }
      val smallestF =
         doShrinking(f.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, it, g.value, h.value, i.value, j.value, k.value) }
      val smallestG =
         doShrinking(g.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, it, h.value, i.value, j.value, k.value) }
      val smallestH =
         doShrinking(h.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, it, i.value, j.value, k.value) }
      val smallestI =
         doShrinking(i.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, it, j.value, k.value) }
      val smallestJ =
         doShrinking(j.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, it, k.value) }
      val smallestK =
         doShrinking(k.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, it) }
      listOf(smallestA, smallestB, smallestC, smallestD, smallestE, smallestF, smallestG, smallestH, smallestI, smallestJ, smallestK)
   }
}

/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to return
 * the smallest failing case.
 */
fun <A, B, C, D, E, F, G, H, I, J, K, L> shrinkfn(
   a: Sample<A>,
   b: Sample<B>,
   c: Sample<C>,
   d: Sample<D>,
   e: Sample<E>,
   f: Sample<F>,
   g: Sample<G>,
   h: Sample<H>,
   i: Sample<I>,
   j: Sample<J>,
   k: Sample<K>,
   l: Sample<L>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L) -> Unit,
   shrinkingMode: ShrinkingMode
): suspend () -> List<ShrinkResult<Any?>> = {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   with(context) {
      val smallestA =
         doShrinking(a.shrinks, shrinkingMode) { property(it, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value) }
      val smallestB =
         doShrinking(b.shrinks, shrinkingMode) { property(smallestA.shrink, it, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value) }
      val smallestC =
         doShrinking(c.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, it, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value) }
      val smallestD =
         doShrinking(d.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, it, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value) }
      val smallestE =
         doShrinking(e.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, it, f.value, g.value, h.value, i.value, j.value, k.value, l.value) }
      val smallestF =
         doShrinking(f.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, it, g.value, h.value, i.value, j.value, k.value, l.value) }
      val smallestG =
         doShrinking(g.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, it, h.value, i.value, j.value, k.value, l.value) }
      val smallestH =
         doShrinking(h.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, it, i.value, j.value, k.value, l.value) }
      val smallestI =
         doShrinking(i.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, it, j.value, k.value, l.value) }
      val smallestJ =
         doShrinking(j.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, it, k.value, l.value) }
      val smallestK =
         doShrinking(k.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, it, l.value) }
      val smallestL =
         doShrinking(l.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, it) }
      listOf(smallestA, smallestB, smallestC, smallestD, smallestE, smallestF, smallestG, smallestH, smallestI, smallestJ, smallestK, smallestL)
   }
}
