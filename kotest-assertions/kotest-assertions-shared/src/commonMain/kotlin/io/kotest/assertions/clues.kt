package io.kotest.assertions

/**
 * Add [clue] as additional info to the assertion error message in case an assertion fails.
 * Can be nested, the error message will contain all available clues.
 *
 * @param thunk the code with assertions to be executed
 * @return the return value of the supplied [thunk]
 */
inline fun <R> withClue(clue: Any, thunk: () -> R): R {
   return clue.asClue { thunk() }
}

/**
 * Similar to `withClue`, but will add `this` as a clue to the assertion error message in case an assertion fails.
 * Can be nested, the error message will contain all available clues.
 *
 * @param block the code with assertions to be executed
 * @return the return value of the supplied [block]
 */
inline fun <T : Any, R> T.asClue(block: (T) -> R): R {
   try {
      errorCollector.pushClue(this)
      return block(this)
   } finally {
      errorCollector.popClue()
   }
}

inline fun <T : Any> Iterable<T>.forEachAsClue(action: (T) -> Unit) = forEach { element ->
   element.asClue {
      action(it)
   }
}
