package io.kotest.runner.jvm

import arrow.core.Try

fun <T> Try<T>.onFailure(f: (Throwable) -> Unit) {
  this.fold(f) { Try.just(it) }
}

fun <T> Try<T>.onSuccess(f: (T) -> Unit) {
  this.fold({ Unit }, { f(it) })
}