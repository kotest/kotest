package com.sksamuel.kotlintest.specs

fun attemptToFail(): Nothing = throw RuntimeException("This shouldn't execute as this test should be ignored!")