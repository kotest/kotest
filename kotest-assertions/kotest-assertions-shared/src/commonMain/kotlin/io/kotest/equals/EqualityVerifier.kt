package io.kotest.equals

interface EqualityVerifier<T: Any?> {
   fun name(): String

   fun verify(actual: T, expected: T) : EqualityResult
}
