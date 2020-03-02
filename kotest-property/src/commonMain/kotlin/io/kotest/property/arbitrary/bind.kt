package io.kotest.property.arbitrary

import io.kotest.property.RandomSource
import io.kotest.property.Sample

fun <A, T> Arb.Companion.bind(arbA: Arb<A>, createFn: (A) -> T): Arb<T> = arbA.map(createFn)

fun <A, B, T> Arb.Companion.bind(arbA: Arb<A>, arbB: Arb<B>, createFn: (A, B) -> T): Arb<T> = object : Arb<T> {
   override fun edgecases(): List<T> = emptyList()

   override fun samples(rs: RandomSource): Sequence<Sample<T>> = values(arbA.samples(rs), arbB.samples(rs))

   override fun generate(rs: RandomSource): Sequence<Sample<T>> = values(arbA.generate(rs), arbB.generate(rs))

   private fun values(aSamples: Sequence<Sample<A>>, bSamples: Sequence<Sample<B>>): Sequence<Sample<T>> {
      val a = aSamples.iterator()
      val b = bSamples.iterator()

      return sequence {
         while (a.hasNext() && b.hasNext()) {
            yield(Sample(createFn(a.next().value, b.next().value)))
         }
      }
   }
}

fun <A, B, C, T> Arb.Companion.bind(
   arbA: Arb<A>,
   arbB: Arb<B>,
   arbC: Arb<C>,
   createFn: (A, B, C) -> T
): Arb<T> = object : Arb<T> {
   override fun edgecases(): List<T> = emptyList()

   override fun samples(rs: RandomSource): Sequence<Sample<T>> = values(arbA.samples(rs), arbB.samples(rs), arbC.samples(rs))

   override fun generate(rs: RandomSource): Sequence<Sample<T>> = values(arbA.generate(rs), arbB.generate(rs), arbC.generate(rs))

   private fun values(
      aSamples: Sequence<Sample<A>>,
      bSamples: Sequence<Sample<B>>,
      cSamples: Sequence<Sample<C>>
   ): Sequence<Sample<T>> {
      val a = aSamples.iterator()
      val b = bSamples.iterator()
      val c = cSamples.iterator()

      return sequence {
         while (a.hasNext() && b.hasNext() && c.hasNext()) {
            yield(Sample(createFn(a.next().value, b.next().value, c.next().value)))
         }
      }
   }
}

fun <A, B, C, D, T> Arb.Companion.bind(
   arbA: Arb<A>, arbB: Arb<B>, arbC: Arb<C>, arbD: Arb<D>,
   createFn: (A, B, C, D) -> T
): Arb<T> = object : Arb<T> {
   override fun edgecases(): List<T> = emptyList()

   override fun samples(rs: RandomSource): Sequence<Sample<T>> = values(arbA.samples(rs), arbB.samples(rs), arbC.samples(rs), arbD.samples(rs))

   override fun generate(rs: RandomSource): Sequence<Sample<T>> = values(arbA.generate(rs), arbB.generate(rs), arbC.generate(rs), arbD.generate(rs))

   private fun values(
      aSamples: Sequence<Sample<A>>,
      bSamples: Sequence<Sample<B>>,
      cSamples: Sequence<Sample<C>>,
      dSamples: Sequence<Sample<D>>
   ): Sequence<Sample<T>> {
      val a = aSamples.iterator()
      val b = bSamples.iterator()
      val c = cSamples.iterator()
      val d = dSamples.iterator()

      return sequence {
         while (a.hasNext() && b.hasNext() && c.hasNext() && d.hasNext()) {
            yield(Sample(createFn(a.next().value, b.next().value, c.next().value, d.next().value)))
         }
      }
   }
}

fun <A, B, C, D, E, T> Arb.Companion.bind(
   arbA: Arb<A>, arbB: Arb<B>, arbC: Arb<C>, arbD: Arb<D>, arbE: Arb<E>,
   createFn: (A, B, C, D, E) -> T
): Arb<T> = object : Arb<T> {
   override fun edgecases(): List<T> = emptyList()

   override fun samples(rs: RandomSource): Sequence<Sample<T>> = values(arbA.samples(rs), arbB.samples(rs), arbC.samples(rs), arbD.samples(rs), arbE.samples(rs))

   override fun generate(rs: RandomSource): Sequence<Sample<T>> = values(arbA.generate(rs), arbB.generate(rs), arbC.generate(rs), arbD.generate(rs), arbE.generate(rs))

   private fun values(
      aSamples: Sequence<Sample<A>>,
      bSamples: Sequence<Sample<B>>,
      cSamples: Sequence<Sample<C>>,
      dSamples: Sequence<Sample<D>>,
      eSamples: Sequence<Sample<E>>
   ): Sequence<Sample<T>> {
      val a = aSamples.iterator()
      val b = bSamples.iterator()
      val c = cSamples.iterator()
      val d = dSamples.iterator()
      val e = eSamples.iterator()

      return sequence {
         while (a.hasNext() && b.hasNext() && c.hasNext() && d.hasNext() && e.hasNext()) {
            yield(Sample(createFn(a.next().value, b.next().value, c.next().value, d.next().value, e.next().value)))
         }
      }
   }
}

fun <A, B, C, D, E, F, T> Arb.Companion.bind(
   arbA: Arb<A>,
   arbB: Arb<B>,
   arbC: Arb<C>,
   arbD: Arb<D>,
   arbE: Arb<E>,
   arbF: Arb<F>,
   createFn: (A, B, C, D, E, F) -> T
): Arb<T> = object : Arb<T> {
   override fun edgecases(): List<T> = emptyList()

   override fun samples(rs: RandomSource): Sequence<Sample<T>> = values(arbA.samples(rs), arbB.samples(rs), arbC.samples(rs), arbD.samples(rs), arbE.samples(rs), arbF.samples(rs))

   override fun generate(rs: RandomSource): Sequence<Sample<T>> = values(arbA.generate(rs), arbB.generate(rs), arbC.generate(rs), arbD.generate(rs), arbE.generate(rs), arbF.generate(rs))

   private fun values(
      aSamples: Sequence<Sample<A>>,
      bSamples: Sequence<Sample<B>>,
      cSamples: Sequence<Sample<C>>,
      dSamples: Sequence<Sample<D>>,
      eSamples: Sequence<Sample<E>>,
      fSamples: Sequence<Sample<F>>
   ): Sequence<Sample<T>> {
      val a = aSamples.iterator()
      val b = bSamples.iterator()
      val c = cSamples.iterator()
      val d = dSamples.iterator()
      val e = eSamples.iterator()
      val f = fSamples.iterator()

      return sequence {
         while (a.hasNext() && b.hasNext() && c.hasNext() && d.hasNext() && e.hasNext() && f.hasNext()) {
            yield(Sample(createFn(a.next().value, b.next().value, c.next().value, d.next().value, e.next().value, f.next().value)))
         }
      }
   }
}

fun <A, B, C, D, E, F, G, T> Arb.Companion.bind(
   arbA: Arb<A>,
   arbB: Arb<B>,
   arbC: Arb<C>,
   arbD: Arb<D>,
   arbE: Arb<E>,
   arbF: Arb<F>,
   arbG: Arb<G>,
   createFn: (A, B, C, D, E, F, G) -> T
): Arb<T> = object : Arb<T> {
   override fun edgecases(): List<T> = emptyList()

   override fun samples(rs: RandomSource): Sequence<Sample<T>> = values(arbA.samples(rs), arbB.samples(rs), arbC.samples(rs), arbD.samples(rs), arbE.samples(rs), arbF.samples(rs), arbG.samples(rs))

   override fun generate(rs: RandomSource): Sequence<Sample<T>> = values(arbA.generate(rs), arbB.generate(rs), arbC.generate(rs), arbD.generate(rs), arbE.generate(rs), arbF.generate(rs), arbG.generate(rs))

   private fun values(
      aSamples: Sequence<Sample<A>>,
      bSamples: Sequence<Sample<B>>,
      cSamples: Sequence<Sample<C>>,
      dSamples: Sequence<Sample<D>>,
      eSamples: Sequence<Sample<E>>,
      fSamples: Sequence<Sample<F>>,
      gSamples: Sequence<Sample<G>>
   ): Sequence<Sample<T>> {
      val a = aSamples.iterator()
      val b = bSamples.iterator()
      val c = cSamples.iterator()
      val d = dSamples.iterator()
      val e = eSamples.iterator()
      val f = fSamples.iterator()
      val g = gSamples.iterator()

      return sequence {
         while (a.hasNext() && b.hasNext() && c.hasNext() && d.hasNext() && e.hasNext() && f.hasNext() && g.hasNext()) {
            yield(Sample(createFn(a.next().value, b.next().value, c.next().value, d.next().value, e.next().value, f.next().value, g.next().value)))
         }
      }
   }
}
