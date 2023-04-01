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

/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to return
 * the smallest failing case.
 */
fun <A, B, C, D, E, F, G, H, I, J, K, L, M> shrinkfn(
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
   m: Sample<M>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M) -> Unit,
   shrinkingMode: ShrinkingMode
): suspend () -> List<ShrinkResult<Any?>> = {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   with(context) {
      val smallestA =
         doShrinking(a.shrinks, shrinkingMode) { property(it, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value) }
      val smallestB =
         doShrinking(b.shrinks, shrinkingMode) { property(smallestA.shrink, it, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value) }
      val smallestC =
         doShrinking(c.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, it, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value) }
      val smallestD =
         doShrinking(d.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, it, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value) }
      val smallestE =
         doShrinking(e.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, it, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value) }
      val smallestF =
         doShrinking(f.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, it, g.value, h.value, i.value, j.value, k.value, l.value, m.value) }
      val smallestG =
         doShrinking(g.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, it, h.value, i.value, j.value, k.value, l.value, m.value) }
      val smallestH =
         doShrinking(h.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, it, i.value, j.value, k.value, l.value, m.value) }
      val smallestI =
         doShrinking(i.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, it, j.value, k.value, l.value, m.value) }
      val smallestJ =
         doShrinking(j.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, it, k.value, l.value, m.value) }
      val smallestK =
         doShrinking(k.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, it, l.value, m.value) }
      val smallestL =
         doShrinking(l.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, it, m.value) }
      val smallestM =
         doShrinking(m.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, it) }
      listOf(smallestA, smallestB, smallestC, smallestD, smallestE, smallestF, smallestG, smallestH, smallestI, smallestJ, smallestK, smallestL, smallestM)
   }
}

/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to return
 * the smallest failing case.
 */
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> shrinkfn(
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
   m: Sample<M>,
   n: Sample<N>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M, N) -> Unit,
   shrinkingMode: ShrinkingMode
): suspend () -> List<ShrinkResult<Any?>> = {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   with(context) {
      val smallestA =
         doShrinking(a.shrinks, shrinkingMode) { property(it, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value) }
      val smallestB =
         doShrinking(b.shrinks, shrinkingMode) { property(smallestA.shrink, it, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value) }
      val smallestC =
         doShrinking(c.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, it, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value) }
      val smallestD =
         doShrinking(d.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, it, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value) }
      val smallestE =
         doShrinking(e.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, it, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value) }
      val smallestF =
         doShrinking(f.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, it, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value) }
      val smallestG =
         doShrinking(g.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, it, h.value, i.value, j.value, k.value, l.value, m.value, n.value) }
      val smallestH =
         doShrinking(h.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, it, i.value, j.value, k.value, l.value, m.value, n.value) }
      val smallestI =
         doShrinking(i.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, it, j.value, k.value, l.value, m.value, n.value) }
      val smallestJ =
         doShrinking(j.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, it, k.value, l.value, m.value, n.value) }
      val smallestK =
         doShrinking(k.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, it, l.value, m.value, n.value) }
      val smallestL =
         doShrinking(l.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, it, m.value, n.value) }
      val smallestM =
         doShrinking(m.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, it, n.value) }
      val smallestN =
         doShrinking(n.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, it) }
      listOf(smallestA, smallestB, smallestC, smallestD, smallestE, smallestF, smallestG, smallestH, smallestI, smallestJ, smallestK, smallestL, smallestM, smallestN)
   }
}

/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to return
 * the smallest failing case.
 */
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> shrinkfn(
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
   m: Sample<M>,
   n: Sample<N>,
   o: Sample<O>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O) -> Unit,
   shrinkingMode: ShrinkingMode
): suspend () -> List<ShrinkResult<Any?>> = {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   with(context) {
      val smallestA =
         doShrinking(a.shrinks, shrinkingMode) { property(it, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value) }
      val smallestB =
         doShrinking(b.shrinks, shrinkingMode) { property(smallestA.shrink, it, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value) }
      val smallestC =
         doShrinking(c.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, it, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value) }
      val smallestD =
         doShrinking(d.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, it, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value) }
      val smallestE =
         doShrinking(e.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, it, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value) }
      val smallestF =
         doShrinking(f.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, it, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value) }
      val smallestG =
         doShrinking(g.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, it, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value) }
      val smallestH =
         doShrinking(h.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, it, i.value, j.value, k.value, l.value, m.value, n.value, o.value) }
      val smallestI =
         doShrinking(i.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, it, j.value, k.value, l.value, m.value, n.value, o.value) }
      val smallestJ =
         doShrinking(j.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, it, k.value, l.value, m.value, n.value, o.value) }
      val smallestK =
         doShrinking(k.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, it, l.value, m.value, n.value, o.value) }
      val smallestL =
         doShrinking(l.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, it, m.value, n.value, o.value) }
      val smallestM =
         doShrinking(m.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, it, n.value, o.value) }
      val smallestN =
         doShrinking(n.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, it, o.value) }
      val smallestO =
         doShrinking(o.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, it) }
      listOf(smallestA, smallestB, smallestC, smallestD, smallestE, smallestF, smallestG, smallestH, smallestI, smallestJ, smallestK, smallestL, smallestM, smallestN, smallestO)
   }
}

/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to return
 * the smallest failing case.
 */
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> shrinkfn(
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
   m: Sample<M>,
   n: Sample<N>,
   o: Sample<O>,
   p: Sample<P>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P) -> Unit,
   shrinkingMode: ShrinkingMode
): suspend () -> List<ShrinkResult<Any?>> = {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   with(context) {
      val smallestA =
         doShrinking(a.shrinks, shrinkingMode) { property(it, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value) }
      val smallestB =
         doShrinking(b.shrinks, shrinkingMode) { property(smallestA.shrink, it, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value) }
      val smallestC =
         doShrinking(c.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, it, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value) }
      val smallestD =
         doShrinking(d.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, it, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value) }
      val smallestE =
         doShrinking(e.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, it, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value) }
      val smallestF =
         doShrinking(f.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, it, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value) }
      val smallestG =
         doShrinking(g.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, it, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value) }
      val smallestH =
         doShrinking(h.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, it, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value) }
      val smallestI =
         doShrinking(i.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, it, j.value, k.value, l.value, m.value, n.value, o.value, p.value) }
      val smallestJ =
         doShrinking(j.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, it, k.value, l.value, m.value, n.value, o.value, p.value) }
      val smallestK =
         doShrinking(k.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, it, l.value, m.value, n.value, o.value, p.value) }
      val smallestL =
         doShrinking(l.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, it, m.value, n.value, o.value, p.value) }
      val smallestM =
         doShrinking(m.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, it, n.value, o.value, p.value) }
      val smallestN =
         doShrinking(n.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, it, o.value, p.value) }
      val smallestO =
         doShrinking(o.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, it, p.value) }
      val smallestP =
         doShrinking(p.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, it) }
      listOf(smallestA, smallestB, smallestC, smallestD, smallestE, smallestF, smallestG, smallestH, smallestI, smallestJ, smallestK, smallestL, smallestM, smallestN, smallestO, smallestP)
   }
}

/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to return
 * the smallest failing case.
 */
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> shrinkfn(
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
   m: Sample<M>,
   n: Sample<N>,
   o: Sample<O>,
   p: Sample<P>,
   q: Sample<Q>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q) -> Unit,
   shrinkingMode: ShrinkingMode
): suspend () -> List<ShrinkResult<Any?>> = {
      // we use a new context for the shrinks, as we don't want to affect classification etc
      val context = PropertyContext()
      with(context) {
         val smallestA =
            doShrinking(a.shrinks, shrinkingMode) { property(it, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value) }
         val smallestB =
            doShrinking(b.shrinks, shrinkingMode) { property(smallestA.shrink, it, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value) }
         val smallestC =
            doShrinking(c.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, it, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value) }
         val smallestD =
            doShrinking(d.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, it, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value) }
         val smallestE =
            doShrinking(e.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, it, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value) }
         val smallestF =
            doShrinking(f.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, it, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value) }
         val smallestG =
            doShrinking(g.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, it, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value) }
         val smallestH =
            doShrinking(h.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, it, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value) }
         val smallestI =
            doShrinking(i.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, it, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value) }
         val smallestJ =
            doShrinking(j.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, it, k.value, l.value, m.value, n.value, o.value, p.value, q.value) }
         val smallestK =
            doShrinking(k.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, it, l.value, m.value, n.value, o.value, p.value, q.value) }
         val smallestL =
            doShrinking(l.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, it, m.value, n.value, o.value, p.value, q.value) }
         val smallestM =
            doShrinking(m.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, it, n.value, o.value, p.value, q.value) }
         val smallestN =
            doShrinking(n.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, it, o.value, p.value, q.value) }
         val smallestO =
            doShrinking(o.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, it, p.value, q.value) }
         val smallestP =
            doShrinking(p.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, it, q.value) }
         val smallestQ =
            doShrinking(q.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, smallestP.shrink, it) }
         listOf(smallestA, smallestB, smallestC, smallestD, smallestE, smallestF, smallestG, smallestH, smallestI, smallestJ, smallestK, smallestL, smallestM, smallestN, smallestO, smallestP, smallestQ)
      }
   }

/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to return
 * the smallest failing case.
 */
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> shrinkfn(
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
   m: Sample<M>,
   n: Sample<N>,
   o: Sample<O>,
   p: Sample<P>,
   q: Sample<Q>,
   r: Sample<R>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R) -> Unit,
   shrinkingMode: ShrinkingMode
): suspend () -> List<ShrinkResult<Any?>> = {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   with(context) {
      val smallestA =
         doShrinking(a.shrinks, shrinkingMode) { property(it, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value) }
      val smallestB =
         doShrinking(b.shrinks, shrinkingMode) { property(smallestA.shrink, it, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value) }
      val smallestC =
         doShrinking(c.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, it, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value) }
      val smallestD =
         doShrinking(d.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, it, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value) }
      val smallestE =
         doShrinking(e.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, it, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value) }
      val smallestF =
         doShrinking(f.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, it, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value) }
      val smallestG =
         doShrinking(g.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, it, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value) }
      val smallestH =
         doShrinking(h.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, it, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value) }
      val smallestI =
         doShrinking(i.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, it, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value) }
      val smallestJ =
         doShrinking(j.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, it, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value) }
      val smallestK =
         doShrinking(k.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, it, l.value, m.value, n.value, o.value, p.value, q.value, r.value) }
      val smallestL =
         doShrinking(l.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, it, m.value, n.value, o.value, p.value, q.value, r.value) }
      val smallestM =
         doShrinking(m.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, it, n.value, o.value, p.value, q.value, r.value) }
      val smallestN =
         doShrinking(n.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, it, o.value, p.value, q.value, r.value) }
      val smallestO =
         doShrinking(o.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, it, p.value, q.value, r.value) }
      val smallestP =
         doShrinking(p.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, it, q.value, r.value) }
      val smallestQ =
         doShrinking(q.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, smallestP.shrink, it, r.value) }
      val smallestR =
         doShrinking(r.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, smallestP.shrink, smallestQ.shrink, it) }
      listOf(smallestA, smallestB, smallestC, smallestD, smallestE, smallestF, smallestG, smallestH, smallestI, smallestJ, smallestK, smallestL, smallestM, smallestN, smallestO, smallestP, smallestQ, smallestR)
   }
}

/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to return
 * the smallest failing case.
 */
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> shrinkfn(
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
   m: Sample<M>,
   n: Sample<N>,
   o: Sample<O>,
   p: Sample<P>,
   q: Sample<Q>,
   r: Sample<R>,
   s: Sample<S>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S) -> Unit,
   shrinkingMode: ShrinkingMode
): suspend () -> List<ShrinkResult<Any?>> = {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   with(context) {
      val smallestA =
         doShrinking(a.shrinks, shrinkingMode) { property(it, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value) }
      val smallestB =
         doShrinking(b.shrinks, shrinkingMode) { property(smallestA.shrink, it, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value) }
      val smallestC =
         doShrinking(c.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, it, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value) }
      val smallestD =
         doShrinking(d.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, it, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value) }
      val smallestE =
         doShrinking(e.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, it, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value) }
      val smallestF =
         doShrinking(f.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, it, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value) }
      val smallestG =
         doShrinking(g.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, it, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value) }
      val smallestH =
         doShrinking(h.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, it, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value) }
      val smallestI =
         doShrinking(i.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, it, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value) }
      val smallestJ =
         doShrinking(j.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, it, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value) }
      val smallestK =
         doShrinking(k.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, it, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value) }
      val smallestL =
         doShrinking(l.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, it, m.value, n.value, o.value, p.value, q.value, r.value, s.value) }
      val smallestM =
         doShrinking(m.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, it, n.value, o.value, p.value, q.value, r.value, s.value) }
      val smallestN =
         doShrinking(n.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, it, o.value, p.value, q.value, r.value, s.value) }
      val smallestO =
         doShrinking(o.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, it, p.value, q.value, r.value, s.value) }
      val smallestP =
         doShrinking(p.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, it, q.value, r.value, s.value) }
      val smallestQ =
         doShrinking(q.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, smallestP.shrink, it, r.value, s.value) }
      val smallestR =
         doShrinking(r.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, smallestP.shrink, smallestQ.shrink, it, s.value) }
      val smallestS =
         doShrinking(s.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, smallestP.shrink, smallestQ.shrink, smallestR.shrink, it) }
      listOf(smallestA, smallestB, smallestC, smallestD, smallestE, smallestF, smallestG, smallestH, smallestI, smallestJ, smallestK, smallestL, smallestM, smallestN, smallestO, smallestP, smallestQ, smallestR, smallestS)
   }
}

/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to return
 * the smallest failing case.
 */
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> shrinkfn(
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
   m: Sample<M>,
   n: Sample<N>,
   o: Sample<O>,
   p: Sample<P>,
   q: Sample<Q>,
   r: Sample<R>,
   s: Sample<S>,
   t: Sample<T>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T) -> Unit,
   shrinkingMode: ShrinkingMode
): suspend () -> List<ShrinkResult<Any?>> = {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   with(context) {
      val smallestA =
         doShrinking(a.shrinks, shrinkingMode) { property(it, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value) }
      val smallestB =
         doShrinking(b.shrinks, shrinkingMode) { property(smallestA.shrink, it, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value) }
      val smallestC =
         doShrinking(c.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, it, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value) }
      val smallestD =
         doShrinking(d.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, it, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value) }
      val smallestE =
         doShrinking(e.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, it, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value) }
      val smallestF =
         doShrinking(f.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, it, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value) }
      val smallestG =
         doShrinking(g.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, it, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value) }
      val smallestH =
         doShrinking(h.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, it, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value) }
      val smallestI =
         doShrinking(i.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, it, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value) }
      val smallestJ =
         doShrinking(j.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, it, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value) }
      val smallestK =
         doShrinking(k.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, it, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value) }
      val smallestL =
         doShrinking(l.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, it, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value) }
      val smallestM =
         doShrinking(m.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, it, n.value, o.value, p.value, q.value, r.value, s.value, t.value) }
      val smallestN =
         doShrinking(n.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, it, o.value, p.value, q.value, r.value, s.value, t.value) }
      val smallestO =
         doShrinking(o.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, it, p.value, q.value, r.value, s.value, t.value) }
      val smallestP =
         doShrinking(p.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, it, q.value, r.value, s.value, t.value) }
      val smallestQ =
         doShrinking(q.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, smallestP.shrink, it, r.value, s.value, t.value) }
      val smallestR =
         doShrinking(r.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, smallestP.shrink, smallestQ.shrink, it, s.value, t.value) }
      val smallestS =
         doShrinking(s.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, smallestP.shrink, smallestQ.shrink, smallestR.shrink, it, t.value) }
      val smallestT =
         doShrinking(t.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, smallestP.shrink, smallestQ.shrink, smallestR.shrink, smallestS.shrink, it) }
      listOf(smallestA, smallestB, smallestC, smallestD, smallestE, smallestF, smallestG, smallestH, smallestI, smallestJ, smallestK, smallestL, smallestM, smallestN, smallestO, smallestP, smallestQ, smallestR, smallestS, smallestT)
   }
}

/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to return
 * the smallest failing case.
 */
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> shrinkfn(
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
   m: Sample<M>,
   n: Sample<N>,
   o: Sample<O>,
   p: Sample<P>,
   q: Sample<Q>,
   r: Sample<R>,
   s: Sample<S>,
   t: Sample<T>,
   u: Sample<U>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U) -> Unit,
   shrinkingMode: ShrinkingMode
): suspend () -> List<ShrinkResult<Any?>> = {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   with(context) {
      val smallestA =
         doShrinking(a.shrinks, shrinkingMode) { property(it, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value) }
      val smallestB =
         doShrinking(b.shrinks, shrinkingMode) { property(smallestA.shrink, it, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value) }
      val smallestC =
         doShrinking(c.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, it, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value) }
      val smallestD =
         doShrinking(d.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, it, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value) }
      val smallestE =
         doShrinking(e.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, it, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value) }
      val smallestF =
         doShrinking(f.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, it, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value) }
      val smallestG =
         doShrinking(g.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, it, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value) }
      val smallestH =
         doShrinking(h.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, it, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value) }
      val smallestI =
         doShrinking(i.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, it, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value) }
      val smallestJ =
         doShrinking(j.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, it, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value) }
      val smallestK =
         doShrinking(k.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, it, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value) }
      val smallestL =
         doShrinking(l.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, it, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value) }
      val smallestM =
         doShrinking(m.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, it, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value) }
      val smallestN =
         doShrinking(n.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, it, o.value, p.value, q.value, r.value, s.value, t.value, u.value) }
      val smallestO =
         doShrinking(o.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, it, p.value, q.value, r.value, s.value, t.value, u.value) }
      val smallestP =
         doShrinking(p.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, it, q.value, r.value, s.value, t.value, u.value) }
      val smallestQ =
         doShrinking(q.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, smallestP.shrink, it, r.value, s.value, t.value, u.value) }
      val smallestR =
         doShrinking(r.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, smallestP.shrink, smallestQ.shrink, it, s.value, t.value, u.value) }
      val smallestS =
         doShrinking(s.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, smallestP.shrink, smallestQ.shrink, smallestR.shrink, it, t.value, u.value) }
      val smallestT =
         doShrinking(t.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, smallestP.shrink, smallestQ.shrink, smallestR.shrink, smallestS.shrink, it, u.value) }
      val smallestU =
         doShrinking(u.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, smallestP.shrink, smallestQ.shrink, smallestR.shrink, smallestS.shrink, smallestT.shrink, it) }
      listOf(smallestA, smallestB, smallestC, smallestD, smallestE, smallestF, smallestG, smallestH, smallestI, smallestJ, smallestK, smallestL, smallestM, smallestN, smallestO, smallestP, smallestQ, smallestR, smallestS, smallestT, smallestU)
   }
}

/**
 * Returns a shrink function, which, when invoked, will shrink the inputs and attempt to return
 * the smallest failing case.
 */
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> shrinkfn(
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
   m: Sample<M>,
   n: Sample<N>,
   o: Sample<O>,
   p: Sample<P>,
   q: Sample<Q>,
   r: Sample<R>,
   s: Sample<S>,
   t: Sample<T>,
   u: Sample<U>,
   v: Sample<V>,
   property: suspend PropertyContext.(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V) -> Unit,
   shrinkingMode: ShrinkingMode
): suspend () -> List<ShrinkResult<Any?>> = {
   // we use a new context for the shrinks, as we don't want to affect classification etc
   val context = PropertyContext()
   with(context) {
      val smallestA =
         doShrinking(a.shrinks, shrinkingMode) { property(it, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value, v.value) }
      val smallestB =
         doShrinking(b.shrinks, shrinkingMode) { property(smallestA.shrink, it, c.value, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value, v.value) }
      val smallestC =
         doShrinking(c.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, it, d.value, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value, v.value) }
      val smallestD =
         doShrinking(d.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, it, e.value, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value, v.value) }
      val smallestE =
         doShrinking(e.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, it, f.value, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value, v.value) }
      val smallestF =
         doShrinking(f.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, it, g.value, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value, v.value) }
      val smallestG =
         doShrinking(g.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, it, h.value, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value, v.value) }
      val smallestH =
         doShrinking(h.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, it, i.value, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value, v.value) }
      val smallestI =
         doShrinking(i.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, it, j.value, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value, v.value) }
      val smallestJ =
         doShrinking(j.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, it, k.value, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value, v.value) }
      val smallestK =
         doShrinking(k.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, it, l.value, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value, v.value) }
      val smallestL =
         doShrinking(l.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, it, m.value, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value, v.value) }
      val smallestM =
         doShrinking(m.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, it, n.value, o.value, p.value, q.value, r.value, s.value, t.value, u.value, v.value) }
      val smallestN =
         doShrinking(n.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, it, o.value, p.value, q.value, r.value, s.value, t.value, u.value, v.value) }
      val smallestO =
         doShrinking(o.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, it, p.value, q.value, r.value, s.value, t.value, u.value, v.value) }
      val smallestP =
         doShrinking(p.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, it, q.value, r.value, s.value, t.value, u.value, v.value) }
      val smallestQ =
         doShrinking(q.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, smallestP.shrink, it, r.value, s.value, t.value, u.value, v.value) }
      val smallestR =
         doShrinking(r.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, smallestP.shrink, smallestQ.shrink, it, s.value, t.value, u.value, v.value) }
      val smallestS =
         doShrinking(s.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, smallestP.shrink, smallestQ.shrink, smallestR.shrink, it, t.value, u.value, v.value) }
      val smallestT =
         doShrinking(t.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, smallestP.shrink, smallestQ.shrink, smallestR.shrink, smallestS.shrink, it, u.value, v.value) }
      val smallestU =
         doShrinking(u.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, smallestP.shrink, smallestQ.shrink, smallestR.shrink, smallestS.shrink, smallestT.shrink, it, v.value) }
      val smallestV =
         doShrinking(v.shrinks, shrinkingMode) { property(smallestA.shrink, smallestB.shrink, smallestC.shrink, smallestD.shrink, smallestE.shrink, smallestF.shrink, smallestG.shrink, smallestH.shrink, smallestI.shrink, smallestJ.shrink, smallestK.shrink, smallestL.shrink, smallestM.shrink, smallestN.shrink, smallestO.shrink, smallestP.shrink, smallestQ.shrink, smallestR.shrink, smallestS.shrink, smallestT.shrink, smallestU.shrink, it) }
      listOf(smallestA, smallestB, smallestC, smallestD, smallestE, smallestF, smallestG, smallestH, smallestI, smallestJ, smallestK, smallestL, smallestM, smallestN, smallestO, smallestP, smallestQ, smallestR, smallestS, smallestT, smallestU, smallestV)
   }
}

