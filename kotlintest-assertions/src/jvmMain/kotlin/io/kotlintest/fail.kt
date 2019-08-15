package io.kotlintest

fun fail(msg: String): Nothing = throw Failures.failure(msg)
