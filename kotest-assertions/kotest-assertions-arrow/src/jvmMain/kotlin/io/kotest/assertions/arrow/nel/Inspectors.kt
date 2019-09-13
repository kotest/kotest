package io.kotest.assertions.arrow.nel

import arrow.data.NonEmptyList

fun <T> NonEmptyList<T>.forAll(fn: (T) -> Unit) = io.kotest.forAll(this.all, fn)

fun <T> NonEmptyList<T>.forOne(fn: (T) -> Unit) = io.kotest.forExactly(1, this.all, fn)

fun <T> NonEmptyList<T>.forExactly(k: Int, fn: (T) -> Unit) = io.kotest.forExactly(k, this.all, fn)

fun <T> NonEmptyList<T>.forSome(fn: (T) -> Unit) = io.kotest.forSome(this.all, fn)

fun <T> NonEmptyList<T>.forAny(f: (T) -> Unit) = io.kotest.forAny(this.all, f)

fun <T> NonEmptyList<T>.forAtLeastOne(f: (T) -> Unit) = io.kotest.forAtLeastOne(this.all, f)

fun <T> NonEmptyList<T>.forAtLeast(k: Int, fn: (T) -> Unit) = io.kotest.forAtLeast(k, this.all, fn)

fun <T> NonEmptyList<T>.forAtMostOne(f: (T) -> Unit) = io.kotest.forAtMost(1, this.all, f)

fun <T> NonEmptyList<T>.forAtMost(k: Int, fn: (T) -> Unit) = io.kotest.forAtMost(k, this.all, fn)

fun <T> NonEmptyList<T>.forNone(f: (T) -> Unit) = io.kotest.forNone(this.all, f)