package io.kotlintest.runner.jvm

import arrow.core.Try

fun <T> Try<T>.onf(f: (Throwable) -> Unit) {
  this.fold(f, { Try.just(it) })
}

fun <T> Try<T>.ons(f: (T) -> Unit) {
  this.fold({ Unit }, { f(it) })
}