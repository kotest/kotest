package io.kotlintest

import io.kotlintest.assertions.Failures

fun fail(msg: String): Nothing = throw Failures.failure(msg)
