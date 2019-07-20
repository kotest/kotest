@file:Suppress("DEPRECATION")

package io.kotlintest.matchers

import io.kotlintest.*

/**
 * Add [clue] as additional info to the assertion error message in case an assertion fails.
 * Can be nested, the error message will contain all available clues.
 *
 * @param thunk the code with assertions to be executed
 * @return the return value of the supplied [thunk]
 */
fun <ReturnType> withClue(clue: Any, thunk: () -> ReturnType): ReturnType {
    return clue.asClue { thunk() }
}

/**
 * Similar to `let`, but will add `this` as a clue to the assertion error message in case an assertion fails.
 * Can be nested, the error message will contain all available clues.
 *
 * @param block the code with assertions to be executed
 * @return the return value of the supplied [block]
 */
fun <ClueType, ReturnType> ClueType.asClue(block: (ClueType) -> ReturnType): ReturnType {
  try {
    ErrorCollector.clueContext.get().push(this)
    return block(this)
  } finally {
    ErrorCollector.clueContext.get().pop()
  }
}

fun Any.shouldHaveSameHashCodeAs(other: Any) = this should haveSameHashCodeAs(other)
fun Any.shouldNotHaveSameHashCodeAs(other: Any) = this shouldNot haveSameHashCodeAs(other)

fun haveSameHashCodeAs(other: Any) = object : Matcher<Any> {
  override fun test(value: Any): MatcherResult {
    return MatcherResult(
        value.hashCode() == other.hashCode(),
        "Value $value should have hash code ${other.hashCode()}",
        "Value $value should not have hash code ${other.hashCode()}"
    )
  }
}
