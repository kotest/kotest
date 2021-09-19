package io.kotest.assertions

import kotlinx.coroutines.TimeoutCancellationException

/**
 * Add [clue] as additional info to the assertion error message in case an assertion fails.
 * Can be nested, the error message will contain all available clues.
 *
 * @param thunk the code with assertions to be executed
 * @return the return value of the supplied [thunk]
 */
inline fun <R> withClue(clue: Any?, thunk: () -> R): R {
   return clue.asClue { thunk() }
}

/**
 * Similar to [withClue] but accepts a lazy in the case that a clue is expensive or is only valid when an assertion fails.
 * Can be nested, the error message will contain all available clues.
 *
 * @param thunk the code with assertions to be executed
 * @return the return value of the supplied [thunk]
 */
inline fun <R> withClue(clue: Lazy<Any?>, thunk: () -> R): R {
   try {
      errorCollector.pushClue { clue.value.toString() }
      return thunk()
   // this is a special control exception used by coroutines
   } catch (t: TimeoutCancellationException) {
      throw Exceptions.createAssertionError(clueContextAsString() + (t.message ?: ""), t)
   } finally {
      errorCollector.popClue()
   }
}

/**
 * Similar to `withClue`, but will add `this` as a clue to the assertion error message in case an assertion fails.
 * Can be nested, the error message will contain all available clues.
 *
 * @param block the code with assertions to be executed
 * @return the return value of the supplied [block]
 */
inline fun <T : Any?, R> T.asClue(block: (T) -> R): R = withClue(lazy { this.toString() }) { block(this) }

inline fun <T : Any?> Iterable<T>.forEachAsClue(action: (T) -> Unit) = forEach { element ->
   element.asClue {
      action(it)
   }
}
