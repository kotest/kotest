package io.kotest.equals

interface EqualityVerifier<T: Any?> {
   fun name(): String

   fun areEqual(actual: T, expected: T) : EqualityResult
}
