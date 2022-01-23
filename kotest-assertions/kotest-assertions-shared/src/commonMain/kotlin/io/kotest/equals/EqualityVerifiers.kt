package io.kotest.equals

import io.kotest.equals.types.ObjectEqualsEqualityVerifier

object EqualityVerifiers {
   fun <T> default() = ObjectEqualsEqualityVerifier<T>()
}

