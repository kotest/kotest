package io.kotest.inspectors

fun <T> Array<T>.forAll(fn: (T) -> Unit) = apply { forAll(this.asList(), fn) }
fun <T> Collection<T>.forAll(fn: (T) -> Unit) = apply { forAll(this, fn) }
fun <T> Sequence<T>.forAll(fn: (T) -> Unit) = apply { forAll(this.toList(), fn) }

fun <T> Array<T>.forOne(fn: (T) -> Unit) = apply { forOne(this.asList(), fn) }
fun <T> Collection<T>.forOne(fn: (T) -> Unit) = apply { forExactly(1, this, fn) }
fun <T> Sequence<T>.forOne(fn: (T) -> Unit) = apply { forOne(this.toList(), fn) }

fun <T> Array<T>.forExactly(k: Int, fn: (T) -> Unit) = apply { forExactly(k, this.asList(), fn) }
fun <T> Collection<T>.forExactly(k: Int, fn: (T) -> Unit) = apply { forExactly(k, this, fn) }
fun <T> Sequence<T>.forExactly(k: Int, fn: (T) -> Unit) = apply { forExactly(k, this.toList(), fn) }

fun <T> Array<T>.forSome(f: (T) -> Unit) = apply { forSome(this.asList(), f) }
fun <T> Collection<T>.forSome(fn: (T) -> Unit) = apply { forSome(this, fn) }
fun <T> Sequence<T>.forSome(fn: (T) -> Unit) = apply { forSome(this.toList(), fn) }

fun <T> Array<T>.forAny(f: (T) -> Unit) = apply { forAny(this, f) }
fun <T> Collection<T>.forAny(f: (T) -> Unit) = apply { forAny(this, f) }
fun <T> Sequence<T>.forAny(fn: (T) -> Unit) = apply { forAny(this.toList(), fn) }

fun <T> Array<T>.forAtLeastOne(f: (T) -> Unit) = apply { forAtLeastOne(this.asList(), f) }
fun <T> Collection<T>.forAtLeastOne(f: (T) -> Unit) = apply { forAtLeastOne(this, f) }
fun <T> Sequence<T>.forAtLeastOne(fn: (T) -> Unit) = apply { forAtLeastOne(this.toList(), fn) }

fun <T> Array<T>.forAtLeast(k: Int, f: (T) -> Unit) = apply { forAtLeast(k, this.asList(), f) }
fun <T> Collection<T>.forAtLeast(k: Int, fn: (T) -> Unit) = apply { forAtLeast(k, this, fn) }
fun <T> Sequence<T>.forAtLeast(k: Int, fn: (T) -> Unit) = apply { forAtLeast(k, this.toList(), fn) }

fun <T> Array<T>.forAtMostOne(f: (T) -> Unit) = apply { forAtMost(1, this.asList(), f) }
fun <T> Collection<T>.forAtMostOne(f: (T) -> Unit) = apply { forAtMost(1, this, f) }
fun <T> Sequence<T>.forAtMostOne(fn: (T) -> Unit) = apply { forAtMostOne(this.toList(), fn) }

fun <T> Array<T>.forAtMost(k: Int, f: (T) -> Unit) = apply { forAtMost(k, this.asList(), f) }
fun <T> Collection<T>.forAtMost(k: Int, fn: (T) -> Unit) = apply { forAtMost(k, this, fn) }
fun <T> Sequence<T>.forAtMost(k: Int, fn: (T) -> Unit) = apply { forAtMost(k, this.toList(), fn) }

fun <T> Array<T>.forNone(f: (T) -> Unit) = apply { forNone(this.asList(), f) }
fun <T> Collection<T>.forNone(f: (T) -> Unit) = apply { forNone(this, f) }
fun <T> Sequence<T>.forNone(fn: (T) -> Unit) = apply { forNone(this.toList(), fn) }
