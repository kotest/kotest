package io.kotest.assertions.arrow.nel

import arrow.core.NonEmptyList
import io.kotest.inspectors.*

fun <T> NonEmptyList<T>.forAll(fn: (T) -> Unit) = all.forAll(fn)

fun <T> NonEmptyList<T>.forOne(fn: (T) -> Unit) = all.forOne(fn)

fun <T> NonEmptyList<T>.forExactly(k: Int, fn: (T) -> Unit) = all.forExactly(k, fn)

fun <T> NonEmptyList<T>.forSome(fn: (T) -> Unit) = all.forSome(fn)

fun <T> NonEmptyList<T>.forAny(f: (T) -> Unit) = all.forAny(f)

fun <T> NonEmptyList<T>.forAtLeastOne(f: (T) -> Unit) = all.forAtLeastOne(f)

fun <T> NonEmptyList<T>.forAtLeast(k: Int, fn: (T) -> Unit) = all.forAtLeast(k, fn)

fun <T> NonEmptyList<T>.forAtMostOne(f: (T) -> Unit) = all.forAtMostOne(f)

fun <T> NonEmptyList<T>.forAtMost(k: Int, fn: (T) -> Unit) = all.forAtMost(k, fn)

fun <T> NonEmptyList<T>.forNone(f: (T) -> Unit) = all.forNone(f)
