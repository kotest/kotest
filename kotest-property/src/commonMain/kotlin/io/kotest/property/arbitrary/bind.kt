package io.kotest.property.arbitrary

fun <A, T> Arb.Companion.bind(arbA: Arb<A>, createFn: (A) -> T): Arb<T> = arb {
   createFn(arbA.sample(it).value)
}

fun <A, B, T> Arb.Companion.bind(arbA: Arb<A>, arbB: Arb<B>, createFn: (A, B) -> T): Arb<T> = arb {
   val a = arbA.sample(it)
   val b = arbB.sample(it)
   createFn(a.value, b.value)
}

fun <A, B, C, T> Arb.Companion.bind(
   arbA: Arb<A>,
   arbB: Arb<B>,
   arbC: Arb<C>,
   createFn: (A, B, C) -> T
): Arb<T> = arb {
   val a = arbA.sample(it)
   val b = arbB.sample(it)
   val c = arbC.sample(it)
   createFn(a.value, b.value, c.value)
}

fun <A, B, C, D, T> Arb.Companion.bind(
   arbA: Arb<A>, arbB: Arb<B>, arbC: Arb<C>, arbD: Arb<D>,
   createFn: (A, B, C, D) -> T
): Arb<T> = arb {
   val a = arbA.sample(it)
   val b = arbB.sample(it)
   val c = arbC.sample(it)
   val d = arbD.sample(it)
   createFn(a.value, b.value, c.value, d.value)
}

fun <A, B, C, D, E, T> Arb.Companion.bind(
   arbA: Arb<A>, arbB: Arb<B>, arbC: Arb<C>, arbD: Arb<D>, arbE: Arb<E>,
   createFn: (A, B, C, D, E) -> T
): Arb<T> = arb {
   val a = arbA.sample(it)
   val b = arbB.sample(it)
   val c = arbC.sample(it)
   val d = arbD.sample(it)
   val e = arbE.sample(it)
   createFn(a.value, b.value, c.value, d.value, e.value)
}

fun <A, B, C, D, E, F, T> Arb.Companion.bind(
   arbA: Arb<A>,
   arbB: Arb<B>,
   arbC: Arb<C>,
   arbD: Arb<D>,
   arbE: Arb<E>,
   arbF: Arb<F>,
   createFn: (A, B, C, D, E, F) -> T
): Arb<T> = arb {
   val a = arbA.sample(it)
   val b = arbB.sample(it)
   val c = arbC.sample(it)
   val d = arbD.sample(it)
   val e = arbE.sample(it)
   val f = arbF.sample(it)
   createFn(a.value, b.value, c.value, d.value, e.value, f.value)
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
): Arb<T> = arb {
   val a = arbA.sample(it)
   val b = arbB.sample(it)
   val c = arbC.sample(it)
   val d = arbD.sample(it)
   val e = arbE.sample(it)
   val f = arbF.sample(it)
   val g = arbG.sample(it)
   createFn(a.value, b.value, c.value, d.value, e.value, f.value, g.value)
}
