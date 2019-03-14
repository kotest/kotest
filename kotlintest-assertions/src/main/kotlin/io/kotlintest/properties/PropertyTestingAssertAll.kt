@file:Suppress("RemoveExplicitTypeArguments")

package io.kotlintest.properties

import outputClassifications
import testAndShrink

inline fun <reified A> assertAll(fn: PropertyContext.(a: A) -> Unit) = assertAll(1000, fn)
inline fun <reified A> assertAll(iterations: Int, fn: PropertyContext.(a: A) -> Unit) = assertAll(iterations, Gen.default(), fn)
inline fun <A> assertAll(genA: Gen<A>, fn: PropertyContext.(a: A) -> Unit) = assertAll(1000, genA, fn)
inline fun <A> assertAll(iterations: Int, genA: Gen<A>, fn: PropertyContext.(a: A) -> Unit) {
  checkIterations(iterations)
  
  val (context, contextValues) = createContextValues(genA, iterations)
  
  contextValues.forEach { testAndShrink(it, genA, context, fn) }
  
  context.outputInfo()
}

@PublishedApi
internal fun <A> createContextValues(genA: Gen<A>, iterations: Int): ContextValues<A> {
  val values = genA.constants().asSequence() + genA.random()
  
  val context = PropertyContext()
  val contextValues = context.collectValues(iterations, values)
  return ContextValues(context, contextValues)
}

inline fun <reified A, reified B> assertAll(fn: PropertyContext.(a: A, b: B) -> Unit) = assertAll(1000, fn)
inline fun <reified A, reified B> assertAll(iterations: Int, fn: PropertyContext.(a: A, b: B) -> Unit) = assertAll(iterations, Gen.default(), Gen.default(), fn)
inline fun <A, B> assertAll(gena: Gen<A>, genb: Gen<B>, fn: PropertyContext.(a: A, b: B) -> Unit) = assertAll(1000, gena, genb, fn)

inline fun <A, B> assertAll(iterations: Int, genA: Gen<A>, genB: Gen<B>, fn: PropertyContext.(a: A, b: B) -> Unit) {
  checkIterations(iterations)
  
  val (context, contextValues) = createContextValues(genA, genB, iterations)
  
  contextValues.forEach { (a, b) -> testAndShrink(a, b, genA, genB, context, fn) }
  context.outputInfo()
}

@PublishedApi
internal fun <A, B> createContextValues(genA: Gen<A>, genB: Gen<B>, iterations: Int): ContextValues<Pair<A, B>> {
  val values = genA.pairWith(genB)
  
  val context = PropertyContext()
  val contextValues = context.collectValues(iterations, values)
  return ContextValues(context, contextValues)
}

@PublishedApi
internal fun <A, B> Gen<A>.pairWith(genB: Gen<B>): Sequence<Pair<A, B>> {
  val constants = constants().flatMap { a -> genB.constants().map { b -> a to b } }
  val randoms = random().zip(genB.random())
  return constants.asSequence() + randoms
}


inline fun <reified A, reified B, reified C> assertAll(fn: PropertyContext.(a: A, b: B, c: C) -> Unit) = assertAll(1000, fn)
inline fun <reified A, reified B, reified C> assertAll(iterations: Int, fn: PropertyContext.(a: A, b: B, c: C) -> Unit) = assertAll(iterations, Gen.default(), Gen.default(), Gen.default(), fn)
inline fun <A, B, C> assertAll(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: PropertyContext.(a: A, b: B, c: C) -> Unit) = assertAll(1000, gena, genb, genc, fn)

inline fun <A, B, C> assertAll(iterations: Int, genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, fn: PropertyContext.(a: A, b: B, c: C) -> Unit) {
  checkIterations(iterations)
  val (context, contextValues) = createContextValues(genA, genB, genC, iterations)
  
  contextValues.forEach { (a, b, c) ->
    testAndShrink(a, b, c, genA, genB, genC, context, fn)
  }
  context.outputInfo()
}

@PublishedApi
internal fun <A, B, C> createContextValues(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, iterations: Int): ContextValues<Triple<A, B, C>> {
  val values = cartesianProduct(genA, genB, genC)
  
  val context = PropertyContext()
  val contextValues = context.collectValues(iterations, values)
  return ContextValues(context, contextValues)
}

@PublishedApi
internal fun <A, B, C> cartesianProduct(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>): Sequence<Triple<A, B, C>> {
  val constants = constantsCartesianProduct(genA, genB, genC)
  val randoms = randomsCartesianProduct(genA, genB, genC)
  
  return constants + randoms
}

private fun <A, B, C> constantsCartesianProduct(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>): Sequence<Triple<A, B, C>> {
  val constants = mutableListOf<Triple<A, B, C>>()
  for (a in genA.constants()) {
    for (b in genB.constants())
      for (c in genC.constants()) {
        constants += Triple(a, b, c)
      }
  }
  return constants.asSequence()
}

private fun <A, B, C> randomsCartesianProduct(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>): Sequence<Triple<A, B, C>> {
  val aSequence = genA.random().iterator()
  val bSequence = genB.random().iterator()
  val cSequence = genC.random().iterator()
  
  return generateInfiniteSequence {
    Triple(aSequence.next(), bSequence.next(), cSequence.next())
  }
}

inline fun <reified A, reified B, reified C, reified D> assertAll(fn: PropertyContext.(a: A, b: B, c: C, D) -> Unit) = assertAll(1000, fn)
inline fun <reified A, reified B, reified C, reified D> assertAll(iterations: Int, fn: PropertyContext.(a: A, b: B, c: C, D) -> Unit) = assertAll(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
inline fun <A, B, C, D> assertAll(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, fn: PropertyContext.(a: A, b: B, c: C, d: D) -> Unit) = assertAll(1000, genA, genB, genC, genD, fn)

inline fun <A, B, C, D> assertAll(iterations: Int, genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, fn: PropertyContext.(a: A, b: B, c: C, d: D) -> Unit) {
  checkIterations(iterations)
  val (context, contextValues) = createContextValues(genA, genB, genC, genD, iterations)
  
  contextValues.forEach { (a, b, c, d) ->
    testAndShrink(a, b, c, d, genA, genB, genC, genD, context, fn)
  }
  
  context.outputInfo()
}

@PublishedApi
internal fun <A, B, C, D> createContextValues(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, iterations: Int): ContextValues<Tuple4<A, B, C, D>> {
  val values = cartesianProduct(genA, genB, genC, genD)
  
  val context = PropertyContext()
  val contextValues = context.collectValues(iterations, values)
  return ContextValues(context, contextValues)
}

@PublishedApi
internal fun <A, B, C, D> cartesianProduct(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>): Sequence<Tuple4<A, B, C, D>> {
  val constants = constantsCartesianProduct(genA, genB, genC, genD)
  val randoms = randomsCartesianProduct(genA, genB, genC, genD)
  return constants + randoms
}

private fun <A, B, C, D> constantsCartesianProduct(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>): Sequence<Tuple4<A, B, C, D>> {
  val constants = mutableListOf<Tuple4<A, B, C, D>>()
  for (a in genA.constants()) {
    for (b in genB.constants()) {
      for (c in genC.constants()) {
        for (d in genD.constants()) {
          constants += Tuple4(a, b, c, d)
        }
      }
    }
  }
  return constants.asSequence()
}

private fun <A, B, C, D> randomsCartesianProduct(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>): Sequence<Tuple4<A, B, C, D>> {
  val aSequence = genA.random().iterator()
  val bSequence = genB.random().iterator()
  val cSequence = genC.random().iterator()
  val dSequence = genD.random().iterator()
  return generateInfiniteSequence {
    Tuple4(aSequence.next(), bSequence.next(), cSequence.next(), dSequence.next())
  }
}

inline fun <reified A, reified B, reified C, reified D, reified E> assertAll(fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E) -> Unit) = assertAll(Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
inline fun <reified A, reified B, reified C, reified D, reified E> assertAll(iterations: Int, fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E) -> Unit) = assertAll(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
inline fun <A, B, C, D, E> assertAll(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E) -> Unit) = assertAll(1000, genA, genB, genC, genD, genE, fn)

inline fun <A, B, C, D, E> assertAll(iterations: Int, genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E) -> Unit) {
  checkIterations(iterations)
  val (context, contextValues) = createContextValues(genA, genB, genC, genD, genE, iterations)
  
  contextValues.forEach { (a, b, c, d, e) ->
    testAndShrink(a, b, c, d, e, genA, genB, genC, genD, genE, context, fn)
  }
  
  context.outputInfo()
}

@PublishedApi
internal fun <A, B, C, D, E> createContextValues(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, iterations: Int): ContextValues<Tuple5<A, B, C, D, E>> {
  val values = cartesianProduct(genA, genB, genC, genD, genE)
  
  val context = PropertyContext()
  val contextValues = context.collectValues(iterations, values)
  return ContextValues(context, contextValues)
}

@PublishedApi
internal fun <A, B, C, D, E> cartesianProduct(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>): Sequence<Tuple5<A, B, C, D, E>> {
  val constants = constantsCartesianProduct(genA, genB, genC, genD, genE)
  val randoms = randomsCartesianProduct(genA, genB, genC, genD, genE)
  
  return constants + randoms
}

private fun <A, B, C, D, E> constantsCartesianProduct(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>): Sequence<Tuple5<A, B, C, D, E>> {
  val constants = mutableListOf<Tuple5<A, B, C, D, E>>()
  for (a in genA.constants()) {
    for (b in genB.constants()) {
      for (c in genC.constants()) {
        for (d in genD.constants()) {
          for (e in genE.constants()) {
            constants += Tuple5(a, b, c, d, e)
          }
        }
      }
    }
  }
  
  return constants.asSequence()
}

private fun <A, B, C, D, E> randomsCartesianProduct(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>): Sequence<Tuple5<A, B, C, D, E>> {
  val aSequence = genA.random().iterator()
  val bSequence = genB.random().iterator()
  val cSequence = genC.random().iterator()
  val dSequence = genD.random().iterator()
  val eSequence = genE.random().iterator()
  
  return generateInfiniteSequence {
    Tuple5(aSequence.next(), bSequence.next(), cSequence.next(), dSequence.next(), eSequence.next())
  }
}

inline fun <reified A, reified B, reified C, reified D, reified E, reified F> assertAll(fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F) -> Unit) =  assertAll(1000, fn)
inline fun <reified A, reified B, reified C, reified D, reified E, reified F> assertAll(iterations: Int, fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F) -> Unit) = assertAll(iterations, Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), Gen.default(), fn)
inline fun <A, B, C, D, E, F> assertAll(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>, fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F) -> Unit) = assertAll(1000, genA, genB, genC, genD, genE, genF, fn)

inline fun <A, B, C, D, E, F> assertAll(iterations: Int, genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>, fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F) -> Unit) {
  checkIterations(iterations)
  
  val (context, contextValues) = createContextValues(genA, genB, genC, genD, genE, genF, iterations)
  
  contextValues.forEach { (a, b, c, d, e, f) ->
    testAndShrink(a, b, c, d, e, f, genA, genB, genC, genD, genE, genF, context, fn)
  }
  context.outputInfo()
}

@PublishedApi
internal fun <A, B, C, D, E, F> createContextValues(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>, iterations: Int): ContextValues<Tuple6<A, B, C, D, E, F>> {
  val values = cartesianProduct(genA, genB, genC, genD, genE, genF)
  val context = PropertyContext()
  
  val contextValues = context.collectValues(iterations, values)
  return ContextValues(context, contextValues)
}


@PublishedApi
internal fun <A, B, C, D, E, F> cartesianProduct(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>): Sequence<Tuple6<A, B, C, D, E, F>> {
  val constants = constantsCartesianProduct(genA, genB, genC, genD, genE, genF)
  val randoms = randomsCartesianProduct(genA, genB, genC, genD, genE, genF)
  
  return constants + randoms
}

private fun <A, B, C, D, E, F> constantsCartesianProduct(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>): Sequence<Tuple6<A, B, C, D, E, F>> {
  val constants = mutableListOf<Tuple6<A, B, C, D, E, F>>()
  for (a in genA.constants()) {
    for (b in genB.constants()) {
      for (c in genC.constants()) {
        for (d in genD.constants()) {
          for (e in genE.constants()) {
            for (f in genF.constants()) {
              constants += Tuple6(a, b, c, d, e, f)
            }
          }
        }
      }
    }
  }
  return constants.asSequence()
}

private fun <A, B, C, D, E, F> randomsCartesianProduct(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>): Sequence<Tuple6<A, B, C, D, E, F>> {
  val aSequence = genA.random().iterator()
  val bSequence = genB.random().iterator()
  val cSequence = genC.random().iterator()
  val dSequence = genD.random().iterator()
  val eSequence = genE.random().iterator()
  val fSequence = genF.random().iterator()
  
  return generateInfiniteSequence {
    Tuple6(aSequence.next(), bSequence.next(), cSequence.next(), dSequence.next(), eSequence.next(), fSequence.next())
  }
}

@PublishedApi
internal fun checkIterations(iterations: Int) {
  if (iterations <= 0) throw IllegalArgumentException("Iterations should be a positive number")
}

@PublishedApi
internal fun PropertyContext.outputInfo() {
  outputValues(this)
  outputClassifications(this)
}

@PublishedApi
internal fun <A> PropertyContext.collectValues(amount: Int, values: Sequence<A>): Sequence<A> = values.take(amount).onEach { addValue(it) }

data class Tuple4<out A, out B, out C, out D>(val a: A, val b: B, val c: C, val d: D) {
  override fun toString(): String {
    return "($a, $b, $c, $d)"
  }
}
data class Tuple5<out A, out B, out C, out D, out E>(val a: A, val b: B, val c: C, val d: D, val e: E) {
  override fun toString(): String {
    return "($a, $b, $c, $d, $e)"
  }
}
data class Tuple6<out A, out B, out C, out D, out E, out F>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F) {
  override fun toString(): String {
    return "($a, $b, $c, $d, $e, $f)"
  }
}

fun <A> Gen<A>.assertAll(iterations: Int = 1000, fn: PropertyContext.(a: A) -> Unit) = assertAll(iterations, this, fn)
fun <A> Gen<A>.assertAll(iterations: Int = 1000, fn: PropertyContext.(a0: A, a1: A) -> Unit) = assertAll(iterations, this, this, fn)
fun <A> Gen<A>.assertAll(iterations: Int = 1000, fn: PropertyContext.(a0: A, a1: A, a2: A) -> Unit) = assertAll(iterations, this, this, this, fn)
fun <A> Gen<A>.assertAll(iterations: Int = 1000, fn: PropertyContext.(a0: A, a1: A, a2: A, a3: A) -> Unit) = assertAll(iterations, this, this, this, this, fn)
fun <A> Gen<A>.assertAll(iterations: Int = 1000, fn: PropertyContext.(a0: A, a1: A, a2: A, a3: A, a4: A) -> Unit) = assertAll(iterations, this, this, this, this, this, fn)
fun <A> Gen<A>.assertAll(iterations: Int = 1000, fn: PropertyContext.(a0: A, a1: A, a2: A, a3: A, a4: A, a5: A) -> Unit) = assertAll(iterations, this, this, this, this, this, this, fn)

data class ContextValues<T>(val context: PropertyContext, val values: Sequence<T>)