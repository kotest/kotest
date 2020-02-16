package io.kotest.property.arbitrary

fun <A, T> Arb.Companion.bind(arbA: Arb<A>, createFn: (A) -> T): Arb<T> = arb {
   createFn(arbA.single(it))
}

fun <A, B, T> Arb.Companion.bind(arbA: Arb<A>, arbB: Arb<B>, createFn: (A, B) -> T): Arb<T> = arb {
   val a = arbA.single(it)
   val b = arbB.single(it)
   createFn(a, b)
}

fun <A, B, C, T> Arb.Companion.bind(
   arbA: Arb<A>,
   arbB: Arb<B>,
   arbC: Arb<C>,
   createFn: (A, B, C) -> T
): Arb<T> = arb {
   val a = arbA.single(it)
   val b = arbB.single(it)
   val c = arbC.single(it)
   createFn(a, b, c)
}

fun <A, B, C, D, T> Arb.Companion.bind(
   arbA: Arb<A>, arbB: Arb<B>, arbC: Arb<C>, arbD: Arb<D>,
   createFn: (A, B, C, D) -> T
): Arb<T> = arb {
   val a = arbA.single(it)
   val b = arbB.single(it)
   val c = arbC.single(it)
   val d = arbD.single(it)
   createFn(a, b, c, d)
}

fun <A, B, C, D, E, T> Arb.Companion.bind(
   arbA: Arb<A>, arbB: Arb<B>, arbC: Arb<C>, arbD: Arb<D>, arbE: Arb<E>,
   createFn: (A, B, C, D, E) -> T
): Arb<T> = arb {
   val a = arbA.single(it)
   val b = arbB.single(it)
   val c = arbC.single(it)
   val d = arbD.single(it)
   val e = arbE.single(it)
   createFn(a, b, c, d, e)
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
   val a = arbA.single(it)
   val b = arbB.single(it)
   val c = arbC.single(it)
   val d = arbD.single(it)
   val e = arbE.single(it)
   val f = arbF.single(it)
   createFn(a, b, c, d, e, f)
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
   val a = arbA.single(it)
   val b = arbB.single(it)
   val c = arbC.single(it)
   val d = arbD.single(it)
   val e = arbE.single(it)
   val f = arbF.single(it)
   val g = arbG.single(it)
   createFn(a, b, c, d, e, f, g)
}
