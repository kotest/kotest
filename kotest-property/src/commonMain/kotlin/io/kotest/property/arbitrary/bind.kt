package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Gen

fun <A, B, T : Any> Arb.Companion.bind(genA: Gen<A>, genB: Gen<B>, bindFn: (A, B) -> T): Arb<T> = arb {
   val seqA = genA.generate(it)
   val seqB = genB.generate(it)
   generateSequence {
      val a = seqA.first()
      val b = seqB.first()
      bindFn(a.value, b.value)
   }
}

fun <A, B, C, T : Any> Arb.Companion.bind(
   genA: Gen<A>,
   genB: Gen<B>,
   genC: Gen<C>,
   bindFn: (A, B, C) -> T
): Arb<T> = arb {
   val seqA = genA.generate(it)
   val seqB = genB.generate(it)
   val seqC = genC.generate(it)
   generateSequence {
      val a = seqA.first()
      val b = seqB.first()
      val c = seqC.first()
      bindFn(a.value, b.value, c.value)
   }
}

fun <A, B, C, D, T : Any> Arb.Companion.bind(
   genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>,
   bindFn: (A, B, C, D) -> T
): Arb<T> = arb {
   val seqA = genA.generate(it)
   val seqB = genB.generate(it)
   val seqC = genC.generate(it)
   val seqD = genD.generate(it)
   generateSequence {
      val a = seqA.first()
      val b = seqB.first()
      val c = seqC.first()
      val d = seqD.first()
      bindFn(a.value, b.value, c.value, d.value)
   }
}

fun <A, B, C, D, E, T : Any> Arb.Companion.bind(
   genA: Arb<A>, genB: Arb<B>, genC: Arb<C>, genD: Arb<D>, genE: Arb<E>,
   bindFn: (A, B, C, D, E) -> T
): Arb<T> = arb {
   val seqA = genA.generate(it)
   val seqB = genB.generate(it)
   val seqC = genC.generate(it)
   val seqD = genD.generate(it)
   val seqE = genE.generate(it)
   generateSequence {
      val a = seqA.first()
      val b = seqB.first()
      val c = seqC.first()
      val d = seqD.first()
      val e = seqE.first()
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
   val seqA = genA.generate(it)
   val seqB = genB.generate(it)
   val seqC = genC.generate(it)
   val seqD = genD.generate(it)
   val seqE = genE.generate(it)
   val seqF = genF.generate(it)
   generateSequence {
      val a = seqA.first()
      val b = seqB.first()
      val c = seqC.first()
      val d = seqD.first()
      val e = seqE.first()
      val f = seqF.first()
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
   val seqA = genA.generate(it)
   val seqB = genB.generate(it)
   val seqC = genC.generate(it)
   val seqD = genD.generate(it)
   val seqE = genE.generate(it)
   val seqF = genF.generate(it)
   val seqG = genG.generate(it)
   generateSequence {
      val a = seqA.first()
      val b = seqB.first()
      val c = seqC.first()
      val d = seqD.first()
      val e = seqE.first()
      val f = seqF.first()
      val g = seqG.first()
      bindFn(a.value, b.value, c.value, d.value, e.value, f.value, g.value)
   }
}
