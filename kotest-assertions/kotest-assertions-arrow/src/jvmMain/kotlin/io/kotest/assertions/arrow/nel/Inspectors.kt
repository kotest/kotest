package io.kotest.assertions.arrow.nel

import arrow.core.NonEmptyList

fun <T> NonEmptyList<T>.forAll(fn: (T) -> Unit) = io.kotest.inspectors.forAll(this.all, fn)

fun <T> NonEmptyList<T>.forOne(fn: (T) -> Unit) = io.kotest.inspectors.forExactly(1, this.all, fn)

fun <T> NonEmptyList<T>.forExactly(k: Int, fn: (T) -> Unit) = io.kotest.inspectors.forExactly(k, this.all, fn)

fun <T> NonEmptyList<T>.forSome(fn: (T) -> Unit) = io.kotest.inspectors.forSome(this.all, fn)

fun <T> NonEmptyList<T>.forAny(f: (T) -> Unit) = io.kotest.inspectors.forAny(this.all, f)

fun <T> NonEmptyList<T>.forAtLeastOne(f: (T) -> Unit) = io.kotest.inspectors.forAtLeastOne(this.all, f)

fun <T> NonEmptyList<T>.forAtLeast(k: Int, fn: (T) -> Unit) = io.kotest.inspectors.forAtLeast(k, this.all, fn)

fun <T> NonEmptyList<T>.forAtMostOne(f: (T) -> Unit) = io.kotest.inspectors.forAtMost(1, this.all, f)

fun <T> NonEmptyList<T>.forAtMost(k: Int, fn: (T) -> Unit) = io.kotest.inspectors.forAtMost(k, this.all, fn)

fun <T> NonEmptyList<T>.forNone(f: (T) -> Unit) = io.kotest.inspectors.forNone(this.all, f)
