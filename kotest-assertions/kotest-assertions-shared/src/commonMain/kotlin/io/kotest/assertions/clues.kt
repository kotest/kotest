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
inline fun <R> withClue(crossinline clue: () -> Any?, thunk: () -> R): R {
   val collector = errorCollector
   try {
      collector.pushClue { clue.invoke().toString() }
      return thunk()
      // this is a special control exception used by coroutines
   } catch (t: TimeoutCancellationException) {
      throw Exceptions.createAssertionError(clueContextAsString() + (t.message ?: ""), t)
   } catch (e: Exception) {
      throw Exceptions.createAssertionError(clueContextAsString() + (e.message ?: ""), e)
   } finally {
      collector.popClue()
   }
}

/**
 * Similar to `withClue`, but will add `this` as a clue to the assertion error message in case an assertion fails.
 * Can be nested, the error message will contain all available clues.
 * `Lazy` and `Function0` are treated as lazy clues, so they will be evaluated only if an assertion fails.
 *
 * @param block the code with assertions to be executed
 * @return the return value of the supplied [block]
 */
@Suppress("USELESS_CAST")
inline fun <T : Any?, R> T.asClue(block: (T) -> R): R =
   withClue(
      // The cast is needed to avoid calling withClue(Any)
      when (this) {
         is Lazy<*> -> ({ value })
         is Function0<*> -> ({ invoke() })
         else -> ({ this })
      } as () -> Any?,
   ) { block(this) }

inline fun <T : Any?> Iterable<T>.forEachAsClue(action: (T) -> Unit) = forEach { element ->
   element.asClue {
      action(it)
   }
}
