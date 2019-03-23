package io.kotlintest.assertions.arrow.nel

import arrow.data.NonEmptyList

fun <T> NonEmptyList<T>.forAll(fn: (T) -> Unit) = io.kotlintest.forAll(this.all, fn)

fun <T> NonEmptyList<T>.forOne(fn: (T) -> Unit) = io.kotlintest.forExactly(1, this.all, fn)

fun <T> NonEmptyList<T>.forExactly(k: Int, fn: (T) -> Unit) = io.kotlintest.forExactly(k, this.all, fn)

fun <T> NonEmptyList<T>.forSome(fn: (T) -> Unit) = io.kotlintest.forSome(this.all, fn)

fun <T> NonEmptyList<T>.forAny(f: (T) -> Unit) = io.kotlintest.forAny(this.all, f)

fun <T> NonEmptyList<T>.forAtLeastOne(f: (T) -> Unit) = io.kotlintest.forAtLeastOne(this.all, f)

fun <T> NonEmptyList<T>.forAtLeast(k: Int, fn: (T) -> Unit) = io.kotlintest.forAtLeast(k, this.all, fn)

fun <T> NonEmptyList<T>.forAtMostOne(f: (T) -> Unit) = io.kotlintest.forAtMost(1, this.all, f)

fun <T> NonEmptyList<T>.forAtMost(k: Int, fn: (T) -> Unit) = io.kotlintest.forAtMost(k, this.all, fn)

fun <T> NonEmptyList<T>.forNone(f: (T) -> Unit) = io.kotlintest.forNone(this.all, f)