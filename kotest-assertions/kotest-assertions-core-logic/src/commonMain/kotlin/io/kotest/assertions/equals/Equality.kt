package io.kotest.assertions.equals

interface Equality<T: Any?> {
   fun name(): String

   fun verify(actual: T, expected: T) : EqualityResult

   companion object {
      fun <T> default() = byObjectEquality<T>()
   }
}
