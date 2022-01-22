package io.kotest.comparators

object Comparators {
   fun <T> default() = EqualityComparator<T>()
}

