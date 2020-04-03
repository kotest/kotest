package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Gen

fun <A, B, T : Any> Arb.Companion.bind(genA: Gen<A>, genB: Gen<B>, bindFn: (A, B) -> T): Arb<T> = arb {
   val iterA = genA.generate(it).iterator()
   val iterB = genB.generate(it).iterator()
   generateSequence {
      val a = iterA.next()
      val b = iterB.next()
      bindFn(a.value, b.value)
   }
}

fun <A, B, C, T : Any> Arb.Companion.bind(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   bindFn: (A, B, C) -> T
): Arb<T> = arb {
   val iterA = genA.generate(it).iterator()
   val iterB = genB.generate(it).iterator()
   val iterC = genC.generate(it).iterator()
   generateSequence {
      val a = iterA.next()
      val b = iterB.next()
      val c = iterC.next()
      bindFn(a.value, b.value, c.value)
   }
}

fun <A, B, C, D, T : Any> Arb.Companion.bind(
   genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>,
   bindFn: (A, B, C, D) -> T
): Arb<T> = arb {
   val iterA = genA.generate(it).iterator()
   val iterB = genB.generate(it).iterator()
   val iterC = genC.generate(it).iterator()
   val iterD = genD.generate(it).iterator()
   generateSequence {
      val a = iterA.next()
      val b = iterB.next()
      val c = iterC.next()
      val d = iterD.next()
      bindFn(a.value, b.value, c.value, d.value)
   }
}

fun <A, B, C, D, E, T : Any> Arb.Companion.bind(
   genA: Arb<A>, genB: Arb<B>, genC: Arb<C>, genD: Arb<D>, genE: Arb<E>,
   bindFn: (A, B, C, D, E) -> T
): Arb<T> = arb {
   val iterA = genA.generate(it).iterator()
   val iterB = genB.generate(it).iterator()
   val iterC = genC.generate(it).iterator()
   val iterD = genD.generate(it).iterator()
   val iterE = genE.generate(it).iterator()
   generateSequence {
      val a = iterA.next()
      val b = iterB.next()
      val c = iterC.next()
      val d = iterD.next()
      val e = iterE.next()
      bindFn(a.value, b.value, c.value, d.value, e.value)
   }
}


fun <A, B, C, D, E, F, T : Any> Arb.Companion.bind(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   bindFn: (A, B, C, D, E, F) -> T
): Arb<T> = arb {
   val iterA = genA.generate(it).iterator()
   val iterB = genB.generate(it).iterator()
   val iterC = genC.generate(it).iterator()
   val iterD = genD.generate(it).iterator()
   val iterE = genE.generate(it).iterator()
   val iterF = genF.generate(it).iterator()
   generateSequence {
      val a = iterA.next()
      val b = iterB.next()
      val c = iterC.next()
      val d = iterD.next()
      val e = iterE.next()
      val f = iterF.next()
      bindFn(a.value, b.value, c.value, d.value, e.value, f.value)
   }
}


fun <A, B, C, D, E, F, G, T : Any> Arb.Companion.bind(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   genD: Gen<D>,
   genE: Gen<E>,
   genF: Gen<F>,
   genG: Gen<G>,
   bindFn: (A, B, C, D, E, F, G) -> T
): Arb<T> = arb {
   val seqA = genA.generate(it).iterator()
   val seqB = genB.generate(it).iterator()
   val seqC = genC.generate(it).iterator()
   val seqD = genD.generate(it).iterator()
   val seqE = genE.generate(it).iterator()
   val seqF = genF.generate(it).iterator()
   val seqG = genG.generate(it).iterator()
   generateSequence {
      val a = seqA.next()
      val b = seqB.next()
      val c = seqC.next()
      val d = seqD.next()
      val e = seqE.next()
      val f = seqF.next()
      val g = seqG.next()
      bindFn(a.value, b.value, c.value, d.value, e.value, f.value, g.value)
   }
}
