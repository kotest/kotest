package io.kotest.equals

import io.kotest.equals.types.objectEquality

object EqualityVerifiers {
   fun <T> default() = objectEquality<T>()
}

