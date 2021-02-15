package io.kotest.assertions

typealias SuspendingProducer<T> = suspend () -> T

/** @return empty string when count is one, otherwise a single 's'. */
fun plural_s(count: Int) = if(count == 1) "" else "s"

