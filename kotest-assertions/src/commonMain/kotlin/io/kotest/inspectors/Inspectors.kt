package io.kotest.inspectors

fun <T> Array<T>.forAll(fn: (T) -> Unit) = io.kotest.forAll(this.asList(), fn)
fun <T> Collection<T>.forAll(fn: (T) -> Unit) = io.kotest.forAll(this, fn)
fun <T> Sequence<T>.forAll(fn: (T) -> Unit) = io.kotest.forAll(this.toList(), fn)

fun <T> Array<T>.forOne(fn: (T) -> Unit) = io.kotest.forOne(this.asList(), fn)
fun <T> Collection<T>.forOne(fn: (T) -> Unit) = io.kotest.forExactly(1, this, fn)
fun <T> Sequence<T>.forOne(fn: (T) -> Unit) = io.kotest.forOne(this.toList(), fn)

fun <T> Array<T>.forExactly(k: Int, fn: (T) -> Unit) = io.kotest.forExactly(k, this.asList(), fn)
fun <T> Collection<T>.forExactly(k: Int, fn: (T) -> Unit) = io.kotest.forExactly(k, this, fn)
fun <T> Sequence<T>.forExactly(k: Int, fn: (T) -> Unit) = io.kotest.forExactly(k, this.toList(), fn)

fun <T> Array<T>.forSome(f: (T) -> Unit) = io.kotest.forSome(this.asList(), f)
fun <T> Collection<T>.forSome(fn: (T) -> Unit) = io.kotest.forSome(this, fn)
fun <T> Sequence<T>.forSome(fn: (T) -> Unit) = io.kotest.forSome(this.toList(), fn)

fun <T> Array<T>.forAny(f: (T) -> Unit) = io.kotest.forAny(this, f)
fun <T> Collection<T>.forAny(f: (T) -> Unit) = io.kotest.forAny(this, f)
fun <T> Sequence<T>.forAny(fn: (T) -> Unit) = io.kotest.forAny(this.toList(), fn)

fun <T> Array<T>.forAtLeastOne(f: (T) -> Unit) = io.kotest.forAtLeastOne(this.asList(), f)
fun <T> Collection<T>.forAtLeastOne(f: (T) -> Unit) = io.kotest.forAtLeastOne(this, f)
fun <T> Sequence<T>.forAtLeastOne(fn: (T) -> Unit) = io.kotest.forAtLeastOne(this.toList(), fn)

fun <T> Array<T>.forAtLeast(k: Int, f: (T) -> Unit) = io.kotest.forAtLeast(k, this.asList(), f)
fun <T> Collection<T>.forAtLeast(k: Int, fn: (T) -> Unit) = io.kotest.forAtLeast(k, this, fn)
fun <T> Sequence<T>.forAtLeast(k: Int, fn: (T) -> Unit) = io.kotest.forAtLeast(k, this.toList(), fn)

fun <T> Array<T>.forAtMostOne(f: (T) -> Unit) = io.kotest.forAtMost(1, this.asList(), f)
fun <T> Collection<T>.forAtMostOne(f: (T) -> Unit) = io.kotest.forAtMost(1, this, f)
fun <T> Sequence<T>.forAtMostOne(fn: (T) -> Unit) = io.kotest.forAtMostOne(this.toList(), fn)

fun <T> Array<T>.forAtMost(k: Int, f: (T) -> Unit) = io.kotest.forAtMost(k, this.asList(), f)
fun <T> Collection<T>.forAtMost(k: Int, fn: (T) -> Unit) = io.kotest.forAtMost(k, this, fn)
fun <T> Sequence<T>.forAtMost(k: Int, fn: (T) -> Unit) = io.kotest.forAtMost(k, this.toList(), fn)

fun <T> Array<T>.forNone(f: (T) -> Unit) = io.kotest.forNone(this.asList(), f)
fun <T> Collection<T>.forNone(f: (T) -> Unit) = io.kotest.forNone(this, f)
fun <T> Sequence<T>.forNone(fn: (T) -> Unit) = io.kotest.forNone(this.toList(), fn)
