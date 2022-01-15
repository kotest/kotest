package io.kotest.comparators

import io.kotest.matchers.MatcherResult

interface Comparator<T> {
   fun matches(actual: T, expected: T) : MatcherResult
}
